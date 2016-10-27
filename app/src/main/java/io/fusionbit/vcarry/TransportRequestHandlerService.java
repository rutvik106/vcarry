package io.fusionbit.vcarry;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

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

    public TransportRequestHandlerService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {

            transportRequestHandler = new TransportRequestHandler(this);

            addNotification();
        }

        return START_STICKY;
    }


    private void addNotification()
    {
        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                .setContentTitle("V-Carry")
                .setContentText("Listening for Transport Request...")
                .setSmallIcon(R.mipmap.ic_launcher);

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
    public void OnReceiveNewTransportRequest()
    {

    }

    @Override
    public void OnRequestChanged()
    {

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

}
