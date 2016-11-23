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
 * Created by rutvik on 11/17/2016 at 10:49 PM.
 */

public class FragmentTrips extends Fragment
{

    Context context;

    public static FragmentTrips newInstance(int index,Context context){
        FragmentTrips fragmentTrips=new FragmentTrips();
        fragmentTrips.context=context;
        Bundle b=new Bundle();
        b.putInt("index",index);
        fragmentTrips.setArguments(b);
        return fragmentTrips;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_trips,container,false);


        return view;
    }
}
