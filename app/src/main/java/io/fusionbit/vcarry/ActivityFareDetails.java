package io.fusionbit.vcarry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripBreakUpDetails;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityFareDetails extends BaseActivity
{

    @BindView(R.id.tv_discountedCustomerFare)
    TextView tvDiscountedCustomerFare;
    @BindView(R.id.tv_driverShare)
    TextView tvDriverShare;
    @BindView(R.id.tv_driverPremium)
    TextView tvDriverPremium;
    @BindView(R.id.tv_driverTotalShare)
    TextView tvDriverTotalShare;
    @BindView(R.id.ll_chargesItemContainer)
    LinearLayout llChargesItemContainer;
    @BindView(R.id.ll_chargesItemContainerView)
    LinearLayout llChargesItemContainerView;
    @BindView(R.id.tv_totalCharges)
    TextView tvTotalCharges;

    double totalDriverShare = 0;

    public static void start(Context context, String tripId)
    {
        final Intent i = new Intent(context, ActivityFareDetails.class);
        i.putExtra(Constants.INTENT_EXTRA_TRIP_ID, tripId);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_details);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(R.string.trip_charges_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final String tripId = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRIP_ID);

        if (tripId != null)
        {
            API.getInstance().getTripBreakUpDetails(tripId, new RetrofitCallbacks<List<TripBreakUpDetails>>()
            {

                @Override
                public void onResponse(Call<List<TripBreakUpDetails>> call, Response<List<TripBreakUpDetails>> response)
                {
                    super.onResponse(call, response);
                    if (response.isSuccessful())
                    {
                        for (int i = 0; i < response.body().size(); i++)
                        {
                            totalDriverShare = totalDriverShare +
                                    response.body().get(i).getTotalDriverShare();

                            if (i == 0)
                            {
                                bindDataToUi(response.body().get(i));
                            } else
                            {
                                llChargesItemContainerView.setVisibility(View.VISIBLE);

                                View v = LayoutInflater.from(ActivityFareDetails.this)
                                        .inflate(R.layout.single_charge_item, llChargesItemContainer, false);

                                TextView tv1 = (TextView) v.findViewById(R.id.tv_chargeItemName);
                                tv1.setText(response.body().get(i).getItemName() + " : " +
                                        response.body().get(i).getItemDesc());

                                TextView tv2 = (TextView) v.findViewById(R.id.tv_chargeItemShare);
                                tv2.setText(getResources().getString(R.string.rs) + " " +
                                        response.body().get(i).getTotalDriverShare());

                                llChargesItemContainer.addView(v);
                            }
                        }

                        tvTotalCharges.setText(getResources().getString(R.string.rs) + " " + totalDriverShare);

                    }
                }
            });
        } else
        {
            finish();
        }

    }

    private void bindDataToUi(TripBreakUpDetails details)
    {
        tvDiscountedCustomerFare.setText(getResources().getString(R.string.rs) + " " +
                details.getDiscountedCustomerFare());
        tvDriverPremium.setText(getResources().getString(R.string.rs) + " " +
                details.getDriverPremium());
        tvDriverShare.setText(getResources().getString(R.string.rs) + " " +
                details.getDriverShare());
        tvDriverTotalShare.setText(getResources().getString(R.string.rs) + " " +
                details.getTotalDriverShare());
    }

    @Override
    protected void internetNotAvailable()
    {
        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void internetAvailable()
    {

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
