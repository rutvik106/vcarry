package viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import apimodels.TripsByDriverMail;
import extra.Utils;
import io.fusionbit.vcarry.ActivityTripDetails;
import io.fusionbit.vcarry.Constants;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 11/27/2016 at 4:05 PM.
 */

public class VHSingleTrip extends RecyclerView.ViewHolder
{

    final Context context;

    TripsByDriverMail model;

    TextView tvTripFrom, tvTripTo, tvTripTime, tvCurrentTripStatus;

    LinearLayout llContainer;

    public VHSingleTrip(final Context context, View itemView)
    {
        super(itemView);
        this.context = context;

        tvTripFrom = (TextView) itemView.findViewById(R.id.tv_tripFrom);
        tvTripTo = (TextView) itemView.findViewById(R.id.tv_tripTo);
        tvTripTime = (TextView) itemView.findViewById(R.id.tv_tripTime);
        tvCurrentTripStatus = (TextView) itemView.findViewById(R.id.tv_currentTripStatus);

        llContainer = (LinearLayout) itemView.findViewById(R.id.ll_singleTripContainer);

        llContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(context, ActivityTripDetails.class);
                i.putExtra(Constants.INTENT_EXTRA_TRIP_ID, model.getTripId());
                context.startActivity(i);
            }
        });

    }

    public static VHSingleTrip create(final Context context, final ViewGroup parent)
    {
        return new VHSingleTrip(context, LayoutInflater.from(context)
                .inflate(R.layout.single_trip_view, parent, false));
    }

    public static void bind(final VHSingleTrip vh, final TripsByDriverMail model)
    {
        vh.model = model;
        vh.tvTripFrom.setText(model.getFromShippingLocation());
        vh.tvTripTo.setText(model.getToShippingLocation());
        vh.tvTripTime.setText(Utils.convertDateToRequireFormat(model.getTripDatetime()));
        vh.tvCurrentTripStatus.setText(model.getStatus());

        final int tripStatus = Integer.valueOf(model.getTripStatus());
        final int tripStatusStarted = Integer.valueOf(Constants.TRIP_STATUS_TRIP_STARTED);
        final int tripStarted = Integer.valueOf(Constants.TRIP_STATUS_TRIP_STARTED);

        if (tripStatus < tripStatusStarted)
        {
            vh.tvCurrentTripStatus.setTextColor(Color.parseColor("#66bb6a"));
        } else if (tripStatus == tripStarted)
        {
            vh.tvCurrentTripStatus.setTextColor(Color.parseColor("#ffa726"));
        } else
        {
            vh.tvCurrentTripStatus.setTextColor(Color.parseColor("#666666"));
        }
    }


}
