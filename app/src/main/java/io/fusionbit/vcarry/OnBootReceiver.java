package io.fusionbit.vcarry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by rutvik on 11/16/2016 at 1:06 PM.
 */

public class OnBootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            //start bound service now
            Intent TransportRequestHandlerService = new Intent(context, TransportRequestHandlerService.class);
            context.startService(TransportRequestHandlerService);
        }
    }
}
