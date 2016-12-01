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

import adapters.TripsAdapter;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 11/17/2016 at 11:17 PM.
 */

public class FragmentAccBalance extends Fragment
{

    Context context;

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

        return view;
    }

}
