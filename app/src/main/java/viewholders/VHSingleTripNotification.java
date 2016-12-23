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
import io.fusionbit.vcarry.Constants;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 12/22/2016 at 7:28 PM.
 */

public class VHSingleTripNotification extends RecyclerView.ViewHolder
{

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
                Intent i = new Intent(context, ActivityTransportRequest.class);
                i.putExtra("REQUEST_ID", model.getTripId());
                i.putExtra(Constants.INTENT_EXTRA_TIME, model.getTripDatetime());
                i.putExtra(Constants.INTENT_EXTRA_FROM, model.getFromShippingLocation());
                i.putExtra(Constants.INTENT_EXTRA_TO, model.getToShippingLocation());
                context.startActivity(i);
            }
        });

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
