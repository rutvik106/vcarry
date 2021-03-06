package fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import extra.LocaleHelper;
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
    public boolean isReady = false;
    LinearLayout llDashboardContainer;
    TextView tvDashCustomerName, tvDashCustomerContact, tvDashTripTo, tvDashTripFrom, tvDashTripFromCompany,
            tvDashTripToCompany;
    Button btnDashStopTrip, btnDashCancelTrip;
    OnTripStopListener onTripStopListener;
    private SyncedMapFragment mapFragment;
    private GoogleMap mMap;
    private LatLngInterpolator mLatLngInterpolator;
    private Context context;
    private Marker currentLocationMarker;
    private LatLng currentLatLng;
    private TripDetails tripDetails;
    private FloatingActionButton fabStartNavigation;

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

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore)
    {

        if (tv.getTag() == null)
        {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout()
            {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0)
                {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine)
                {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else
                {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore)
    {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText))
        {
            ssb.setSpan(new ClickableSpan()
            {

                @Override
                public void onClick(View widget)
                {

                    if (viewMore)
                    {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else
                    {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 2, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

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

        tvDashTripFromCompany = (TextView) view.findViewById(R.id.tv_dashTripFromCompany);
        tvDashTripToCompany = (TextView) view.findViewById(R.id.tv_dashTripToCompany);

        btnDashStopTrip = (Button) view.findViewById(R.id.btn_dashStopTrip);
        btnDashCancelTrip = (Button) view.findViewById(R.id.btn_dashCancelTrip);

        fabStartNavigation = (FloatingActionButton) view.findViewById(R.id.fab_startNavigation);

        fabStartNavigation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);
                if (!isOnTrip)
                {
                    Toast.makeText(getActivity(), R.string.not_on_trip, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tripDetails != null)
                {
                    if (tripDetails.getToLatLong() != null)
                    {
                        if (!tripDetails.getToLatLong().isEmpty())
                        {
                            final String[] stringLatLng = tripDetails.getToLatLong().split(",");
                            String uri = String
                                    .format(Locale.ENGLISH,
                                            "http://maps.google.com/maps?daddr=%s,%s (%s)",
                                            stringLatLng[0], stringLatLng[1], tripDetails.getToShippingLocation());
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse(uri));
                            //Uri.parse("google.navigation:q=" + tripDetails.getToLatLong() + "&mode=d"));
                                    /*Uri.parse("http://maps.google.com/maps?daddr=" +
                                            stringLatLng[0] + "," + stringLatLng[1]));*/
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setPackage("com.google.android.apps.maps");
                            intent.setClassName("com.google.android.apps.maps",
                                    "com.google.android.maps.MapsActivity");
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                            {
                                startActivity(intent);
                            }

                        } else
                        {
                            Toast.makeText(getActivity(), R.string.destination_not_available, Toast.LENGTH_SHORT).show();
                        }
                    } else
                    {
                        Toast.makeText(getActivity(), R.string.destination_not_available, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loadMapNow();

        checkIfDriverOnTrip();

        return view;
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

        if (tripDetails != null)
        {
            if (tripDetails.getToLatLong() != null)
            {
                if (!tripDetails.getToLatLong().isEmpty())
                {
                    final String[] stringLatLng = tripDetails.getToLatLong().split(",");
                    final LatLng latLng = new LatLng(Double.parseDouble(stringLatLng[0]),
                            Double.parseDouble(stringLatLng[1]));
                    MarkerOptions marker = new MarkerOptions().position(latLng)
                            .title(tripDetails.getToShippingLocation());

                    mMap.clear();
                    mMap.addMarker(marker);

                    CameraUpdate c = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                    mMap.animateCamera(c);
                }
            }
        }

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
    }

    @Override
    public void onCameraIdle()
    {

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

                final Realm realm = Realm.getDefaultInstance();


                tripDetails = realm
                        .where(TripDetails.class).equalTo("tripId", tripId).findFirst();


                if (tripDetails != null)
                {
                    llDashboardContainer.setVisibility(View.VISIBLE);
                    if (tripDetails.getToContactNo() != null)
                    {
                        tvDashCustomerContact.setText(tripDetails.getToContactNo());
                        tvDashCustomerContact.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(Intent.ACTION_CALL,
                                        Uri.parse("tel:" + tripDetails.getToContactNo()));
                                startActivity(intent);
                            }
                        });
                    } else
                    {
                        tvDashCustomerContact.setVisibility(View.GONE);
                    }
                    if (LocaleHelper.getLanguage(getActivity()).equalsIgnoreCase("gu"))
                    {
                        tvDashTripTo.setText(tripDetails.getToGujaratiAddress());
                        tvDashTripFrom.setText(tripDetails.getFromGujaratiAddress());
                        tvDashTripFromCompany.setText(tripDetails.getFromGujaratiName());
                        tvDashTripToCompany.setText(tripDetails.getToGujaratiName());
                    } else
                    {
                        tvDashTripTo.setText(tripDetails.getToShippingLocation());
                        tvDashTripFrom.setText(tripDetails.getFromShippingLocation());
                        tvDashTripFromCompany.setText(tripDetails.getFromCompanyName());
                        tvDashTripToCompany.setText(tripDetails.getToCompanyName());
                    }
                    if (tripDetails.getFromContactNo() != null)
                    {
                        tvDashCustomerName.setText(tripDetails.getFromContactNo());
                        tvDashCustomerName.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intent = new Intent(Intent.ACTION_CALL,
                                        Uri.parse("tel:" + tripDetails.getFromContactNo()));
                                startActivity(intent);
                            }
                        });
                    } else
                    {
                        tvDashCustomerName.setVisibility(View.GONE);
                    }

                    makeTextViewResizable(tvDashTripFrom, 2, "View More", true);

                    makeTextViewResizable(tvDashTripTo, 2, "View More", true);

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

                    btnDashCancelTrip.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getResources().getString(R.string.cancel_trip))
                                    .setMessage(R.string.trip_cancel_msg)
                                    .setPositiveButton(getResources().getString(R.string.cancel_trip), new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            cancelTrip(tripId);
                                        }
                                    }).setNegativeButton(getResources().getString(R.string.cancel), null)
                                    .show();
                        }
                    });

                    fabStartNavigation.setVisibility(View.VISIBLE);

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
                    mMap.clear();
                    onTripStopListener.onTripStop(tripId);
                    fabStartNavigation.setVisibility(View.GONE);
                }

            }

        });
    }

    private void cancelTrip(final String tripId)
    {
        API.getInstance().updateTripStatus(Constants.TRIP_STATUS_CANCELLED_BY_DRIVER,
                tripId, new RetrofitCallbacks<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        super.onResponse(call, response);

                        if (response.isSuccessful())
                        {
                            llDashboardContainer.setVisibility(View.GONE);
                            mMap.clear();
                            onTripStopListener.onTripCancel(tripId);
                            fabStartNavigation.setVisibility(View.GONE);
                        }

                    }

                });

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


    public interface OnTripStopListener
    {
        void onTripStop(String tripId);

        void onTripCancel(String tripId);
    }

}
