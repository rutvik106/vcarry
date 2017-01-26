package view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Map;

import apimodels.TripDetails;
import extra.Utils;
import firebase.TransportRequestHandler;
import io.fusionbit.vcarry.ActivityTransportRequest;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 12/29/2016 at 9:14 AM.
 */

public class NewTripAlert extends FrameLayout implements TransportRequestHandler.RequestDetailsCallback, App.TripNotificationListener
{
    private static final String TAG = App.APP_TAG + NewTripAlert.class.getSimpleName();

    private final String requestId;

    TextView tvFrom, tvTo, tvTime, tvFare;

    LinearLayout llAcceptRejectButtonContainer;

    final ActivityTransportRequest activityTransportRequest;

    public NewTripAlert(final String requestId,
                        final ActivityTransportRequest activityTransportRequest,
                        final Context context)
    {
        super(context);

        this.requestId = requestId;
        this.activityTransportRequest = activityTransportRequest;

        View v = inflate(context, R.layout.new_trip_alert_view, null);

        addView(v);

        this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        llAcceptRejectButtonContainer = (LinearLayout) findViewById(R.id.ll_acceptRejectButtonContainer);

        findViewById(R.id.btn_accept).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                llAcceptRejectButtonContainer.setVisibility(View.GONE);
                NewTripAlert.this
                        .activityTransportRequest.acceptRequest(requestId);
            }
        });

        findViewById(R.id.btn_reject).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                llAcceptRejectButtonContainer.setVisibility(View.GONE);
                NewTripAlert.this
                        .activityTransportRequest.rejectRequest(requestId);
            }
        });

        tvFrom = (TextView) findViewById(R.id.tv_from);
        tvTo = (TextView) findViewById(R.id.tv_to);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvFare = (TextView) findViewById(R.id.tv_fare);

        getRequestDetails();

    }

    private void getRequestDetails()
    {
        TransportRequestHandler.getRequestDetails(requestId, this);
    }

    @Override
    public void onGetRequestDetails(DataSnapshot dataSnapshot)
    {
        Map details = (Map) dataSnapshot.getValue();
        if (details != null)
        {
            tvFrom.setText(getResources().getString(R.string.request_from) + ": " + details.get("from").toString());
            tvTo.setText(getResources().getString(R.string.request_to) + ": " + details.get("to").toString());
            tvTime.setText(Utils.convertDateToRequireFormat(details.get("date_time").toString()));
            tvFare.setText(getResources().getString(R.string.rs) + " " + details.get("fare").toString());
        } else
        {
            onRequestDetailsNotFound(null);
        }
    }

    @Override
    public void onRequestDetailsNotFound(DatabaseError databaseError)
    {
        Log.i(TAG, "TRIP YOU ARE TRYING TO ACCEPT WAS NOT FOUND IN FIREBASE");
        Log.i(TAG, "TRYING TO GET IT FROM DATABASE");


        for (TripDetails tripDetails : ((App) activityTransportRequest.getApplication()).getTripNotificationList())
        {
            if (tripDetails.getTripId().equals(requestId))
            {
                setTripRequestData(tripDetails);
            }
        }

        ((App) activityTransportRequest.getApplication()).getTripNotifications(this);
    }

    @Override
    public void onGetTripNotificationList(List<TripDetails> tripDetailsList)
    {
        for (TripDetails tripDetails : tripDetailsList)
        {
            if (tripDetails.getTripId().equals(requestId))
            {
                setTripRequestData(tripDetails);
            }
        }
    }

    private void setTripRequestData(TripDetails tripDetails)
    {
        tvFrom.setText(getResources().getString(R.string.request_from) + ": "
                + tripDetails.getFromShippingLocation());
        tvTo.setText(getResources().getString(R.string.request_to) + ": "
                + tripDetails.getToShippingLocation());
        tvTime.setText(Utils.convertDateToRequireFormat(tripDetails.getTripDatetime()));

        tvFare.setText(getResources().getString(R.string.rs) + " "
                + tripDetails.getFare());

    }
}
