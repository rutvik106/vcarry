package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import apimodels.TripsByDriverMail;
import viewholders.VHSingleTrip;

/**
 * Created by rutvik on 11/27/2016 at 4:03 PM.
 */

public class TripsAdapter extends RecyclerView.Adapter
{

    final List<TripsByDriverMail> tripsByDriverMailList;

    final Context context;

    public TripsAdapter(final Context context)
    {
        this.context = context;
        tripsByDriverMailList = new LinkedList<>();
    }

    public void addTrip(TripsByDriverMail tripsByDriverMail)
    {
        tripsByDriverMailList.add(tripsByDriverMail);
        notifyItemInserted(tripsByDriverMailList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return VHSingleTrip.create(context, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        VHSingleTrip.bind((VHSingleTrip) holder, tripsByDriverMailList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return tripsByDriverMailList.size();
    }
}
