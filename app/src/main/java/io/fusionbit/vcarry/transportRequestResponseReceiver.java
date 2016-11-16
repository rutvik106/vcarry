package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import firebase.TransportRequestHandler;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by rutvik on 11/16/2016 at 2:32 PM.
 */

public class TransportRequestResponseReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String response = intent.getStringExtra(Constants.T_RESPONSE);
        final String requestId = intent.getStringExtra(Constants.TRANSPORT_REQUEST_ID);

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (response.equals(Constants.ACCEPT))
        {
            TransportRequestHandler.acceptRequest(requestId);
            notificationManager.cancel(Integer.valueOf(requestId.substring(requestId.length() - 4, requestId.length())));
        } else if (response.equals(Constants.REJECT))
        {
            notificationManager.cancel(Integer.valueOf(requestId.substring(requestId.length() - 4, requestId.length())));
        }
    }

}
