package io.fusionbit.vcarry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import apimodels.TripDetails;
import apimodels.TripsByDriverMail;

public class ActivityFinishedTripDetails extends AppCompatActivity
{

    String tripId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_trip_details);

        tripId = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRIP_ID);

        if (!tripId.isEmpty())
        {
            getFinishedTripDetailsFromRealm();
        }

    }

    private void getFinishedTripDetailsFromRealm()
    {


        bindDataToUi();
    }

    private void bindDataToUi()
    {


    }
}
