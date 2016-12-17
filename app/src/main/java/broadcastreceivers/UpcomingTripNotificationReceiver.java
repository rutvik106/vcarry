package broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import extra.Utils;
import io.fusionbit.vcarry.Constants;

/**
 * Created by rutvik on 12/17/2016 at 9:34 AM.
 */

public class UpcomingTripNotificationReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final int id = intent.getIntExtra(Constants.INTENT_EXTRA_TRIP_ID, 0);

        final String time = intent.getStringExtra(Constants.INTENT_EXTRA_TIME);

        if (id != 0)
        {
            Utils.showSimpleNotification(context, id, "Upcoming Trip!", "You have a trip at " + time);
        }
    }

}
