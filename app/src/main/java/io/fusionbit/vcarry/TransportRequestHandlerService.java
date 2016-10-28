package io.fusionbit.vcarry;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import extra.Log;
import firebase.TransportRequestHandler;

/**
 * Created by rutvik on 10/27/2016 at 2:54 PM.
 */

public class TransportRequestHandlerService extends Service implements TransportRequestHandler.TransportRequestListener
{
    private static final String TAG =
            App.APP_TAG + TransportRequestHandlerService.class.getSimpleName();

    IBinder transportRequestServiceBinder = new TransportRequestServiceBinder();

    private static final int NOTIFICATION_ID = 246;

    private TransportRequestHandler transportRequestHandler;

    private int childCount = 0;


    public TransportRequestHandlerService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            if (transportRequestHandler == null)
            {
                transportRequestHandler = new TransportRequestHandler(this);
                addNotification();
            }
        }

        return START_STICKY;
    }


    private void addNotification()
    {
        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                .setContentTitle("V-Carry")
                .setContentText("Listening for Transport Request...")
                .setSmallIcon(R.drawable.logo_small);

        // create the pending intent and add to the notification
        Intent intent = new Intent(this, ActivityHome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        m_notificationBuilder.setContentIntent(pendingIntent);

        // send the notification
        startForeground(NOTIFICATION_ID, m_notificationBuilder.build());

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return transportRequestServiceBinder;
    }

    @Override
    public void OnReceiveNewTransportRequest(DataSnapshot dataSnapshot)
    {
        Log.i(TAG, "New Request arrived!!!!");

        //showNotification();
        showAlert();

    }

    @Override
    public void OnRequestChanged()
    {
        Log.i(TAG, "Request changed!!!!");
    }

    @Override
    public void OnRequestRemoved()
    {

    }


    public class TransportRequestServiceBinder extends Binder
    {
        public TransportRequestHandlerService getService()
        {
            return TransportRequestHandlerService.this;
        }
    }

    private void showAlert()
    {
        Intent i = new Intent(this, ActivityTransportRequest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void showNotification()
    {

        Intent intent = new Intent(this, ActivityHome.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(this)
                .setContentTitle("Transport Request")
                .setContentText("New Transport request form V-Carry")
                .setSmallIcon(R.drawable.logo_small)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 100})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.drawable.ic_done_black_24dp, "Accept", pIntent)
                .addAction(R.drawable.ic_clear_black_24dp, "Ignore", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

    }


}
