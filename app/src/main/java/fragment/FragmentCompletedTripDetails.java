package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import extra.Utils;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.R;
import io.realm.Realm;
import models.TripDistanceDetails;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rutvik on 12/4/2016 at 2:56 PM.
 */

public class FragmentCompletedTripDetails extends Fragment implements View.OnClickListener
{
    private static final String TAG = App.APP_TAG + FragmentCompletedTripDetails.class.getSimpleName();

    Context context;

    String tripId;

    TextView tvCompletedTripStartTime, tvCompletedTripEndTime, tvCompletedTripDistance,
            tvCompletedTripFare, tvCompletedTripCustomerName;
    //, tvCompletedTripFrom, tvCompletedTripTo;

    CheckBox cbMemo, cbLabor;

    EditText etMemoAmount, etLaborAmount, etTotalAmount;

    FloatingActionButton fabTripDone;

    TripDetails tripDetails = null;
    TripDistanceDetails tripDistanceDetails = null;

    TripStopDataInsertionCallback tripStopDataInsertionCallback;

    RetrofitCallbacks OnInsertTripStopData = new RetrofitCallbacks<ResponseBody>()
    {

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
        {
            super.onResponse(call, response);
            if (response.isSuccessful())
            {
                tripStopDataInsertionCallback.dataInsertedSuccessfully();
            }
            tripStopDataInsertionCallback.failedToInsertTripStopData();
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t)
        {
            super.onFailure(call, t);
            tripStopDataInsertionCallback.failedToInsertTripStopData();
        }
    };

    public static FragmentCompletedTripDetails newInstance(final Context context, final String tripId,
                                                           final TripStopDataInsertionCallback tripStopDataInsertionCallback)
    {
        FragmentCompletedTripDetails fragmentCompletedTripDetails = new FragmentCompletedTripDetails();
        fragmentCompletedTripDetails.context = context;
        fragmentCompletedTripDetails.tripId = tripId;
        fragmentCompletedTripDetails.tripStopDataInsertionCallback = tripStopDataInsertionCallback;
        return fragmentCompletedTripDetails;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_completed_trip_details, container, false);

        fabTripDone = (FloatingActionButton) view.findViewById(R.id.fab_tripDone);

        fabTripDone.setOnClickListener(this);

        etTotalAmount = (EditText) view.findViewById(R.id.et_totalTripAmount);

        etLaborAmount = (EditText) view.findViewById(R.id.et_laborAmount);
        etMemoAmount = (EditText) view.findViewById(R.id.et_memoAmount);

        cbLabor = (CheckBox) view.findViewById(R.id.cb_labor);
        cbMemo = (CheckBox) view.findViewById(R.id.cb_memo);

        cbMemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    etMemoAmount.setEnabled(true);
                } else
                {
                    etMemoAmount.setText("");
                    etMemoAmount.setEnabled(false);
                }
            }
        });

        cbLabor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    etLaborAmount.setEnabled(true);
                } else
                {
                    etLaborAmount.setText("");
                    etLaborAmount.setEnabled(false);
                }
            }
        });

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

    @Override
    public void onClick(View view)
    {

        if (cbLabor.isChecked())
        {
            if (etLaborAmount.getText().toString().trim().isEmpty())
            {
                Toast.makeText(context, "Please Enter Labour Amount", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (cbMemo.isChecked())
        {
            if (etMemoAmount.getText().toString().trim().isEmpty())
            {
                Toast.makeText(context, "Please Enter Memo Amount", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (etTotalAmount.getText().toString().trim().isEmpty())
        {
            Toast.makeText(context, "Please Enter Cash Received", Toast.LENGTH_SHORT).show();
            return;
        }

        API.getInstance().stopTripAndSendDetails(tripId, tripDistanceDetails.getTripStartTime() + "",
                tripDistanceDetails.getTripStopTime() + "",
                tripDistanceDetails.getStartLatLng(),
                tripDistanceDetails.getStopLatLng(),
                tvCompletedTripDistance.getText().toString(),
                etMemoAmount.getText().toString(),
                etLaborAmount.getText().toString(),
                etTotalAmount.getText().toString(), OnInsertTripStopData);
    }

    public interface TripStopDataInsertionCallback
    {
        void dataInsertedSuccessfully();

        void failedToInsertTripStopData();
    }

}
