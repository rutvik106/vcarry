package io.fusionbit.vcarry;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by rutvik on 9/26/2016 at 12:19 PM.
 */

public class FusedLocation implements LocationListener
{

    private static final String TAG = "LOC " + FusedLocation.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 8000; //2000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000; //UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context mContext;
    private GetLocation mGetCurrentLocation;

    GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;

    public enum Status
    {
        UNAVAILABLE,
        UPDATEREQUIRE,
        AVAILABLE
    }

    public interface GetLocation
    {
        void onLocationChanged(Location location);
    }

    public FusedLocation(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener)
    {
        mContext = context;
        this.connectionCallbacks = connectionCallbacks;
        this.onConnectionFailedListener = onConnectionFailedListener;
        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient()
    {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        connect();
    }


    public Location getLastKnownLocation(final Context context)
    {


        Log.i(TAG, "GETTING LAST KNOWN LOCATION");
        int coarse = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fine = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (coarse == PackageManager.PERMISSION_GRANTED && fine == PackageManager.PERMISSION_GRANTED)
        {

            Log.i(TAG, "LOCATION PERMISSION GRANTED!!!");
            Log.i(TAG, "TRYING TO GET LOCATION FROM FUSED API!!!");
            final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null)
            {
                Log.i(TAG, "GOT LAST KNOWN LOCATION FROM FUSED API");
                return location;
            }
            /*else
            {
                Log.i(TAG, "FAILED TO GET LOCATION FROM FUSED API");
                Log.i(TAG, "TRYING TO GET LOCATION FROM LOCATION MANAGER");
                return getLastKnownLocationFromLocationManager(context);
            }*/
        }
        Log.i(TAG, "RETURNING NULL LOCATION :(");
        return null;
    }


    public void startGettingLocation(GetLocation location)
    {
        mGetCurrentLocation = location;
        startLocationUpdates();
    }

    public void stopGettingLocation() throws IllegalStateException
    {
        stopLocationUpdates();
        disconnect();
    }


    @Override
    public void onLocationChanged(Location location)
    {
        mGetCurrentLocation.onLocationChanged(location);
    }


    public Status isGooglePlayServiceAvailable(Context context)
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (status == ConnectionResult.SUCCESS)
        {
            return Status.AVAILABLE;
        } else if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)
        {
            return Status.UPDATEREQUIRE;
        } else
        {
            return Status.UNAVAILABLE;
        }
    }


    private void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public Location getLastKnownLocationFromLocationManager(final Context context)
    {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        Log.i(TAG, "TOTAL LOCATION PROVIDERS: " + providers.size());
        for (String provider : providers)
        {
            int coarse = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fine = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (coarse == PackageManager.PERMISSION_GRANTED && fine == PackageManager.PERMISSION_GRANTED)
            {
                Location l = mLocationManager.getLastKnownLocation(provider);

                if (l == null)
                {
                    Log.i(TAG, "FOR NULL LOCATION FROM: " + provider);
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
                {
                    Log.i(TAG, "GOT LOCATION FROM: " + provider);
                    //Log.i(TAG, "LOCATION ACCURACY: " + bestLocation.getAccuracy());
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }


    private void startLocationUpdates()
    {
        if (mGoogleApiClient.isConnected())
        {
            int coarse = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fine = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (coarse == PackageManager.PERMISSION_GRANTED && fine == PackageManager.PERMISSION_GRANTED)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    private void stopLocationUpdates()
    {
        if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void connect()
    {
        mGoogleApiClient.connect();
    }

    private void disconnect()
    {
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

}
