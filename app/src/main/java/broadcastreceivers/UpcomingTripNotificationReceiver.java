package broadcastreceivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import extra.Utils;
import io.fusionbit.vcarry.ActivityTripDetails;
import io.fusionbit.vcarry.Constants;

/**
 * Created by rutvik on 12/17/2016 at 9:34 AM.
 */

public class UpcomingTripNotificationReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        final String tripId = intent.getStringExtra(Constants.INTENT_EXTRA_TRIP_ID);

        final String time = intent.getStringExtra(Constants.INTENT_EXTRA_TIME);

        if (!tripId.isEmpty())
        {

            final Intent tripDetails = new Intent(context, ActivityTripDetails.class);
            tripDetails.putExtra(Constants.INTENT_EXTRA_TRIP_ID, tripId);

            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tripDetails, 0);

            Utils.showSimpleNotification(context, Integer.valueOf(tripId),
                    "Upcoming Trip!", "You have a trip at " + time, pendingIntent);
        }
    }

}
