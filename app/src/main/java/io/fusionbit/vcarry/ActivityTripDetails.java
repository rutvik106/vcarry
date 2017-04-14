package io.fusionbit.vcarry;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.Locale;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import extra.LocaleHelper;
import extra.Log;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityTripDetails extends BaseActivity implements View.OnClickListener
{

    private static final String TAG = App.APP_TAG + ActivityTripDetails.class.getSimpleName();

    String tripId, tripNumber;

    TextView tvTripDetailTime, tvTripCustomerName, tvTripLocation, tvTripDestination,
            tvTripStatus, tvTripFare, tvTripNumber, tvFromCompanyName, tvToCompanyName,
            tvTripWeight, tvTripDimension;

    Button btnStartTrip;

    FloatingActionButton fabUpdateFromLocation, fabUpdateToLocation;

    TripDetails tripDetails;

    CoordinatorLayout clActivityTripDetails;

    Realm realm;

    //is activity connected to service
    boolean mServiceBound = false;

    //main STICKY service running in foreground (also shows up in notifications)
    TransportRequestHandlerService mService;

    Snackbar sbNoInternet;

    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.i(TAG, "ACTIVITY CONNECTED TO SERVICE");
            mServiceBound = true;
            //get service instance here
            mService = ((TransportRequestHandlerService.TransportRequestServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className)
        {
            Log.i(TAG, "ACTIVITY DISCONNECTED FROM SERVICE");
            mServiceBound = false;
            mService = null;
        }
    };
    private Call<ResponseBody> updatingLocation;

    public static void start(Context context, String tripNumber)
    {
        Intent i = new Intent(context, ActivityTripDetails.class);
        i.putExtra(Constants.INTENT_EXTRA_TRIP_NUMBER, tripNumber);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getResources().getString(R.string.trip_details));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        tvTripDetailTime = (TextView) findViewById(R.id.tv_tripDetailTime);
        tvTripCustomerName = (TextView) findViewById(R.id.tv_tripCustomerName);
        tvTripDestination = (TextView) findViewById(R.id.tv_tripDestination);
        tvTripLocation = (TextView) findViewById(R.id.tv_tripLocation);
        tvTripFare = (TextView) findViewById(R.id.tv_tripFare);
        tvTripStatus = (TextView) findViewById(R.id.tv_tripStatus);

        tvTripNumber = (TextView) findViewById(R.id.tv_tripNumber);

        btnStartTrip = (Button) findViewById(R.id.btn_startTrip);

        clActivityTripDetails = (CoordinatorLayout) findViewById(R.id.cl_activityTripDetails);

        fabUpdateFromLocation = (FloatingActionButton) findViewById(R.id.fab_updateFromLocation);
        fabUpdateToLocation = (FloatingActionButton) findViewById(R.id.fab_updateToLocation);

        tvFromCompanyName = (TextView) findViewById(R.id.tv_tripFromCompanyName);
        tvToCompanyName = (TextView) findViewById(R.id.tv_tripToCompanyName);

        tvTripDimension = (TextView) findViewById(R.id.tv_tripDimension);
        tvTripWeight = (TextView) findViewById(R.id.tv_tripWeight);

        fabUpdateFromLocation.setOnClickListener(this);
        fabUpdateToLocation.setOnClickListener(this);

        tripId = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRIP_ID);

        tripNumber = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRIP_NUMBER);

        Log.i(TAG, "TRIP ID IN ACTIVITY TRIP DETAILS IS: " + tripId);

    }

    @Override
    protected void onStart()
    {
        super.onStart();


        realm = Realm.getDefaultInstance();
        tryToGetFromRealm();
        getTripDetails();


        //start bound service now
        Intent TransportRequestHandlerService = new Intent(this, TransportRequestHandlerService.class);
        startService(TransportRequestHandlerService);

        //bind activity to service
        bindService(TransportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void tryToGetFromRealm()
    {
        if (tripId != null)
        {
            tripDetails = realm.where(TripDetails.class).equalTo("tripId", tripId).findFirst();
        } else if (tripNumber != null)
        {
            tripDetails = realm.where(TripDetails.class).equalTo("tripNo", tripNumber).findFirst();
        }


        if (tripDetails != null)
        {
            bindDataToUi();
            tripDetails.addChangeListener(new RealmChangeListener<TripDetails>()
            {
                @Override
                public void onChange(TripDetails tripDetails)
                {
                    bindDataToUi();
                }
            });
        }
    }

    private void getTripDetails()
    {
        if (tripId != null)
        {
            API.getInstance().getTripDetailsByTripId(tripId, new RetrofitCallbacks<TripDetails>()
            {

                @Override
                public void onResponse(Call<TripDetails> call, Response<TripDetails> response)
                {
                    super.onResponse(call, response);
                    if (response.isSuccessful())
                    {
                        handleResponse(response);
                    }
                }
            });
        } else if (tripNumber != null)
        {
            API.getInstance().getTripDetailsByTripNo(tripNumber, new RetrofitCallbacks<TripDetails>()
            {

                @Override
                public void onResponse(Call<TripDetails> call, Response<TripDetails> response)
                {
                    super.onResponse(call, response);
                    if (response.isSuccessful())
                    {
                        handleResponse(response);
                    }
                }
            });
        }

    }

    private void handleResponse(Response<TripDetails> response)
    {
        if (response.body() != null)
        {
            tripDetails = response.body();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(response.body());
            realm.commitTransaction();

            bindDataToUi();
        }
    }

    private void bindDataToUi()
    {
        Log.i(TAG, "BINDING DATA TO UI");
        tvTripFare.setText(getResources().getString(R.string.rs) + " " + tripDetails.getFare());
        tvTripStatus.setText(tripDetails.getStatus());


        if (LocaleHelper.getLanguage(this).equalsIgnoreCase("gu"))
        {
            tvTripLocation.setText(tripDetails.getFromGujaratiAddress());
            tvTripDestination.setText(tripDetails.getToGujaratiAddress());
            tvFromCompanyName.setText(tripDetails.getFromGujaratiName());
            tvToCompanyName.setText(tripDetails.getToGujaratiName());
        } else
        {
            tvTripLocation.setText(tripDetails.getFromShippingLocation());
            tvTripDestination.setText(tripDetails.getToShippingLocation());
            tvFromCompanyName.setText(tripDetails.getFromCompanyName());
            tvToCompanyName.setText(tripDetails.getToCompanyName());
        }


        tvTripCustomerName.setText(tripDetails.getCustomerName());
        tvTripDetailTime.setText(tripDetails.getTripDatetimeDmy());

        tvTripWeight.setText(tripDetails.getWeight());
        tvTripDimension.setText(tripDetails.getDimensions());

        tvTripNumber.setText(tripDetails.getTripNo());

        final int tripStatus = Integer.valueOf(tripDetails.getTripStatus());
        final int tripStatusStarted = Integer.valueOf(Constants.TRIP_STATUS_TRIP_STARTED);
        final int tripStatusFinished = Integer.valueOf(Constants.TRIP_STATUS_FINISHED);

        if (tripStatus < tripStatusStarted)
        {
            btnStartTrip.setVisibility(View.VISIBLE);
            btnStartTrip.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    new AlertDialog.Builder(ActivityTripDetails.this)
                            .setTitle(getResources().getString(R.string.start_trip))
                            .setMessage(getResources().getString(R.string.are_you_sure_start_trip))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(getResources().getString(R.string.start),
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            startTrip();
                                        }
                                    })
                            .setNegativeButton(getResources().getString(R.string.cancel), null)
                            .show();
                }
            });
        } else if (tripStatus == tripStatusFinished)
        {
            findViewById(R.id.btn_viewTripDetails).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_viewTripDetails).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(ActivityTripDetails.this, ActivityFinishedTripDetails.class);
                    i.putExtra(Constants.INTENT_EXTRA_TRIP_ID, tripId);
                    startActivity(i);
                }
            });
        }

        if (tripDetails.getToLatLong() == null)
        {
            fabUpdateToLocation
                    .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        } else if (tripDetails.getToLatLong().isEmpty())
        {
            fabUpdateToLocation
                    .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        } else
        {
            fabUpdateToLocation
                    .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        }


        if (tripDetails.getFromLatLong() == null)
        {
            fabUpdateFromLocation
                    .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));

        } else if (tripDetails.getFromLatLong().isEmpty())
        {
            fabUpdateFromLocation
                    .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        } else
        {
            fabUpdateFromLocation
                    .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        } else if (item.getItemId() == R.id.action_showTripOnMap)
        {
            if (tripDetails.getFromLatLong() == null)
            {
                return true;
            } else if (tripDetails.getFromLatLong().isEmpty())
            {
                return true;
            }

            final String[] stringFromLatLng = tripDetails.getFromLatLong().split(",");

            String uri;

            if (tripDetails.getToLatLong() != null)
            {
                if (!tripDetails.getToLatLong().isEmpty())
                {
                    final String[] stringToLatLng = tripDetails.getToLatLong().split(",");

                    uri = String.format(Locale.ENGLISH,
                            "http://maps.google.com/maps?saddr=%s,%s(%s)&daddr=%s,%s(%s)",
                            stringFromLatLng[0], stringFromLatLng[1], tripDetails.getFromShippingLocation(),
                            stringToLatLng[0], stringToLatLng[1], tripDetails.getToShippingLocation());
                } else
                {
                    uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%s,%s(%s)",
                            stringFromLatLng[0], stringFromLatLng[1], tripDetails.getFromShippingLocation());
                }
            } else
            {
                uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%s,%s(%s)",
                        stringFromLatLng[0], stringFromLatLng[1], tripDetails.getFromShippingLocation());
            }

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
            if (intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTrip()
    {
        btnStartTrip.setVisibility(View.GONE);

        final boolean isDriverOnTrip = PreferenceManager.getDefaultSharedPreferences(ActivityTripDetails.this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);

        if (!isDriverOnTrip)
        {
            API.getInstance().updateTripStatus(Constants.TRIP_STATUS_TRIP_STARTED,
                    tripId, new RetrofitCallbacks<ResponseBody>()
                    {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                        {
                            super.onResponse(call, response);
                            if (response.isSuccessful())
                            {

                                getTripDetails();

                                if (mService != null)
                                {
                                    Log.i(TAG, "STARTING TRIP NOW CALLING FUNCTION ON SERVICE");
                                    mService.startTrip(tripId);
                                    Intent i = new Intent(ActivityTripDetails.this, ActivityHome.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }

                                try
                                {
                                    System.out.println(response.body().string());
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                        }


                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t)
                        {
                            super.onFailure(call, t);
                            btnStartTrip.setVisibility(View.VISIBLE);
                            Toast.makeText(mService, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    });

        } else
        {
            Toast.makeText(this, getResources().getString(R.string.please_complete_current_trip), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (updatingLocation != null)
        {
            updatingLocation.cancel();
        }
        if (mServiceBound)
        {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void internetNotAvailable()
    {
        if (sbNoInternet == null)
        {
            sbNoInternet = Snackbar.make(clActivityTripDetails, R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
            sbNoInternet.show();
        }
    }

    @Override
    protected void internetAvailable()
    {
        if (sbNoInternet != null)
        {
            if (sbNoInternet.isShown())
            {
                sbNoInternet.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.fab_updateFromLocation:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.are_you_sure_update_from_location)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                new UpdateShippingLocationLatLng(tripDetails.getFromShippingLocationId());
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                break;

            case R.id.fab_updateToLocation:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.are_you_sure_update_to_location)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                new UpdateShippingLocationLatLng(tripDetails.getToShippingLocationId());
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_trip_details, menu);
        return true;
    }

    private class UpdateShippingLocationLatLng extends RetrofitCallbacks<ResponseBody>
    {

        final String shippingLocationId;

        FusedLocation fusedLocation;

        UpdateShippingLocationLatLng(String shippingLocationId)
        {
            showProgressDialog(getString(R.string.getting_location));
            this.shippingLocationId = shippingLocationId;
            fusedLocation = new FusedLocation(ActivityTripDetails.this, new FusedLocation.ApiConnectionCallbacks(null)
            {
                @Override
                public void onConnected(@Nullable Bundle bundle)
                {
                    fusedLocation.startGettingLocation(new FusedLocation.GetLocation()
                    {
                        @Override
                        public void onLocationChanged(Location location)
                        {
                            fusedLocation.stopGettingLocation();
                            updateShippingLocation(location.getLatitude() + "," + location.getLongitude());
                        }
                    });
                }
            }, new GoogleApiClient.OnConnectionFailedListener()
            {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                {
                    Toast.makeText(ActivityTripDetails.this, R.string.cannot_connect_location_api, Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            });
        }

        void updateShippingLocation(String latLng)
        {
            showProgressDialog(getString(R.string.updating_location));
            fusedLocation = null;
            updatingLocation = API.getInstance().updateShippingLocationLatLng(shippingLocationId, latLng, this);
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
        {
            super.onResponse(call, response);
            hideProgressDialog();
            if (response.isSuccessful())
            {
                try
                {
                    if (response.body().string().contains("success"))
                    {
                        Toast.makeText(ActivityTripDetails.this, R.string.location_updated_successfully, Toast.LENGTH_SHORT).show();
                        getTripDetails();
                    } else
                    {
                        Toast.makeText(ActivityTripDetails.this, R.string.failed_to_update_location, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                Toast.makeText(ActivityTripDetails.this, R.string.failed_to_update_location, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t)
        {
            super.onFailure(call, t);
            hideProgressDialog();
            Toast.makeText(ActivityTripDetails.this, R.string.failed_to_update_location, Toast.LENGTH_SHORT).show();
        }
    }

}
