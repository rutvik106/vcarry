package io.fusionbit.vcarry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.List;

import adapters.TripsAdapter;
import apimodels.TripsByDriverMail;

public class ActivityTrips extends AppCompatActivity
{

    private String tripsType = null;

    private RecyclerView rvTrips;

    private TripsAdapter adapter;

    private List<TripsByDriverMail> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        tripsType = getIntent().getStringExtra(Constants.ACCOUNT_TRIP_TYPE);

        trips = getIntent().getParcelableArrayListExtra(Constants.PARCELABLE_TRIP_LIST);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            switch (tripsType)
            {
                case Constants.AccountTripType.TODAY:
                    getSupportActionBar().setTitle(R.string.trips_today);
                    break;
                case Constants.AccountTripType.THIS_MONTH:
                    getSupportActionBar().setTitle(R.string.trips_this_month);
                    break;
                case Constants.AccountTripType.TOTAL:
                    getSupportActionBar().setTitle(R.string.total_trips);
                    break;
            }
        }

        rvTrips = (RecyclerView) findViewById(R.id.rv_accountTrips);

        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        rvTrips.setHasFixedSize(true);

        adapter = new TripsAdapter(this);

        rvTrips.setAdapter(adapter);

        for (TripsByDriverMail trip : trips)
        {
            adapter.addTrip(trip);
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
}
