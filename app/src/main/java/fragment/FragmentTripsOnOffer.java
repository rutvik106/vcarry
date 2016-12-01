package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 11/17/2016 at 11:18 PM.
 */

public class FragmentTripsOnOffer extends Fragment
{

    Context context;

    public static FragmentTripsOnOffer newInstance(int index, Context context)
    {
        FragmentTripsOnOffer fragmentTripsOnOffer = new FragmentTripsOnOffer();
        fragmentTripsOnOffer.context = context;
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragmentTripsOnOffer.setArguments(b);
        return fragmentTripsOnOffer;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trip_on_offer, container, false);

        return view;
    }

}
