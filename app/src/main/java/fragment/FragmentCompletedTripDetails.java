package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apimodels.TripDetails;
import extra.Utils;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.R;
import io.realm.Realm;
import models.TripDistanceDetails;

/**
 * Created by rutvik on 12/4/2016 at 2:56 PM.
 */

public class FragmentCompletedTripDetails extends Fragment
{
    private static final String TAG = App.APP_TAG + FragmentCompletedTripDetails.class.getSimpleName();

    Context context;

    String tripId;

    TextView tvCompletedTripStartTime, tvCompletedTripEndTime, tvCompletedTripDistance,
            tvCompletedTripFare, tvCompletedTripCustomerName;
    //, tvCompletedTripFrom, tvCompletedTripTo;

    public static FragmentCompletedTripDetails newInstance(Context context, final String tripId)
    {
        FragmentCompletedTripDetails fragmentCompletedTripDetails = new FragmentCompletedTripDetails();
        fragmentCompletedTripDetails.context = context;
        fragmentCompletedTripDetails.tripId = tripId;
        return fragmentCompletedTripDetails;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_completed_trip_details, container, false);

        tvCompletedTripStartTime = (TextView) view.findViewById(R.id.tv_completedTripStartTime);
        tvCompletedTripEndTime = (TextView) view.findViewById(R.id.tv_completedTripEndTime);
        tvCompletedTripDistance = (TextView) view.findViewById(R.id.tv_completedTripDistance);
        tvCompletedTripFare = (TextView) view.findViewById(R.id.tv_completedTripFare);
        tvCompletedTripCustomerName = (TextView) view.findViewById(R.id.tv_completedTripCustomerName);
        //tvCompletedTripFrom = (TextView) view.findViewById(R.id.tv_completedTripFrom);
        //tvCompletedTripTo = (TextView) view.findViewById(R.id.tv_completedTripTo);

        showCompletedTripDetails(tripId);

        return view;
    }

    public void showCompletedTripDetails(String tripId)
    {

        TripDetails tripDetails = null;
        TripDistanceDetails tripDistanceDetails = null;

        final Realm realm = Realm.getDefaultInstance();

        tripDetails = realm.where(TripDetails.class)
                .equalTo("tripId", tripId).findFirst();
        realm.close();

        tripDistanceDetails = realm.where(TripDistanceDetails.class)
                .equalTo("tripId", tripId).findFirst();
        realm.close();

        if (tripDetails == null)
        {
            Log.i(TAG, "tripDetails IS NULL");
        }

        if (tripDistanceDetails == null)
        {
            Log.i(TAG, "tripDistanceDetails IS NULL");
        }

        if (tripDetails != null && tripDistanceDetails != null)
        {
            bindDataToUi(tripDetails, tripDistanceDetails);
        } else
        {
            Log.i(TAG, "NO DATA FOUND!!!");
        }
    }

    private void bindDataToUi(final TripDetails tripDetails, final TripDistanceDetails tripDistanceDetails)
    {
        Log.i(TAG, "tripDetails id: " + tripDetails.getTripId());
        Log.i(TAG, "tripDistanceDetails id: " + tripDistanceDetails.getTripId());

        Log.i(TAG, "tripDetails fare: " + tripDetails.getFare());
        Log.i(TAG, "tripDistanceDetails stop time: " + tripDistanceDetails.getTripStopTime());

        Log.i(TAG, "tripDetails customer name: " + tripDetails.getCustomerName());
        Log.i(TAG, "tripDistanceDetails distance travelled: " + tripDistanceDetails.getDistanceTravelled());

        tvCompletedTripStartTime
                .setText(Utils.getDateFromMills(tripDistanceDetails.getTripStartTime()));

        tvCompletedTripEndTime
                .setText(Utils.getDateFromMills(tripDistanceDetails.getTripStopTime()));

        tvCompletedTripDistance.setText(tripDistanceDetails.getDistanceTravelled() + "Km");

        tvCompletedTripFare.setText(getResources().getString(R.string.rs) + " " + tripDetails.getFare());

        tvCompletedTripCustomerName.setText(tripDetails.getCustomerName());

        //tvCompletedTripFrom.setText(tripDetails.getFromShippingLocation());

        //tvCompletedTripTo.setText(tripDetails.getToShippingLocation());

    }

}
