package io.fusionbit.vcarry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import extra.Log;
import io.realm.Realm;
import io.realm.RealmChangeListener;
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
            getSupportActionBar().setTitle("Trip Details");
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
        tvTripCustomerName.setText(tripDetails.getCustomerId());
        tvTripDetailTime.setText(tripDetails.getTripDatetime());
    }

}
