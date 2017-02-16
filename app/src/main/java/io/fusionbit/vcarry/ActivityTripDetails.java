package io.fusionbit.vcarry;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import extra.Log;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityTripDetails extends AppCompatActivity
{

    private static final String TAG = App.APP_TAG + ActivityTripDetails.class.getSimpleName();

    String tripId;

    TextView tvTripDetailTime, tvTripCustomerName, tvTripLocation, tvTripDestination,
            tvTripStatus, tvTripFare;

    Button btnStartTrip;

    TripDetails tripDetails;

    Realm realm;

    //is activity connected to service
    boolean mServiceBound = false;

    //main STICKY service running in foreground (also shows up in notifications)
    TransportRequestHandlerService mService;

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

        btnStartTrip = (Button) findViewById(R.id.btn_startTrip);


        tripId = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRIP_ID);

        Log.i(TAG, "TRIP ID IN ACTIVITY TRIP DETAILS IS: " + tripId);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (!tripId.isEmpty())
        {
            realm = Realm.getDefaultInstance();
            tryToGetFromRealm();
            getTripDetails();
        }

        //start bound service now
        Intent TransportRequestHandlerService = new Intent(this, TransportRequestHandlerService.class);
        startService(TransportRequestHandlerService);

        //bind activity to service
        bindService(TransportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void tryToGetFromRealm()
    {
        tripDetails = realm.where(TripDetails.class).equalTo("tripId", tripId).findFirst();


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

        API.getInstance().getTripDetailsByTripId(tripId, new RetrofitCallbacks<TripDetails>()
        {

            @Override
            public void onResponse(Call<TripDetails> call, Response<TripDetails> response)
            {
                super.onResponse(call, response);
                if (response.isSuccessful())
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
            }
        });

    }


    private void bindDataToUi()
    {
        Log.i(TAG, "BINDING DATA TO UI");
        tvTripFare.setText(getResources().getString(R.string.rs) + " " + tripDetails.getFare());
        tvTripStatus.setText(tripDetails.getStatus());
        tvTripLocation.setText(tripDetails.getFromShippingLocation());
        tvTripDestination.setText(tripDetails.getToShippingLocation());
        tvTripCustomerName.setText(tripDetails.getCustomerName());
        tvTripDetailTime.setText(tripDetails.getTripDatetime());

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
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
                            Toast.makeText(mService, "something went wrong please try again later", Toast.LENGTH_SHORT).show();
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
        if (mServiceBound)
        {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }
}
