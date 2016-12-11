package io.fusionbit.vcarry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;

/**
 * Created by rutvik on 9/26/2016 at 12:19 PM.
 */

public class FusedLocation implements LocationListener
{

    private static final String TAG = "LOC " + FusedLocation.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 7000; //2000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000; //UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context mContext;
    private GetLocation mGetCurrentLocation;

    ApiConnectionCallbacks connectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;

    public static final int FORCE_TO_TURN_ON_LOCATION = 4444;

    public enum Status
    {
        UNAVAILABLE,
        UPDATE_REQUIRE,
        AVAILABLE
    }

    public interface GetLocation
    {
        void onLocationChanged(Location location);
    }

    public FusedLocation(Context context, ApiConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener)
    {
        mContext = context;
        this.onConnectionFailedListener = onConnectionFailedListener;
        this.connectionCallbacks = connectionCallbacks;
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
        connectionCallbacks.setmGoogleApiClient(mGoogleApiClient);
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


    public static Status isGooglePlayServiceAvailable(Context context)
    {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (status == ConnectionResult.SUCCESS)
        {
            return Status.AVAILABLE;
        } else if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)
        {
            return Status.UPDATE_REQUIRE;
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

    public GoogleApiClient getmGoogleApiClient()
    {
        return mGoogleApiClient;
    }


    public static abstract class ApiConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks
    {
        final ForceLocationCallbacks forceLocationCallbacks;

        GoogleApiClient mGoogleApiClient;

        public ApiConnectionCallbacks(final LocationAwareActivity locationAwareActivity)
        {
            this.forceLocationCallbacks = new ForceLocationCallbacks(locationAwareActivity);
        }


        public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient)
        {
            this.mGoogleApiClient = mGoogleApiClient;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle)
        {
            //generate request location msg to check locationing enable/disable status
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi
                            .checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(forceLocationCallbacks);

        }

        @Override
        public void onConnectionSuspended(int i)
        {

        }
    }

    private static class ForceLocationCallbacks implements ResultCallback<LocationSettingsResult>
    {
        final LocationAwareActivity requestingActivity;

        public ForceLocationCallbacks(final LocationAwareActivity requestingActivity)
        {
            this.requestingActivity = requestingActivity;
        }

        @Override
        public final void onResult(LocationSettingsResult result)
        {
            final com.google.android.gms.common.api.Status status = result.getStatus();

            switch (status.getStatusCode())
            {
                case LocationSettingsStatusCodes.SUCCESS:
                    requestingActivity.locationServiceAlreadyOn();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    //locationing is disabled on user device, and needs to be enbled
                    try
                    {
                        //request user to turm locationing on
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        if (requestingActivity != null)
                        {
                            status.startResolutionForResult(requestingActivity, FORCE_TO_TURN_ON_LOCATION);
                        }
                    } catch (IntentSender.SendIntentException e)
                    {
                        Log.i(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    //location setting cannot be modified sorry :(
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    requestingActivity.locationSettingChangeUnavailable();
                    break;
            }
        }

    }

    public interface ForceLocationResultCallbacks
    {
        void locationServiceAlreadyOn();

        void locationServiceTurnedOn();

        void locationSettingChangeUnavailable();
    }

    public static abstract class LocationAwareActivity extends AppCompatActivity implements ForceLocationResultCallbacks
    {

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode)
            {

                case FORCE_TO_TURN_ON_LOCATION:
                    //user allowed to access location, now location service on
                    Log.i(TAG, "REQUEST CHECK SETTING RESULT: " + resultCode);
                    if (resultCode == Activity.RESULT_OK)
                    {
                        locationServiceTurnedOn();
                    }
                    break;

                default:

                    break;
            }
        }
    }

}
