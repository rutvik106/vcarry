package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apimodels.TripDetails;
import viewholders.VHSingleTripNotification;

/**
 * Created by rutvik on 12/22/2016 at 7:16 PM.
 */

public class TripNotificationAdapter extends RecyclerView.Adapter
{

    final List<TripDetails> tripDetailsList;

    final Context context;

    public TripNotificationAdapter(final Context context)
    {
        this.context = context;
        tripDetailsList = new ArrayList<>();
    }

    public void addTripDetails(final TripDetails tripDetails)
    {
        tripDetailsList.add(tripDetails);
        notifyItemInserted(tripDetailsList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return VHSingleTripNotification.create(context, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        VHSingleTripNotification.bind((VHSingleTripNotification) holder,
                tripDetailsList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return tripDetailsList.size();
    }

    public void clear()
    {
        tripDetailsList.clear();
        notifyDataSetChanged();
    }

}
