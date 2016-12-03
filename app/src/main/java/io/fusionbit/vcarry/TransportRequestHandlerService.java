package io.fusionbit.vcarry;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;

import extra.Log;
import firebase.TransportRequestHandler;
import models.TripDistanceDetails;

import static io.fusionbit.vcarry.Constants.NOTIFICATION_ID;

/**
 * Created by rutvik on 10/27/2016 at 2:54 PM.
 */

public class TransportRequestHandlerService extends Service implements TransportRequestHandler.TransportRequestListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, FusedLocation.GetLocation
{
    private static final String TAG =
            App.APP_TAG + TransportRequestHandlerService.class.getSimpleName();

    IBinder transportRequestServiceBinder = new TransportRequestServiceBinder();

    private TransportRequestHandler transportRequestHandler;

    private int childCount = 0;

    private TransportRequestResponseReceiver transportRequestResponseReceiver;

    FusedLocation mFusedLocation;

    TripDistanceDetails tripDistanceDetails;

    public TransportRequestHandlerService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        final String tripId = intent.getStringExtra(Constants.CURRENT_TRIP_ID);
        if (tripId != null)
        {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(Constants.CURRENT_TRIP_ID, tripId)
                    .apply();
        }


        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            if (transportRequestHandler == null)
            {
                addNotification();
                addTripNotification();
                startCalculatingDistanceIfDriverOnTrip();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        transportRequestResponseReceiver = new TransportRequestResponseReceiver();
        registerReceiver(transportRequestResponseReceiver,
                new IntentFilter(Constants.TRANSPORT_REQUEST_RESPONSE));
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(transportRequestResponseReceiver);
        super.onDestroy();
    }

    private void addNotification()
    {
        final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);
        if (isOnTrip)
        {
            return;
        }

        transportRequestHandler = new TransportRequestHandler(this);

        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                .setContentTitle("V-Carry")
                .setContentText("Listening for Transport Request...")
                .setSmallIcon(R.drawable.logo_small);


        // create the pending intent and add to the notification
        Intent intent = new Intent(this, ActivityHome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        m_notificationBuilder.setContentIntent(pendingIntent);

        // send the notification
        startForeground(NOTIFICATION_ID, m_notificationBuilder.build());

    }

    private void addTripNotification()
    {

        final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);
        if (isOnTrip)
        {

            final String tripId = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(Constants.CURRENT_TRIP_ID, "");

            Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                    .setContentTitle("V-Carry")
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setTicker("Trip has been started")
                    .setContentText("You are on a trip")
                    .setSmallIcon(R.drawable.logo_small);

            // create the pending intent and add to the notification
            Intent intent = new Intent(this, ActivityHome.class);
            intent.putExtra(Constants.CURRENT_TRIP_ID, tripId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            m_notificationBuilder.setContentIntent(pendingIntent);

            // send the notification
            startForeground(NOTIFICATION_ID, m_notificationBuilder.build());
        }
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

        showNotification(dataSnapshot.getKey());
        showAlert(dataSnapshot.getKey());

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

    private void showAlert(String requestId)
    {
        Intent i = new Intent(this, ActivityTransportRequest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("REQUEST_ID", requestId);
        startActivity(i);
    }

    private void showNotification(String requestId)
    {
        final int mRequestId = Integer
                .valueOf(requestId);

        Intent accept = new Intent(Constants.TRANSPORT_REQUEST_RESPONSE);
        accept.putExtra(Constants.T_RESPONSE, Constants.ACCEPT);
        accept.putExtra(Constants.TRANSPORT_REQUEST_ID, requestId);

        PendingIntent pAccept = PendingIntent
                .getBroadcast(this, mRequestId, accept, 0);

        Intent reject = new Intent(Constants.TRANSPORT_REQUEST_RESPONSE);
        reject.putExtra(Constants.T_RESPONSE, Constants.REJECT);
        reject.putExtra(Constants.TRANSPORT_REQUEST_ID, requestId);

        PendingIntent pReject = PendingIntent
                .getBroadcast(this, mRequestId, reject, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(this)
                .setContentTitle("Transport Request")
                .setContentText("New Transport request form V-Carry")
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                //.setContentIntent(pIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 100})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.drawable.ic_done_black_24dp, "Accept", pAccept)
                .addAction(R.drawable.ic_clear_black_24dp, "Reject", pReject).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(mRequestId, n);

    }


    public void startCalculatingDistanceIfDriverOnTrip()
    {
        final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);
        if (isOnTrip)
        {
            if (mFusedLocation == null)
            {
                final String tripId = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(Constants.CURRENT_TRIP_ID, "");

                tripDistanceDetails = new TripDistanceDetails(tripId, Calendar.getInstance().getTimeInMillis());
                mFusedLocation = new FusedLocation(this, this, this);
            }
        } else
        {
            if (mFusedLocation != null)
            {
                mFusedLocation.stopGettingLocation();
                Toast.makeText(this, "TOTAL DISTANCE in m: " + tripDistanceDetails.getDistanceTravelled(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, "Fused Location API Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Toast.makeText(this, "Fused Location API CONNECTED", Toast.LENGTH_SHORT).show();

        mFusedLocation.startGettingLocation(this);

    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Toast.makeText(this, "Fused Location API Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        tripDistanceDetails.addLocationData(location.getTime(), location.getLatitude(), location.getLongitude());
    }

}
