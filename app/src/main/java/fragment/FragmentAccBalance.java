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

import adapters.AccountBalanceAdapter;
import api.API;
import api.RetrofitCallbacks;
import apimodels.AccountSummary;
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

        getAccountBalance();

        return view;
    }

    private void getAccountBalance()
    {

        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final RetrofitCallbacks<AccountSummary> onGetAccountSummary =
                new RetrofitCallbacks<AccountSummary>()
                {

                    @Override
                    public void onResponse(Call<AccountSummary> call, Response<AccountSummary> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            adapter.addAccountSummaryCard(response.body());
                        }
                    }
                };

        API.getInstance().getAccountSummary(email, onGetAccountSummary);

    }

}
