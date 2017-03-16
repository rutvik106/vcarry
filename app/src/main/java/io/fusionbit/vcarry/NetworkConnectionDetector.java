package io.fusionbit.vcarry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rutvik on 3/17/2017 at 12:03 AM.
 */

public class NetworkConnectionDetector extends BroadcastReceiver
{
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public NetworkConnectionDetector()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            if (connectivityReceiverListener != null)
            {
                connectivityReceiverListener.onNetworkConnectionChanged(true);
            }
        } else
        {
            if (connectivityReceiverListener != null)
            {
                connectivityReceiverListener.onNetworkConnectionChanged(false);
            }
        }
    }

    public interface ConnectivityReceiverListener
    {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}
