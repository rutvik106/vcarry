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

import java.util.List;

import adapters.TripsAdapter;
import api.API;
import api.RetrofitCallbacks;
import apimodels.TripsByDriverMail;
import io.fusionbit.vcarry.R;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rutvik on 11/17/2016 at 10:49 PM.
 */

public class FragmentTrips extends Fragment
{

    Context context;

    RecyclerView rvTrips;

    TripsAdapter adapter;

    public static FragmentTrips newInstance(int index, Context context)
    {
        FragmentTrips fragmentTrips = new FragmentTrips();
        fragmentTrips.context = context;
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragmentTrips.setArguments(b);
        return fragmentTrips;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        rvTrips = (RecyclerView) view.findViewById(R.id.rv_trips);
        rvTrips.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTrips.setHasFixedSize(true);

        adapter = new TripsAdapter(getActivity());

        rvTrips.setAdapter(adapter);

        getTrips();

        return view;
    }

    private void getTrips()
    {

        API.getInstance().getTripsByDriverMail(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                new RetrofitCallbacks<List<TripsByDriverMail>>()
                {

                    @Override
                    public void onResponse(Call<List<TripsByDriverMail>> call, Response<List<TripsByDriverMail>> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            for (TripsByDriverMail trip : response.body())
                            {
                                adapter.addTrip(trip);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }
}
