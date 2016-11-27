package viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apimodels.TripsByDriverMail;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 11/27/2016 at 4:05 PM.
 */

public class VHSingleTrip extends RecyclerView.ViewHolder
{

    final Context context;

    TripsByDriverMail model;

    TextView tvTripFrom, tvTripTo, tvTripTime;

    public VHSingleTrip(Context context, View itemView)
    {
        super(itemView);
        this.context = context;

        tvTripFrom = (TextView) itemView.findViewById(R.id.tv_tripFrom);
        tvTripTo = (TextView) itemView.findViewById(R.id.tv_tripTo);
        tvTripTime = (TextView) itemView.findViewById(R.id.tv_tripTime);

    }

    public static VHSingleTrip create(final Context context, final ViewGroup parent)
    {
        return new VHSingleTrip(context, LayoutInflater.from(context)
                .inflate(R.layout.single_trip_view, parent, false));
    }

    public static void bind(final VHSingleTrip vh, final TripsByDriverMail model)
    {
        vh.model = model;

        vh.tvTripFrom.setText(vh.context.getResources().getString(R.string.from) + ": " + model
                .getFromShippingLocation());

        vh.tvTripTo.setText(vh.context.getResources().getString(R.string.to) + ": " + model
                .getToShippingLocation());

        vh.tvTripTime.setText(vh.context.getResources().getString(R.string.time) + ": " + model
                .getTripDatetime());
    }


}
