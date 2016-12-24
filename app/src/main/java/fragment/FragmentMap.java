package fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.Constants;
import io.fusionbit.vcarry.R;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;
import static java.lang.StrictMath.toDegrees;

/**
 * Created by rutvik on 9/15/2016 at 1:48 PM.
 */

public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnCameraIdleListener
{
    private static final String TAG = App.APP_TAG + FragmentMap.class.getSimpleName();

    private SyncedMapFragment mapFragment;

    private GoogleMap mMap;

    private LatLngInterpolator mLatLngInterpolator;

    public boolean isReady = false;

    private Context context;

    private Marker currentLocationMarker;

    private LatLng currentLatLng;

    LinearLayout llDashboardContainer;

    TextView tvDashCustomerName, tvDashCustomerContact, tvDashTripTo, tvDashTripFrom;

    Button btnDashStopTrip;

    OnTripStopListener onTripStopListener;

    public static FragmentMap newInstance(int index, Context context, OnTripStopListener onTripStopListener)
    {
        FragmentMap fragmentMap = new FragmentMap();
        fragmentMap.context = context;
        fragmentMap.onTripStopListener = onTripStopListener;
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragmentMap.setArguments(b);
        return fragmentMap;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        mLatLngInterpolator = new LatLngInterpolator.Linear();

        mapFragment = (SyncedMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.frag_map);

        llDashboardContainer = (LinearLayout) view.findViewById(R.id.ll_dashboardContainer);

        tvDashCustomerContact = (TextView) view.findViewById(R.id.tv_dashCustomerContact);
        tvDashCustomerName = (TextView) view.findViewById(R.id.tv_dashCustomerName);
        tvDashTripFrom = (TextView) view.findViewById(R.id.tv_dashTripFrom);
        tvDashTripTo = (TextView) view.findViewById(R.id.tv_dashTripTo);

        btnDashStopTrip = (Button) view.findViewById(R.id.btn_dashStopTrip);

        loadMapNow();

        checkIfDriverOnTrip();

        return view;
    }

    public void placeCurrentLocationMarker(final LatLng latLng)
    {
        if (isReady)
        {

            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Shop Name"));

            currentLatLng = latLng;

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


        }
    }


    private void loadMapNow()
    {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        isReady = true;

        mMap = googleMap;

        if (ActivityCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        LatLng gujarat = new LatLng(23.012068, 72.5789153);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gujarat, 11));

        mMap.setOnCameraChangeListener(this);

        mMap.setOnCameraIdleListener(this);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
        {
            @Override
            public void onMyLocationChange(Location location)
            {
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            }
        });

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
    }

    @Override
    public void onCameraIdle()
    {

    }


    public interface LatLngInterpolator
    {
        public LatLng interpolate(float fraction, LatLng a, LatLng b);

        public class Linear implements LatLngInterpolator
        {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b)
            {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lng = (b.longitude - a.longitude) * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }

        public class LinearFixed implements LatLngInterpolator
        {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b)
            {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;

                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180)
                {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }

        public class Spherical implements LatLngInterpolator
        {

            /* From github.com/googlemaps/android-maps-utils */
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to)
            {
                // http://en.wikipedia.org/wiki/Slerp
                double fromLat = toRadians(from.latitude);
                double fromLng = toRadians(from.longitude);
                double toLat = toRadians(to.latitude);
                double toLng = toRadians(to.longitude);
                double cosFromLat = cos(fromLat);
                double cosToLat = cos(toLat);

                // Computes Spherical interpolation coefficients.
                double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
                double sinAngle = sin(angle);
                if (sinAngle < 1E-6)
                {
                    return from;
                }
                double a = sin((1 - fraction) * angle) / sinAngle;
                double b = sin(fraction * angle) / sinAngle;

                // Converts from polar to vector and interpolate.
                double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
                double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
                double z = a * sin(fromLat) + b * sin(toLat);

                // Converts interpolated vector back to polar.
                double lat = atan2(z, sqrt(x * x + y * y));
                double lng = atan2(y, x);
                return new LatLng(toDegrees(lat), toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng)
            {
                // Haversine's formula
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                        cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
            }
        }

    }

    public void checkIfDriverOnTrip()
    {

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        final boolean isOnTrip = pref
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);

        if (isOnTrip)
        {
            final String tripId = pref.getString(Constants.CURRENT_TRIP_ID, "");

            if (!tripId.isEmpty())
            {

                final TripDetails tripDetails = Realm.getDefaultInstance()
                        .where(TripDetails.class).equalTo("tripId", tripId).findFirst();

                if (tripDetails != null)
                {
                    llDashboardContainer.setVisibility(View.VISIBLE);
                    tvDashCustomerContact.setText(tripDetails.getCustomerId());
                    tvDashTripTo.setText(tripDetails.getToShippingLocation());
                    tvDashTripFrom.setText(tripDetails.getFromShippingLocation());
                    tvDashCustomerName.setText(tripDetails.getCustomerName());

                    btnDashStopTrip.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getResources().getString(R.string.stop_trip))
                                    .setMessage(getResources().getString(R.string.are_you_sure_stop_trip))
                                    .setPositiveButton(getResources().getString(R.string.stop), new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            stopTrip(tripId);
                                        }
                                    }).setNegativeButton(getResources().getString(R.string.cancel), null)
                                    .show();


                        }
                    });

                }

            }
        }

    }


    private void stopTrip(final String tripId)
    {
        API.getInstance().updateTripStatus(Constants.TRIP_STATUS_FINISHED, tripId, new RetrofitCallbacks<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                super.onResponse(call, response);

                if (response.isSuccessful())
                {

                    llDashboardContainer.setVisibility(View.GONE);

                    onTripStopListener.onStripStop(tripId);

                }

            }

        });
    }

    public interface OnTripStopListener
    {
        void onStripStop(String tripId);
    }

}
