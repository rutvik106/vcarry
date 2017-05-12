package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
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

import api.API;
import api.RetrofitCallbacks;
import apimodels.DriverDetails;
import extra.Utils;
import fcm.FCM;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.BIND_AUTO_CREATE;

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

    TransportRequestHandlerService mService;

    boolean mServiceBound = false;

    ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mServiceBound = true;
            mService = ((TransportRequestHandlerService.TransportRequestServiceBinder) iBinder)
                    .getService();
            try
            {
                mService.insertTripDataIntoRealmAndSetupAlarm(extra.getString("trip_id"));
                context.unbindService(mServiceConnection);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mServiceBound = false;
            mService = null;
        }
    };

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
                    showBigNotification(0, data.getString("title"), data.getString("message"));
                    break;
                case Constants.NotificationType.DRIVER_UNALLOCATED:
                    showBigNotification(0, data.getString("title"), data.getString("message"));
                    break;
                case Constants.NotificationType.GET_DRIVER_LOCATION:
                    sendLocationToCustomer();
                    break;
                case Constants.NotificationType.DRIVER_ALLOCATED:

                    final Intent intent = new Intent(context, ActivityTripDetails.class);
                    intent.putExtra(Constants.INTENT_EXTRA_TRIP_ID, extra.getString("trip_id"));

                    //extra.Log.i(TAG, "SETTING TRIP ID: " + tripId + " IN PENDING INTENT");

                    final PendingIntent pendingIntent = PendingIntent.getActivity(context,
                            Integer.valueOf(extra.getString("trip_id")),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    //start bound service now
                    Intent transportRequestHandlerService = new Intent(context,
                            TransportRequestHandlerService.class);
                    context.startService(transportRequestHandlerService);

                    //bind activity to service
                    context.bindService(transportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);

                    Utils.showSimpleNotification(context, Integer.valueOf(extra.getString("trip_id")),
                            data.getString("title"),
                            data.getString("message"), pendingIntent);
                    break;

                case Constants.NotificationType.SEND_LOCATION_SERVER:
                    Log.i(TAG, "SERVER REQUESTING LOCATION....");
                    sendLocationToServer();
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
                                fusedLocation.stopGettingLocation();
                                try
                                {
                                    extraDetails.put("lat", location.getLatitude());
                                    extraDetails.put("lng", location.getLongitude());

                                    final DriverDetails dd = Realm.getDefaultInstance()
                                            .where(DriverDetails.class).findFirst();

                                    if (dd != null)
                                    {
                                        if (dd.getMultiTrip().equals("1"))
                                        {
                                            extraDetails.put("driver_type", 1);
                                        } else
                                        {
                                            extraDetails.put("driver_type", 0);
                                        }
                                    } else
                                    {
                                        extraDetails.put("driver_type", -1);
                                    }

                                    new AsyncTask<Void, Void, Void>()
                                    {
                                        @Override
                                        protected Void doInBackground(Void... voids)
                                        {
                                            FCM.sendPushNotification(Constants.NotificationType.DRIVER_CURRENT_LOCATION,
                                                    "Motorist Location", "Motorist current location", customerDeviceToken, extraDetails);
                                            return null;
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

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

    private void sendLocationToServer()
    {

        if (fusedLocation == null)
        {
            Log.i(TAG, "GETTING LOCATION...");
            fusedLocation = new FusedLocation(context, new FusedLocation.ApiConnectionCallbacks(null)
            {
                @Override
                public void onConnected(@Nullable Bundle bundle)
                {
                    Log.i(TAG, "CONNECTED TO FUSED LOCATION API");
                    fusedLocation.startGettingLocation(new FusedLocation.GetLocation()
                    {
                        @Override
                        public void onLocationChanged(Location location)
                        {
                            Log.i(TAG, "GOT LOCATION...");
                            fusedLocation.stopGettingLocation();
                            try
                            {
                                Log.i(TAG, "SENDING LOCATION TO SERVER");
                                API.getInstance().updateDriverLatLng(extra.getString("driver_id"),
                                        location.getLatitude() + "," + location.getLongitude(),
                                        new RetrofitCallbacks<ResponseBody>()
                                        {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                                            {
                                                super.onResponse(call, response);
                                                if (response.isSuccessful())
                                                {
                                                    Log.i(TAG, "LOCATION SENT TO SERVER SUCCESSFULLY");
                                                }
                                            }
                                        });
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
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

