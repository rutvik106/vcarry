package io.fusionbit.vcarry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by rutvik on 3/15/2017 at 7:56 AM.
 */

public abstract class BaseActivity extends AppCompatActivity
{
    private NetworkConnectionDetector networkConnectionDetector;

    @Override
    protected void onStart()
    {
        super.onStart();
        IntentFilter i = new IntentFilter();
        i.addAction("android.net.wifi.STATE_CHANGE");
        i.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkConnectionDetector, i);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkInternet();
    }

    @Override
    protected void onStop()
    {
        if (networkConnectionDetector != null)
        {
            unregisterReceiver(networkConnectionDetector);
        }
        super.onStop();
    }

    public void checkInternet()
    {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            internetAvailable();
        } else
        {
            internetNotAvailable();
        }
    }

    protected abstract void internetNotAvailable();

    protected abstract void internetAvailable();


    public class NetworkConnectionDetector extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            checkInternet();
        }

    }

}
