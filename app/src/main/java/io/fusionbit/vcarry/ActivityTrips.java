package io.fusionbit.vcarry;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.TripsAdapter;
import api.API;
import api.RetrofitCallbacks;
import apimodels.TripsByDriverMail;
import extra.Utils;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityTrips extends BaseActivity
{

    final String tripStatus = Constants.TRIP_STATUS_FINISHED + ","
            + Constants.TRIP_STATUS_CANCELLED_BY_DRIVER + ","
            + Constants.TRIP_STATUS_CANCELLED_BY_CUSTOMER;
    CoordinatorLayout clActivityTrips;
    Snackbar sbNoInternet;
    FrameLayout flLoadingTrips;
    private String tripsType = null;
    private RecyclerView rvTrips;
    private TripsAdapter adapter;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        clActivityTrips = (CoordinatorLayout) findViewById(R.id.cl_activityTrips);

        flLoadingTrips = (FrameLayout) findViewById(R.id.fl_loadingTrips);

        tripsType = getIntent().getStringExtra(Constants.ACCOUNT_TRIP_TYPE);

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            switch (tripsType)
            {
                case Constants.AccountTripType.TODAY:
                    getSupportActionBar().setTitle(R.string.trips_today);
                    getTripForToday();
                    break;
                case Constants.AccountTripType.THIS_MONTH:
                    getSupportActionBar().setTitle(R.string.trips_this_month);
                    getTripForThisMonth();
                    break;
                case Constants.AccountTripType.TOTAL:
                    getSupportActionBar().setTitle(R.string.total_trips);
                    getTotalTrips();
                    break;
            }
        }

        rvTrips = (RecyclerView) findViewById(R.id.rv_accountTrips);

        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        rvTrips.setHasFixedSize(true);

        adapter = new TripsAdapter(this);

        rvTrips.setAdapter(adapter);

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
    protected void internetNotAvailable()
    {
        if (sbNoInternet == null)
        {
            sbNoInternet = Snackbar.make(clActivityTrips, R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
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


    private void getTripForToday()
    {
        final RetrofitCallbacks<List<TripsByDriverMail>> onGetTripSummary =
                new RetrofitCallbacks<List<TripsByDriverMail>>()
                {

                    @Override
                    public void onResponse(Call<List<TripsByDriverMail>> call, Response<List<TripsByDriverMail>> response)
                    {
                        super.onResponse(call, response);
                        flLoadingTrips.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                for (TripsByDriverMail tripsByDriverMail : response.body())
                                {
                                    adapter.addTrip(tripsByDriverMail);
                                }
                            }
                        } else
                        {
                            Toast.makeText(ActivityTrips.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TripsByDriverMail>> call, Throwable t)
                    {
                        super.onFailure(call, t);
                        Toast.makeText(ActivityTrips.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        flLoadingTrips.setVisibility(View.GONE);
                    }
                };

        final String today = Utils.getDate(Calendar.getInstance().getTime());

        API.getInstance().getTripSummary(email, tripStatus, today, today, null,
                onGetTripSummary);

    }

    private void getTripForThisMonth()
    {
        final RetrofitCallbacks<List<TripsByDriverMail>> onGetTripSummary =
                new RetrofitCallbacks<List<TripsByDriverMail>>()
                {

                    @Override
                    public void onResponse(Call<List<TripsByDriverMail>> call, Response<List<TripsByDriverMail>> response)
                    {
                        super.onResponse(call, response);
                        flLoadingTrips.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                for (TripsByDriverMail tripsByDriverMail : response.body())
                                {
                                    adapter.addTrip(tripsByDriverMail);
                                }
                            }
                        } else
                        {
                            Toast.makeText(ActivityTrips.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TripsByDriverMail>> call, Throwable t)
                    {
                        super.onFailure(call, t);
                        Toast.makeText(ActivityTrips.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        flLoadingTrips.setVisibility(View.GONE);
                    }
                };

        final String today = Utils.getDate(Calendar.getInstance().getTime());
        final Date month = Utils.addDays(Calendar.getInstance().getTime(), -30);
        final String monthInString = Utils.getDate(month);

        API.getInstance().getTripSummary(email, tripStatus, monthInString, today, null,
                onGetTripSummary);
    }

    private void getTotalTrips()
    {
        final RetrofitCallbacks<List<TripsByDriverMail>> onGetTripSummary =
                new RetrofitCallbacks<List<TripsByDriverMail>>()
                {

                    @Override
                    public void onResponse(Call<List<TripsByDriverMail>> call, Response<List<TripsByDriverMail>> response)
                    {
                        super.onResponse(call, response);
                        flLoadingTrips.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                for (TripsByDriverMail tripsByDriverMail : response.body())
                                {
                                    adapter.addTrip(tripsByDriverMail);
                                }
                            } else
                            {
                                Toast.makeText(ActivityTrips.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TripsByDriverMail>> call, Throwable t)
                    {
                        super.onFailure(call, t);
                        Toast.makeText(ActivityTrips.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        flLoadingTrips.setVisibility(View.GONE);
                    }
                };

        API.getInstance().getTripSummary(email, tripStatus, null, null, null,
                onGetTripSummary);
    }


}
