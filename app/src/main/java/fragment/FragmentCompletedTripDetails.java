package fragment;

import android.content.Context;
import android.os.AsyncTask;
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

import java.io.IOException;

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

    String memoAmount = "0", labourAmount = "0";

    double distance;

    boolean isReadOnlyMode;

    Call apiCall;

    RetrofitCallbacks OnInsertTripStopData = new RetrofitCallbacks<ResponseBody>()
    {

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
        {
            super.onResponse(call, response);
            if (response.isSuccessful())
            {
                try
                {
                    if (response.body().string().contains("error"))
                    {
                        tripStopDataInsertionCallback.failedToInsertTripStopData();
                    } else
                    {
                        tripStopDataInsertionCallback.dataInsertedSuccessfully();

                        final Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(tripDetails);
                        realm.copyToRealmOrUpdate(tripDistanceDetails);
                        realm.commitTransaction();

                    }
                } catch (IOException e)
                {
                    tripStopDataInsertionCallback.failedToInsertTripStopData();
                    e.printStackTrace();
                }
            } else
            {
                tripStopDataInsertionCallback.failedToInsertTripStopData();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t)
        {
            super.onFailure(call, t);
            tripStopDataInsertionCallback.failedToInsertTripStopData();
        }
    };

    public static FragmentCompletedTripDetails newInstance(final Context context, final String tripId,
                                                           final boolean isReadOnlyMode,
                                                           final TripStopDataInsertionCallback tripStopDataInsertionCallback)
    {
        FragmentCompletedTripDetails fragmentCompletedTripDetails = new FragmentCompletedTripDetails();
        fragmentCompletedTripDetails.context = context;
        fragmentCompletedTripDetails.tripId = tripId;
        fragmentCompletedTripDetails.isReadOnlyMode = isReadOnlyMode;
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

        cbMemo.setChecked(true);
        cbLabor.setChecked(true);

        etLaborAmount.setText("0");
        etMemoAmount.setText("0");

        etTotalAmount.setText("0");

        tvCompletedTripStartTime = (TextView) view.findViewById(R.id.tv_completedTripStartTime);
        tvCompletedTripEndTime = (TextView) view.findViewById(R.id.tv_completedTripEndTime);
        tvCompletedTripDistance = (TextView) view.findViewById(R.id.tv_completedTripDistance);
        tvCompletedTripFare = (TextView) view.findViewById(R.id.tv_completedTripFare);
        tvCompletedTripCustomerName = (TextView) view.findViewById(R.id.tv_completedTripCustomerName);
        //tvCompletedTripFrom = (TextView) view.findViewById(R.id.tv_completedTripFrom);
        //tvCompletedTripTo = (TextView) view.findViewById(R.id.tv_completedTripTo);

        if (isReadOnlyMode)
        {
            setToReadOnlyMode();
        }

        showCompletedTripDetails(tripId);

        return view;
    }

    public void showCompletedTripDetails(String tripId)
    {

        final Realm realm = Realm.getDefaultInstance();

        final TripDetails td = realm.where(TripDetails.class)
                .equalTo("tripId", tripId).findFirst();

        if (td != null)
        {
            tripDetails = realm.copyFromRealm(td);
        }

        final TripDistanceDetails tdd = realm.where(TripDistanceDetails.class)
                .equalTo("tripId", tripId).findFirst();

        if (tdd != null)
        {
            tripDistanceDetails = realm.copyFromRealm(tdd);
        }


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
        //Log.i(TAG, "tripDistanceDetails distance travelled: " + tripDistanceDetails.getDistanceTravelled());

        tvCompletedTripStartTime
                .setText(Utils.getDateFromMills(tripDistanceDetails.getTripStartTime()));

        tvCompletedTripEndTime
                .setText(Utils.getDateFromMills(tripDistanceDetails.getTripStopTime()));


        new AsyncTask<Void, Void, Double>()
        {

            final String tId = tripId;

            @Override
            protected Double doInBackground(Void... voids)
            {
                final Realm mRealm = Realm.getDefaultInstance();

                final TripDistanceDetails mTripDistanceDetails = mRealm.where(TripDistanceDetails.class)
                        .equalTo("tripId", tId).findFirst();

                return mTripDistanceDetails.getDistanceTravelled();
            }

            @Override
            protected void onPostExecute(Double aDouble)
            {
                distance = aDouble;
                tvCompletedTripDistance.setText(aDouble + " Km");
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        tvCompletedTripFare.setText(getResources().getString(R.string.rs) + " " + tripDetails.getFare());

        tvCompletedTripCustomerName.setText(tripDetails.getCustomerName());

        //tvCompletedTripFrom.setText(tripDetails.getFromShippingLocation());

        //tvCompletedTripTo.setText(tripDetails.getToShippingLocation());

        if (isReadOnlyMode)
        {
            setToReadOnlyMode();
        }

    }

    @Override
    public void onClick(View view)
    {
        if (tripDetails == null || tripDistanceDetails == null)
        {
            return;
        }

        if (apiCall != null)
        {
            apiCall.cancel();
        }

        if (cbLabor.isChecked())
        {
            if (etLaborAmount.getText().toString().trim().isEmpty())
            {
                Toast.makeText(context, R.string.enter_memo_amount, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (cbMemo.isChecked())
        {
            if (etMemoAmount.getText().toString().trim().isEmpty())
            {
                Toast.makeText(context, R.string.enter_labour_amount, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (etTotalAmount.getText().toString().trim().isEmpty())
        {
            Toast.makeText(context, R.string.enter_cash_received, Toast.LENGTH_SHORT).show();
            return;
        }


        if (etMemoAmount.getText() != null)
        {
            if (!etMemoAmount.getText().toString().isEmpty())
            {
                memoAmount = etMemoAmount.getText().toString();
                tripDistanceDetails.setMemoAmount(memoAmount);
            }
        }

        if (etLaborAmount.getText() != null)
        {
            if (!etLaborAmount.getText().toString().isEmpty())
            {
                labourAmount = etLaborAmount.getText().toString();
                tripDistanceDetails.setLabourAmount(labourAmount);
            }
        }

        tripDistanceDetails.setAmount(etTotalAmount.getText().toString());

        apiCall = API.getInstance().stopTripAndSendDetails(tripId, tripDistanceDetails.getTripStartTime() + "",
                tripDistanceDetails.getTripStopTime() + "",
                tripDistanceDetails.getStartLatLng(),
                tripDistanceDetails.getStopLatLng(),
                distance + "",
                memoAmount,
                labourAmount,
                etTotalAmount.getText().toString(), OnInsertTripStopData);
    }

    private void setToReadOnlyMode()
    {
        cbMemo.setEnabled(false);
        cbLabor.setEnabled(false);

        etTotalAmount.setEnabled(false);
        etMemoAmount.setEnabled(false);
        etLaborAmount.setEnabled(false);

        fabTripDone.setEnabled(false);
        fabTripDone.setVisibility(View.GONE);

        if (tripDistanceDetails == null)
        {
            return;
        }

        if (tripDistanceDetails.getAmount() != null)
        {
            etTotalAmount.setText(tripDistanceDetails.getAmount());
        }

        if (tripDistanceDetails.getLabourAmount() != null)
        {
            etLaborAmount.setText(tripDistanceDetails.getLabourAmount());
        }

        if (tripDistanceDetails.getMemoAmount() != null)
        {
            etMemoAmount.setText(tripDistanceDetails.getMemoAmount());
        }

    }

    public interface TripStopDataInsertionCallback
    {
        void dataInsertedSuccessfully();

        void failedToInsertTripStopData();
    }

}
