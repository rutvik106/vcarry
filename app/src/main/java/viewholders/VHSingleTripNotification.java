package viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import apimodels.TripDetails;
import extra.Utils;
import io.fusionbit.vcarry.ActivityTransportRequest;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.Constants;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 12/22/2016 at 7:28 PM.
 */

public class VHSingleTripNotification extends RecyclerView.ViewHolder
{

    private static final String TAG = App.APP_TAG + VHSingleTripNotification.class.getSimpleName();

    final Context context;

    TripDetails model;

    TextView tvTripFrom, tvTripTo, tvTripTime;

    LinearLayout llContainer;

    public VHSingleTripNotification(final Context context, View itemView)
    {
        super(itemView);
        this.context = context;

        tvTripFrom = (TextView) itemView.findViewById(R.id.tv_tripFrom);
        tvTripTo = (TextView) itemView.findViewById(R.id.tv_tripTo);
        tvTripTime = (TextView) itemView.findViewById(R.id.tv_tripTime);

        llContainer = (LinearLayout) itemView.findViewById(R.id.ll_singleTripContainer);

        llContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openTripAlertActivity();
            }
        });

    }

    /**
     * private void checkIfTripAcceptedOrRejected()
     * {
     * final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
     * <p>
     * RetrofitCallbacks<ResponseBody> onGetTripAcceptedRejectedStatus = new RetrofitCallbacks<ResponseBody>()
     * {
     *
     * @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
     * {
     * super.onResponse(call, response);
     * if (response.isSuccessful())
     * {
     * try
     * {
     * if (response.body().string().equals("accepted"))
     * {
     * Toast.makeText(context, "Trip already accepted", Toast.LENGTH_SHORT).show();
     * } else if (response.body().string().equals("rejected"))
     * {
     * Toast.makeText(context, "You have already rejected this trip", Toast.LENGTH_SHORT).show();
     * } else if (response.body().string().contains("no action"))
     * {
     * Log.i(TAG, "OPENING ALERT ACTIVITY!!!");
     * openTripAlertActivity();
     * } else
     * {
     * openTripAlertActivity();
     * }
     * } catch (IOException e)
     * {
     * e.printStackTrace();
     * }
     * }
     * }
     * };
     * <p>
     * API.getInstance().getAcceptedRejectedStatus(email, model.getTripId(),
     * onGetTripAcceptedRejectedStatus);
     * <p>
     * }
     */

    private void openTripAlertActivity()
    {
        Intent i = new Intent(context, ActivityTransportRequest.class);
        i.putExtra("REQUEST_ID", model.getTripId());
        i.putExtra(Constants.INTENT_EXTRA_TIME, model.getTripDatetime());
        i.putExtra(Constants.INTENT_EXTRA_FROM, model.getFromShippingLocation());
        i.putExtra(Constants.INTENT_EXTRA_TO, model.getToShippingLocation());
        context.startActivity(i);
    }

    public static VHSingleTripNotification create(final Context context, final ViewGroup parent)
    {
        return new VHSingleTripNotification(context, LayoutInflater.from(context)
                .inflate(R.layout.single_notification_trip_row, parent, false));
    }

    public static void bind(final VHSingleTripNotification vh, final TripDetails model)
    {
        vh.model = model;
        vh.tvTripFrom.setText(model.getFromShippingLocation());
        vh.tvTripTo.setText(model.getToShippingLocation());
        vh.tvTripTime.setText(Utils.convertDateToRequireFormat(model.getTripDatetime()));
    }


}
