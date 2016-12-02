package io.fusionbit.vcarry;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    Realm realm;

    TripDetails tripDetails;

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

        if (!tripId.isEmpty())
        {
            getTripDetails();
        }

    }

    private void getTripDetails()
    {
        realm = Realm.getDefaultInstance();

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

        API.getInstance().getTripDetailsByTripId(tripId, new RetrofitCallbacks<TripDetails>()
        {

            @Override
            public void onResponse(Call<TripDetails> call, Response<TripDetails> response)
            {
                super.onResponse(call, response);
                if (response.isSuccessful())
                {
                    tripDetails = response.body();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(response.body());
                    realm.commitTransaction();
                    bindDataToUi();
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
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTrip()
    {
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
                                PreferenceManager.getDefaultSharedPreferences(ActivityTripDetails.this)
                                        .edit()
                                        .putString(Constants.CURRENT_TRIP_ID, tripId)
                                        .putBoolean(Constants.IS_DRIVER_ON_TRIP, true)
                                        .apply();

                                btnStartTrip.setVisibility(View.GONE);

                                stopService(new Intent(ActivityTripDetails.this,
                                        TransportRequestHandlerService.class));

                                startService(new Intent(ActivityTripDetails.this,
                                        TransportRequestHandlerService.class));

                                try
                                {
                                    System.out.println(response.body().string());
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

        } else
        {
            Toast.makeText(this, getResources().getString(R.string.are_you_sure_start_trip), Toast.LENGTH_SHORT).show();
        }
    }
}
