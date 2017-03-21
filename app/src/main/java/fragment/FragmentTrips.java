package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import adapters.TripsAdapter;
import api.API;
import api.RetrofitCallbacks;
import apimodels.TripsByDriverMail;
import extra.Log;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.R;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rutvik on 11/17/2016 at 10:49 PM.
 */

public class FragmentTrips extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener
{

    private static final String TAG = App.APP_TAG + FragmentTrips.class.getSimpleName();
    Context context;

    RecyclerView rvTrips;

    TripsAdapter adapter;

    Realm realm;

    RealmResults<TripsByDriverMail> tripResults;

    private SwipeRefreshLayout srlRefreshTrips;

    FrameLayout flTripListEmptyView;

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

        flTripListEmptyView = (FrameLayout) view.findViewById(R.id.fl_tripListEmptyView);

        srlRefreshTrips = (SwipeRefreshLayout) view.findViewById(R.id.srl_refreshTrips);

        srlRefreshTrips.setOnRefreshListener(this);

        rvTrips = (RecyclerView) view.findViewById(R.id.rv_trips);
        rvTrips.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTrips.setHasFixedSize(true);

        adapter = new TripsAdapter(getActivity());

        rvTrips.setAdapter(adapter);

        return view;
    }

    public void getTrips()
    {
        realm = Realm.getDefaultInstance();

        tripResults = realm.where(TripsByDriverMail.class).findAll();

        for (TripsByDriverMail t : tripResults)
        {
            adapter.addTrip(t);
        }
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() > 0)
        {
            flTripListEmptyView.setVisibility(View.GONE);
        } else
        {
            flTripListEmptyView.setVisibility(View.VISIBLE);
        }

        tripResults
                .addChangeListener(new RealmChangeListener<RealmResults<TripsByDriverMail>>()
                                   {
                                       @Override
                                       public void onChange(RealmResults<TripsByDriverMail> results)
                                       {
                                           for (TripsByDriverMail trip : results)
                                           {
                                               adapter.addTrip(trip);
                                           }
                                           adapter.notifyDataSetChanged();
                                           if (adapter.getItemCount() > 0)
                                           {
                                               flTripListEmptyView.setVisibility(View.GONE);
                                           } else
                                           {
                                               flTripListEmptyView.setVisibility(View.VISIBLE);
                                           }
                                       }
                                   }

                );

        API.getInstance().getTripsByDriverMail(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                new RetrofitCallbacks<List<TripsByDriverMail>>()
                {

                    @Override
                    public void onResponse(Call<List<TripsByDriverMail>> call, Response<List<TripsByDriverMail>> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            for (final TripsByDriverMail trip : response.body())
                            {
                                if (trip instanceof TripsByDriverMail)
                                {
                                    Log.i(TAG, "TRIP ID: " + trip.getTripId());
                                    realm.copyToRealmOrUpdate(trip);
                                }
                            }

                            realm.commitTransaction();

                            if (srlRefreshTrips.isRefreshing())
                            {
                                srlRefreshTrips.setRefreshing(false);
                            }

                        }
                    }
                });

    }


    @Override
    public void onRefresh()
    {
        getTrips();
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (newText.length() > 0)
        {
            final List<TripsByDriverMail> searchedTrips = new ArrayList<>();
            for (TripsByDriverMail trip : tripResults)
            {
                if (trip.getTripNo().contains(newText) ||
                        trip.getStatus().toLowerCase().contains(newText.toLowerCase()))
                {
                    searchedTrips.add(trip);
                }
            }
            if (searchedTrips.size() > 0)
            {
                adapter.clear();
                for (TripsByDriverMail trip : searchedTrips)
                {
                    adapter.addTrip(trip);
                }
            }
        } else
        {
            adapter.clear();
            for (TripsByDriverMail trip : tripResults)
            {
                adapter.addTrip(trip);
            }
        }
        return false;
    }
}
