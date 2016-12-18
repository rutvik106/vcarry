package broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apimodels.TripDetails;
import extra.Utils;
import io.fusionbit.vcarry.Constants;
import io.realm.Realm;
import io.realm.RealmResults;

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
