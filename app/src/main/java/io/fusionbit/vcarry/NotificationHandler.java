package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import fcm.FCM;

/**
 * Created by rutvik on 1/26/2017 at 6:45 PM.
 */

public class NotificationHandler
{
    private static final String TAG = App.APP_TAG + NotificationHandler.class.getSimpleName();

    Context context;

    RemoteMessage remoteMessage;

    JSONObject data;

    JSONObject extra;

    FusedLocation fusedLocation;

    public NotificationHandler(Context context, RemoteMessage remoteMessage)
    {
        this.context = context;
        this.remoteMessage = remoteMessage;
        try
        {
            this.data = new JSONObject(remoteMessage.getData().get("data"));
            this.extra = new JSONObject(remoteMessage.getData().get("extra"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void handleNotification()
    {

        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.d(TAG, "key, " + key + " value " + value);
        }

        try
        {
            switch (data.get("type").toString())
            {
                case Constants.NotificationType.SIMPLE:
                    showNotification(0, data.getString("title"), data.getString("message"));
                    break;
                case Constants.NotificationType.GET_DRIVER_LOCATION:
                    sendLocationToCustomer();
                    break;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void sendLocationToCustomer()
    {

        try
        {
            final String customerDeviceToken = extra.getString("customer_device_token");

            final JSONObject extraDetails = new JSONObject();

            if (fusedLocation == null)
            {
                fusedLocation = new FusedLocation(context, new FusedLocation.ApiConnectionCallbacks(null)
                {
                    @Override
                    public void onConnected(@Nullable Bundle bundle)
                    {
                        fusedLocation.startGettingLocation(new FusedLocation.GetLocation()
                        {
                            @Override
                            public void onLocationChanged(Location location)
                            {
                                try
                                {
                                    extraDetails.put("lat", location.getLatitude());
                                    extraDetails.put("lng", location.getLongitude());

                                    new AsyncTask<Void, Void, Void>()
                                    {
                                        @Override
                                        protected Void doInBackground(Void... voids)
                                        {
                                            FCM.sendPushNotification(Constants.NotificationType.DRIVER_CURRENT_LOCATION,
                                                    "Driver Location", "Driver current location", customerDeviceToken, extraDetails);
                                            return null;
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                fusedLocation.stopGettingLocation();
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i)
                    {

                    }
                }, new GoogleApiClient.OnConnectionFailedListener()
                {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {

                    }
                });

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private void showNotification(final int id, final String title, final String message)
    {
        Intent intent = new Intent(context, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_small)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    private void showBigNotification(final int id, final String title, final String message)
    {
        Intent intent = new Intent(context, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_small)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

}

