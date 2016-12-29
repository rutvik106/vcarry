package viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apimodels.AccountSummary;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 12/24/2016 at 9:09 AM.
 */

public class VHAccountSummaryCard extends RecyclerView.ViewHolder
{
    private final Context context;

    private TextView tvAccountPaidToday, tvAccountUnpaidToday,
            tvAccountPaidThisMonth, tvAccountUnpaidThisMonth, tvAccountPaidTotal,
            tvAccountUnpaidTotal, tvCompletedTripToday, tvCompletedTripThisMonth,
            tvCompletedTripTotal, tvIncompleteTripToday, tvIncompleteTripThisMonth,
            tvIncompleteTripTotal;

    private VHAccountSummaryCard(Context context, View itemView)
    {
        super(itemView);
        this.context = context;

        tvAccountPaidToday = (TextView) itemView.findViewById(R.id.tv_accountPaidToday);
        tvAccountUnpaidToday = (TextView) itemView.findViewById(R.id.tv_accountUnpaidToday);
        tvAccountPaidThisMonth = (TextView) itemView.findViewById(R.id.tv_accountPaidThisMonth);
        tvAccountUnpaidThisMonth = (TextView) itemView.findViewById(R.id.tv_accountUnpaidThisMonth);
        tvAccountPaidTotal = (TextView) itemView.findViewById(R.id.tv_accountPaidTotal);
        tvAccountUnpaidTotal = (TextView) itemView.findViewById(R.id.tv_accountUnpaidTotal);

        tvCompletedTripToday = (TextView) itemView.findViewById(R.id.tv_completedTripToday);
        tvCompletedTripThisMonth = (TextView) itemView.findViewById(R.id.tv_completedTripThisMonth);
        tvCompletedTripTotal = (TextView) itemView.findViewById(R.id.tv_completedTripTotal);

        tvIncompleteTripToday = (TextView) itemView.findViewById(R.id.tv_incompleteTripToday);
        tvIncompleteTripThisMonth = (TextView) itemView.findViewById(R.id.tv_incompleteTripThisMonth);
        tvIncompleteTripTotal = (TextView) itemView.findViewById(R.id.tv_incompleteTripTotal);
    }

    public static VHAccountSummaryCard create(final Context context, final ViewGroup parent)
    {
        return new VHAccountSummaryCard(context, LayoutInflater.from(context)
                .inflate(R.layout.account_summary_card, parent, false));
    }

    public static void bind(final VHAccountSummaryCard vh, AccountSummary accountSummary)
    {
        vh.tvAccountPaidToday.setText(vh.context.getResources().getString(R.string.rs) + " " +
                accountSummary.getReceivedToday());

        vh.tvAccountUnpaidToday.setText(vh.context.getResources().getString(R.string.rs) + " " +
                (accountSummary.getReceivableToday() - accountSummary.getReceivedToday()));

        vh.tvAccountPaidThisMonth.setText(vh.context.getResources().getString(R.string.rs) + " " +
                accountSummary.getReceivedThisMonth());

        vh.tvAccountUnpaidThisMonth.setText(vh.context.getResources().getString(R.string.rs) + " " +
                (accountSummary.getReceivableThisMonth() - accountSummary.getReceivedThisMonth()));

        vh.tvAccountPaidTotal.setText(vh.context.getResources().getString(R.string.rs) + " " +
                accountSummary.getTotalReceived());

        vh.tvAccountUnpaidTotal.setText(vh.context.getResources().getString(R.string.rs) + " " +
                (accountSummary.getTotalReceivable() - accountSummary.getTotalReceived()));

        vh.tvCompletedTripToday.setText(vh.context.getResources().getString(R.string.trip_completed) + " " +
                accountSummary.getTodayCompletedTrips());

        vh.tvCompletedTripThisMonth.setText(vh.context.getResources().getString(R.string.trip_completed) + " " +
                accountSummary.getThisMonthCompletedTrips());

        vh.tvCompletedTripTotal.setText(vh.context.getResources().getString(R.string.trip_completed) + " " +
                accountSummary.getTotalCompletedTrips());


        vh.tvIncompleteTripToday.setText(vh.context.getResources().getString(R.string.incomplete_trip) + " " +
                accountSummary.getTodayIncompleteTrips());

        vh.tvIncompleteTripThisMonth.setText(vh.context.getResources().getString(R.string.incomplete_trip) + " " +
                accountSummary.getThisMonthIncompleteTrips());

        vh.tvIncompleteTripTotal.setText(vh.context.getResources().getString(R.string.incomplete_trip) + " " +
                accountSummary.getTotalIncompleteTrips());

    }

}