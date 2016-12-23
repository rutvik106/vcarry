package io.fusionbit.vcarry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.List;

import adapters.TripNotificationAdapter;
import apimodels.TripDetails;

public class ActivityTripNotifications extends AppCompatActivity implements App.TripNotificationListener
{

    RecyclerView rvTripNotifications;

    TripNotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_notifications);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Trip Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        rvTripNotifications = (RecyclerView) findViewById(R.id.rv_tripNotifications);

        rvTripNotifications.setHasFixedSize(true);
        rvTripNotifications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TripNotificationAdapter(this);

        rvTripNotifications.setAdapter(adapter);

        for (TripDetails singleTripDetails : ((App) getApplication()).getTripNotificationList())
        {
            adapter.addTripDetails(singleTripDetails);
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        ((App) getApplication()).getTripNotifications(this);

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

    @Override
    public void onGetTripNotificationList(List<TripDetails> tripDetailsList)
    {
        adapter.clear();
        for (TripDetails singleTripDetails : tripDetailsList)
        {
            adapter.addTripDetails(singleTripDetails);
        }
    }
}
