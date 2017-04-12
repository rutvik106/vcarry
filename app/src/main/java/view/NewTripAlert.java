package view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Map;

import apimodels.TripDetails;
import extra.LocaleHelper;
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
    final ActivityTransportRequest activityTransportRequest;
    private final String requestId;
    private final LinearLayout llVehicleImageContainer;
    TextView tvFrom, tvTo, tvTime, tvFare, tvVehicle, tvWeight, tvDimension;
    LinearLayout llAcceptRejectButtonContainer;

    ProgressBar pbLoadingTripDetails;

    ImageView ivVehicleImage;

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

        llVehicleImageContainer = (LinearLayout) findViewById(R.id.ll_vehicleImageContainer);
        ivVehicleImage = (ImageView) findViewById(R.id.iv_vehicleImage);

        pbLoadingTripDetails = (ProgressBar) findViewById(R.id.pb_loadingTripDetails);

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
                promptForTripRejection();
            }
        });

        tvFrom = (TextView) findViewById(R.id.tv_from);
        tvTo = (TextView) findViewById(R.id.tv_to);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvFare = (TextView) findViewById(R.id.tv_fare);
        tvVehicle = (TextView) findViewById(R.id.tv_vehicle);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvDimension = (TextView) findViewById(R.id.tv_dimension);

        getRequestDetails();

    }

    private void promptForTripRejection()
    {
        new AlertDialog.Builder(activityTransportRequest)
                .setMessage(R.string.trip_rejection_prompt_msg)
                .setPositiveButton(R.string.reject, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        llAcceptRejectButtonContainer.setVisibility(View.GONE);
                        NewTripAlert.this
                                .activityTransportRequest.rejectRequest(requestId);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void getRequestDetails()
    {
        TransportRequestHandler.getRequestDetails(requestId, this);
    }

    @Override
    public void onGetRequestDetails(DataSnapshot dataSnapshot)
    {
        pbLoadingTripDetails.setVisibility(GONE);

        Map details = (Map) dataSnapshot.getValue();
        if (details != null)
        {
            if (LocaleHelper.getLanguage(activityTransportRequest).equalsIgnoreCase("gu"))
            {
                if (details.get("from_guj_address") != null && details.get("to_guj_address") != null)
                {
                    tvFrom.setText(getResources().getString(R.string.request_from) + ": " +
                            (details.get("from_guj_address").toString().isEmpty() ?
                                    details.get("from").toString() : details.get("from_guj_address").toString()));

                    tvTo.setText(getResources().getString(R.string.request_to) + ": " +
                            (details.get("to_guj_address").toString().isEmpty() ?
                                    details.get("to").toString() : details.get("to_guj_address").toString()));
                } else
                {
                    tvFrom.setText(getResources().getString(R.string.request_from) + ": " + details.get("from").toString());
                    tvTo.setText(getResources().getString(R.string.request_to) + ": " + details.get("to").toString());
                }
            } else
            {
                tvFrom.setText(getResources().getString(R.string.request_from) + ": " + details.get("from").toString());
                tvTo.setText(getResources().getString(R.string.request_to) + ": " + details.get("to").toString());
            }
            tvTime.setText(Utils.convertDateToRequireFormat(details.get("date_time").toString()));
            tvFare.setText(getResources().getString(R.string.rs) + " " + details.get("fare").toString());


            //SET VEHICLE
            if (details.get("vehicle") != null)
            {
                tvVehicle.setText(details.get("vehicle").toString());
                setVehicleImage(details.get("vehicle").toString());
            } else
            {
                tvVehicle.setVisibility(GONE);
            }
            //SET WEIGHT
            if (details.get("wt") != null)
            {
                if (!details.get("wt").toString().isEmpty())
                {
                    if (!details.get("wt").toString().equals("0"))
                    {
                        tvWeight.setVisibility(VISIBLE);
                        tvWeight.setText(details.get("wt").toString() + " Kg");
                    }
                }
            }
            //SET DIMENSIONS
            if (details.get("dimension") != null)
            {
                if (!details.get("dimension").toString().isEmpty())
                {
                    tvDimension.setVisibility(VISIBLE);
                    tvDimension.setText(details.get("dimension").toString());
                }
            }
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
        pbLoadingTripDetails.setVisibility(GONE);

        if (LocaleHelper.getLanguage(activityTransportRequest).equalsIgnoreCase("gu"))
        {
            Log.i(TAG, "setTripRequestData: LANGUAGE IS GUJARATI");
            tripDetails.setReturnGujaratiAddress(true);
        } else
        {
            Log.i(TAG, "setTripRequestData: LANGUAGE IS NOT GUJARATI");
        }

        tvFrom.setText(getResources().getString(R.string.request_from) + ": "
                + tripDetails.getFromShippingLocation());

        tvTo.setText(getResources().getString(R.string.request_to) + ": "
                + tripDetails.getToShippingLocation());

        tvTime.setText(Utils.convertDateToRequireFormat(tripDetails.getTripDatetime()));

        tvFare.setText(getResources().getString(R.string.rs) + " "
                + tripDetails.getFare());

        setVehicleImage(tripDetails.getVehicleType());

        if (tripDetails.getVehicleType() != null)
        {
            tvVehicle.setText(tripDetails.getVehicleType());
        } else
        {
            tvVehicle.setVisibility(GONE);
        }

        if (tripDetails.getWeight() != null)
        {
            if (!tripDetails.getWeight().isEmpty())
            {
                if (!tripDetails.getWeight().equals("0"))
                {
                    tvWeight.setVisibility(VISIBLE);
                    tvWeight.setText(tripDetails.getWeight() + " Kg");
                }
            }
        }

        if (tripDetails.getDimensions() != null)
        {
            if (!tripDetails.getDimensions().isEmpty())
            {
                tvDimension.setVisibility(VISIBLE);
                tvDimension.setText(tripDetails.getDimensions());
            }
        }

    }

    private void setVehicleImage(String vehicleType)
    {
        if (vehicleType != null)
        {
            if (!vehicleType.isEmpty())
            {
                llVehicleImageContainer.setVisibility(VISIBLE);
                switch (vehicleType)
                {
                    case "Pickup Bolero":
                        ivVehicleImage.setImageResource(R.drawable.bolero);
                        break;

                    case "Tata Ace":
                        ivVehicleImage.setImageResource(R.drawable.tata_ace);
                        break;

                    case "Tempo Trailer":
                        ivVehicleImage.setImageResource(R.drawable.eicher);
                        break;

                    case "Three Wheel Tempo":
                        ivVehicleImage.setImageResource(R.drawable.atul_loading);
                        break;

                    default:
                        llVehicleImageContainer.setVisibility(GONE);

                }
            }
        }
    }
}
