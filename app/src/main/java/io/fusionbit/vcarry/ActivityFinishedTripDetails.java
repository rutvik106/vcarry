package io.fusionbit.vcarry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import fragment.FragmentCompletedTripDetails;

public class ActivityFinishedTripDetails extends AppCompatActivity
{

    String tripId = "";

    //TripDetails tripDetails;

    //TripDistanceDetails tripDistanceDetails;

    FragmentCompletedTripDetails fragmentCompletedTripDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_trip_details);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(R.string.finished_trip_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tripId = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRIP_ID);

        if (tripId != null)
        {
            bindDataToUi();
        } else
        {
            finish();
        }

    }

/*    private void getFinishedTripDetailsFromRealm()
    {
        final TripDetails tripDetailsResult = Realm.getDefaultInstance()
                .where(TripDetails.class)
                .equalTo("tripId", tripId)
                .findFirst();
        if()
        tripDetails = Realm.getDefaultInstance().copyFromRealm(tripDetailsResult);

        final TripDistanceDetails tripDistanceDetailsResult = Realm.getDefaultInstance()
                .where(TripDistanceDetails.class)
                .equalTo("tripId", tripId)
                .findFirst();

        tripDistanceDetails = Realm.getDefaultInstance().copyFromRealm(tripDistanceDetailsResult);

        bindDataToUi();
    }*/

    private void bindDataToUi()
    {

        fragmentCompletedTripDetails = FragmentCompletedTripDetails.newInstance(this, tripId,
                true, null);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_completedTripDetails, fragmentCompletedTripDetails)
                .commitAllowingStateLoss();


        /*if (tripDetails != null && tripDistanceDetails != null)
        {

        }*/

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
}
