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

    private TextView tvAccountPaid, tvAccountUnpaid;

    private VHAccountSummaryCard(Context context, View itemView)
    {
        super(itemView);
        this.context = context;

        tvAccountPaid = (TextView) itemView.findViewById(R.id.tv_accountPaid);
        tvAccountUnpaid = (TextView) itemView.findViewById(R.id.tv_accountUnpaid);
    }

    public static VHAccountSummaryCard create(final Context context, final ViewGroup parent)
    {
        return new VHAccountSummaryCard(context, LayoutInflater.from(context)
                .inflate(R.layout.account_summary_card, parent, false));
    }

    public static void bind(final VHAccountSummaryCard vh, AccountSummary accountSummary)
    {
        vh.tvAccountPaid.setText(vh.context.getResources().getString(R.string.rs) + " " +
                accountSummary.getReceived());

        vh.tvAccountUnpaid.setText(vh.context.getResources().getString(R.string.rs) + " " +
                (accountSummary.getReceivable() - accountSummary.getReceived()));
    }

}
