package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.AccountBalanceAdapter;
import api.API;
import api.RetrofitCallbacks;
import apimodels.AccountSummary;
import apimodels.TripsByDriverMail;
import extra.Utils;
import io.fusionbit.vcarry.Constants;
import io.fusionbit.vcarry.R;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rutvik on 11/17/2016 at 11:17 PM.
 */

public class FragmentAccBalance extends Fragment
{

    Context context;

    RecyclerView rvAccountBalance;

    AccountBalanceAdapter adapter;

    final AccountSummary accountSummary = new AccountSummary();

    String email;

    final String tripStatus = Constants.TRIP_STATUS_FINISHED + ","
            + Constants.TRIP_STATUS_CANCELLED_BY_DRIVER + ","
            + Constants.TRIP_STATUS_CANCELLED_BY_CUSTOMER;

    public static FragmentAccBalance newInstance(int index, Context context)
    {
        FragmentAccBalance fragmentAccBalance = new FragmentAccBalance();
        fragmentAccBalance.context = context;
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragmentAccBalance.setArguments(b);
        return fragmentAccBalance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_account_balance, container, false);

        rvAccountBalance = (RecyclerView) view.findViewById(R.id.rv_accountBalance);
        rvAccountBalance.setHasFixedSize(true);
        rvAccountBalance.setLayoutManager(new LinearLayoutManager(context));

        adapter = new AccountBalanceAdapter(getActivity());

        rvAccountBalance.setAdapter(adapter);

        adapter.addAccountSummaryCard(accountSummary);

        getAccountBalance();

        return view;
    }

    public void getAccountBalance()
    {
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        getAccountBalanceForToday();
        getAccountBalanceForThisMonth();
        getTotalAccountBalance();

        getTripForToday();
        getTripForThisMonth();
        getTotalTrips();
    }

    private void getAccountBalanceForToday()
    {
        final RetrofitCallbacks<AccountSummary> onGetAccountSummary =
                new RetrofitCallbacks<AccountSummary>()
                {

                    @Override
                    public void onResponse(Call<AccountSummary> call, Response<AccountSummary> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                accountSummary.setReceivedToday(response.body().getReceived());
                                accountSummary.setReceivableToday(response.body().getReceivable());
                            }
                        }
                    }
                };

        final String today = Utils.getDate(Calendar.getInstance().getTime());

        API.getInstance().getAccountSummary(email, today, today, onGetAccountSummary);

    }

    private void getAccountBalanceForThisMonth()
    {
        final RetrofitCallbacks<AccountSummary> onGetAccountSummary =
                new RetrofitCallbacks<AccountSummary>()
                {

                    @Override
                    public void onResponse(Call<AccountSummary> call, Response<AccountSummary> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                accountSummary.setReceivedThisMonth(response.body().getReceived());
                                accountSummary.setReceivableThisMonth(response.body().getReceivable());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                };

        final String today = Utils.getDate(Calendar.getInstance().getTime());
        final Date month = Utils.addDays(Calendar.getInstance().getTime(), -30);
        final String monthInString = Utils.getDate(month);

        API.getInstance().getAccountSummary(email, monthInString, today, onGetAccountSummary);
    }

    private void getTotalAccountBalance()
    {
        final RetrofitCallbacks<AccountSummary> onGetAccountSummary =
                new RetrofitCallbacks<AccountSummary>()
                {

                    @Override
                    public void onResponse(Call<AccountSummary> call, Response<AccountSummary> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                accountSummary.setTotalReceived(response.body().getReceived());
                                accountSummary.setTotalReceivable(response.body().getReceivable());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                };

        API.getInstance().getAccountSummary(email, null, null, onGetAccountSummary);
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
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                for (TripsByDriverMail tripsByDriverMail : response.body())
                                {
                                    accountSummary.getTripToday().add(tripsByDriverMail);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
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
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                for (TripsByDriverMail tripsByDriverMail : response.body())
                                {
                                    accountSummary.getTripThisMonth().add(tripsByDriverMail);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
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
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                for (TripsByDriverMail tripsByDriverMail : response.body())
                                {
                                    accountSummary.getTotalTrips().add(tripsByDriverMail);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                };

        API.getInstance().getTripSummary(email, tripStatus, null, null, null,
                onGetTripSummary);
    }


}
