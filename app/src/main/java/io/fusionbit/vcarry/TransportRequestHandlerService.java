package io.fusionbit.vcarry;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import api.API;
import api.RetrofitCallbacks;
import apimodels.DriverDetails;
import apimodels.LocationUpdateResponse;
import apimodels.TripDetails;
import broadcastreceivers.UpcomingTripNotificationReceiver;
import extra.Log;
import extra.Utils;
import firebase.TransportRequestHandler;
import io.realm.Realm;
import models.TripDistanceDetails;
import retrofit2.Call;
import retrofit2.Response;

import static io.fusionbit.vcarry.Constants.NOTIFICATION_ID;

/**
 * Created by rutvik on 10/27/2016 at 2:54 PM.
 */

public class TransportRequestHandlerService extends Service
        implements TransportRequestHandler.TransportRequestListener,
        GoogleApiClient.OnConnectionFailedListener,
        FusedLocation.GetLocation, TransportRequestHandler.ConfirmationListener {
    private static final String TAG =
            App.APP_TAG + TransportRequestHandlerService.class.getSimpleName();

    IBinder transportRequestServiceBinder = new TransportRequestServiceBinder();

    private TransportRequestHandler transportRequestHandler;

    private FusedLocation mFusedLocation;

    private TripDistanceDetails tripDistanceDetails;

    private ResultReceiver resultReceiver;

    private UpcomingTripNotificationReceiver upcomingTripNotificationReceiver;

    //private TransportRequestResponseReceiver transportRequestResponseReceiver;

    private String driverId;
    private Call<LocationUpdateResponse> updatingLocation;

    public TransportRequestHandlerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String contactNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

            getDriverIdByPhoneNumber(contactNo);
        }

        return START_STICKY;
    }

    private void getDriverIdByPhoneNumber(final String contactNo) {
        final RetrofitCallbacks<Integer> onGetDriverIdCallback =
                new RetrofitCallbacks<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() > 0) {
                                PreferenceManager
                                        .getDefaultSharedPreferences(TransportRequestHandlerService.this)
                                        .edit()
                                        .putString(Constants.DRIVER_ID, String.valueOf(response.body()))
                                        .apply();

                                driverId = String.valueOf(response.body());

                                if (transportRequestHandler == null) {
                                    transportRequestHandler = new TransportRequestHandler(TransportRequestHandlerService.this);
                                    addNotification(getString(R.string.service_listening));
                                }

                                TransportRequestHandler.setupConnectivityLogic();

                            } else {
                                PreferenceManager
                                        .getDefaultSharedPreferences(TransportRequestHandlerService.this)
                                        .edit()
                                        .putString(Constants.DRIVER_ID, null)
                                        .apply();
                                if (transportRequestHandler != null) {
                                    transportRequestHandler = null;
                                    addNotification(getString(R.string.driver_not_registered));
                                }

                                TransportRequestHandler.setupConnectivityLogic();
                            }
                        }
                    }
                };

        API.getInstance().getDriverIdByDriverContact(contactNo, onGetDriverIdCallback);

    }

    public void setResultReceiver(ResultReceiver resultReceiver) {
        this.resultReceiver = resultReceiver;
    }

    @Override
    public void onCreate() {
        /*transportRequestResponseReceiver = new TransportRequestResponseReceiver()
        {
            @Override
            public void tripAcceptedSuccessfully(String tripId)
            {
                super.tripAcceptedSuccessfully(tripId);
                startListeningForTripConfirmation(tripId);
            }

            @Override
            public void failedToAcceptTrip(String tripId, String location, String acceptedTime, DatabaseError databaseError)
            {
                super.failedToAcceptTrip(tripId, location, acceptedTime, databaseError);
            }
        };*/


        /*registerReceiver(transportRequestResponseReceiver,
                new IntentFilter(Constants.TRANSPORT_REQUEST_RESPONSE));*/

        upcomingTripNotificationReceiver = new UpcomingTripNotificationReceiver();

        registerReceiver(upcomingTripNotificationReceiver,
                new IntentFilter(Constants.Broadcast.UPCOMING_TRIP_NOTIFICATION));
    }

    //Not Using this function As we are receiving FCM notification

    /**
     * public void startListeningForTripConfirmation(final String tripId)
     * {
     * TransportRequestHandler.startListeningForTripConfirmation(tripId, TransportRequestHandlerService.this);
     * }
     */

    @Override
    public void onDestroy() {
        //unregisterReceiver(transportRequestResponseReceiver);
        unregisterReceiver(upcomingTripNotificationReceiver);
        super.onDestroy();
    }

    private void addNotification(String message) {
        final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);
        if (isOnTrip) {
            addTripNotification();
            startCalculatingDistanceIfDriverOnTrip();
            return;
        }

        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                .setContentTitle("V-Carry")
                .setContentText(message)
                .setSmallIcon(R.drawable.logo_small);


        // create the pending intent and add to the notification
        Intent intent = new Intent(this, ActivityHome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        m_notificationBuilder.setContentIntent(pendingIntent);

        // send the notification
        startForeground(NOTIFICATION_ID, m_notificationBuilder.build());

    }

    private void addTripNotification() {
        Log.i(TAG, "ADDING START TRIP NOTIFICATION");

        final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);
        if (isOnTrip) {

            final String tripId = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(Constants.CURRENT_TRIP_ID, "");

            Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                    .setContentTitle("V-Carry")
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(getResources().getString(R.string.you_are_on_a_trip))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_local_shipping_black_24dp);

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
    public IBinder onBind(Intent intent) {
        return transportRequestServiceBinder;
    }

    public void startTrip(String tripId) {
        Log.i(TAG, "SERVICE IS STARTING TRIP NOW");

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(Constants.CURRENT_TRIP_ID, tripId)
                .putBoolean(Constants.IS_DRIVER_ON_TRIP, true)
                .apply();

        tripDistanceDetails = new TripDistanceDetails(tripId, Calendar.getInstance().getTimeInMillis());

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tripDistanceDetails);
        realm.commitTransaction();

        addTripNotification();
        startCalculatingDistanceIfDriverOnTrip();
    }

    public void stopTrip(String tripId) {
        Log.i(TAG, "STOPPING TRIP WITH TRIP ID: " + tripId);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.IS_DRIVER_ON_TRIP, false)
                .apply();
        addNotification(getString(R.string.service_listening));
        startCalculatingDistanceIfDriverOnTrip();
    }

    public void cancelTrip(final String tripId) {
        Log.i(TAG, "CANCELING THIS TRIP ID: " + tripId);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.IS_DRIVER_ON_TRIP, false)
                .apply();
        addNotification(getString(R.string.service_listening));

        if (!tripId.isEmpty()) {
            if (mFusedLocation != null) {
                Log.i(TAG, "DESTROYING FUSED LOCATION PROVIDER");
                mFusedLocation.stopGettingLocation();
                mFusedLocation = null;
            }
            Log.i(TAG, "DESTROYING TRIP DISTANCE DETAILS OBJECT");
            tripDistanceDetails = null;
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(Constants.CURRENT_TRIP_ID, "")
                    .apply();
            Log.i(TAG, "CURRENT TRIP ID SET TO EMPTY");
            resultReceiver.send(Constants.ON_TRIP_STOPPED, null);
        }

    }

    @Override
    public void OnReceiveNewTransportRequest(DataSnapshot dataSnapshot) {
        Log.i(TAG, "New Request arrived!!!!");

        final String driverId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.DRIVER_ID, null);

        if (driverId == null) {
            return;
        }

        final Realm realm = Realm.getDefaultInstance();
        final DriverDetails driverDetails
                = realm.where(DriverDetails.class).equalTo("driverId", driverId).findFirst();

        if (driverDetails == null) {
            return;
        }

        TransportRequestHandler.getRequestDetails(dataSnapshot.getKey(), new TransportRequestHandler.RequestDetailsCallback() {
            @Override
            public void onGetRequestDetails(DataSnapshot dataSnapshot) {
                Map details = (Map) dataSnapshot.getValue();
                if (details != null) {
                    if (details.get("vehicle") != null) {
                        if (!driverDetails.getMultiTrip().equals("1")) {
                            if (driverDetails.getVehicleType().equals(details.get("vehicle"))) {
                                showNotification(dataSnapshot.getKey());
                                showAlert(dataSnapshot.getKey());
                            }
                        } else {
                            showNotification(dataSnapshot.getKey());
                            showAlert(dataSnapshot.getKey());
                        }
                    }
                }
            }

            @Override
            public void onRequestDetailsNotFound(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void OnRequestChanged() {
        Log.i(TAG, "Request changed!!!!");
    }

    @Override
    public void OnRequestRemoved() {

    }

    @Override
    public void tripConfirmed(String tripId) {

        final Intent intent = new Intent(this, ActivityTripDetails.class);
        intent.putExtra(Constants.INTENT_EXTRA_TRIP_ID, tripId);

        Log.i(TAG, "SETTING TRIP ID: " + tripId + " IN PENDING INTENT");

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.valueOf(tripId),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        insertTripDataIntoRealmAndSetupAlarm(tripId);

        Utils.showSimpleNotification(this, Integer.valueOf(tripId),
                getResources().getString(R.string.trip_confirm_notification_title),
                getResources().getString(R.string.trip_confirm_notification_message), pendingIntent);
    }

    public void insertTripDataIntoRealmAndSetupAlarm(final String tripId) {
        final RetrofitCallbacks<TripDetails> callback = new RetrofitCallbacks<TripDetails>() {

            @Override
            public void onResponse(Call<TripDetails> call, final Response<TripDetails> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(response.body());

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            setupTripAlarm(tripId);
                        }
                    });
                }
            }
        };

        API.getInstance().getTripDetailsByTripId(tripId, callback);

    }

    private void setupTripAlarm(final String tripId) {
        final Realm realm = Realm.getDefaultInstance();
        final TripDetails tripDetails =
                realm.where(TripDetails.class)
                        .equalTo("tripId", tripId)
                        .findFirst();


        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date tripDate = sdf.parse(tripDetails.getTripDatetime());
            final long currentDate = Calendar.getInstance().getTimeInMillis();
            if (currentDate < tripDate.getTime() - (60 * (60 * 1000))) {
                Intent i = new Intent(this, UpcomingTripNotificationReceiver.class);
                i.putExtra(Constants.INTENT_EXTRA_TRIP_ID, tripId);
                i.putExtra(Constants.INTENT_EXTRA_TIME, Utils.getDateFromMills(tripDate.getTime()));
                PendingIntent pintent = PendingIntent.getBroadcast(this, Integer.valueOf(tripId),
                        i, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, tripDate.getTime() - (60 * (60 * 1000)), pintent);
                //Toast.makeText(this, "Trip Alarm was set!", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void tripNotConfirmed(String tripId) {
        Utils.showSimpleNotification(this, Integer.valueOf(tripId),
                getResources().getString(R.string.trip_not_confirm_notification_title),
                getResources().getString(R.string.trip_not_confirm_notification_message), null);
    }

    private void showAlert(String requestId) {
        Intent newTripRequestIntent = new Intent(Constants.NEW_TRIP_REQUEST);
        newTripRequestIntent.putExtra("REQUEST_ID", requestId);
        sendBroadcast(newTripRequestIntent);
        Intent i = new Intent(this, ActivityTransportRequest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("REQUEST_ID", requestId);
        startActivity(i);
    }

    private void showNotification(String requestId) {
        final int mRequestId = Integer
                .valueOf(requestId);

        /*final Intent accept = new Intent(Constants.TRANSPORT_REQUEST_RESPONSE);
        accept.putExtra(Constants.T_RESPONSE, Constants.ACCEPT);
        accept.putExtra(Constants.TRANSPORT_REQUEST_ID, requestId);*/

        /*final PendingIntent pAccept = PendingIntent
                .getBroadcast(this, mRequestId, accept, 0);*/

        /*final Intent reject = new Intent(Constants.TRANSPORT_REQUEST_RESPONSE);
        reject.putExtra(Constants.T_RESPONSE, Constants.REJECT);
        reject.putExtra(Constants.TRANSPORT_REQUEST_ID, requestId);*/

        /*final PendingIntent pReject = PendingIntent
                .getBroadcast(this, mRequestId, reject, 0);*/

        final Intent tripAlertActivity = new Intent(this, ActivityTransportRequest.class);
        tripAlertActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        tripAlertActivity.putExtra("REQUEST_ID", requestId);

        final PendingIntent showTripDetailsPendingIntent =
                PendingIntent.getActivity(this, mRequestId, tripAlertActivity,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // build notification
        // the addAction re-use the same intent to keep the example short
        NotificationCompat.Builder n = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.transport_request))
                .setContentText(getResources().getString(R.string.new_request))
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setContentIntent(showTripDetailsPendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                /*.addAction(R.drawable.ic_done_black_24dp, getResources().getString(R.string.accept), pAccept)
                .addAction(R.drawable.ic_clear_black_24dp, getResources().getString(R.string.reject), pReject)*/
        //.build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(mRequestId, n.build());

    }

    public void startCalculatingDistanceIfDriverOnTrip() {


        Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE");

        final boolean isOnTrip = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_DRIVER_ON_TRIP, false);

        final String tripId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.CURRENT_TRIP_ID, "");

        if (tripDistanceDetails == null) {
            //your phone might have turned off and trip details goes null
            // get it form realm

            final Realm realm = Realm.getDefaultInstance();

            final TripDistanceDetails td = realm.where(TripDistanceDetails.class).equalTo("tripId", tripId)
                    .findFirst();

            tripDistanceDetails = realm.copyFromRealm(td);

        }

        if (isOnTrip) {
            Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE: Motorist ON TRIP");
            if (mFusedLocation == null) {
                Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE: CREATING NEW FUSED LOCATION PROVIDER");
                mFusedLocation = new FusedLocation(this, new FusedLocation.ApiConnectionCallbacks(null) {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        /**Toast.makeText(TransportRequestHandlerService.this,
                         "Fused Location API CONNECTED", Toast.LENGTH_SHORT).show();*/
                        mFusedLocation.startGettingLocation(TransportRequestHandlerService.this);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        /**Toast.makeText(TransportRequestHandlerService.this,
                         "Fused Location API Connection Suspended", Toast.LENGTH_SHORT).show();*/
                    }
                }, this);
            }
        } else {
            Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE: Motorist NOT ON TRIP");
            if (mFusedLocation != null) {
                Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE: FUSED LOCATION PROVIDER IS NOT NULL");

                Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE: STOPPING LOCATION PROVIDER");

                mFusedLocation.stopGettingLocation();

                mFusedLocation = null;
            }

            tripDistanceDetails.stopTrip(Calendar.getInstance().getTimeInMillis());

            Log.i(TAG, "INSIDE START CALCULATING TRIP DISTANCE: WRITING TRIP DETAILS TO REALM");

            final Realm realm = Realm.getDefaultInstance();

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(tripDistanceDetails);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                    Log.i(TAG, "TRANSACTION SUCCESSFUL");
                    Bundle b = new Bundle();
                    b.putString(Constants.CURRENT_TRIP_ID, tripId);
                    resultReceiver.send(Constants.ON_TRIP_STOPPED, b);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {

                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(this, "Fused Location API FAILED TO CONNECT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "Location CHANGED!!!", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "******LOCATION CHANGED******");
        Log.i(TAG, "LAT: " + location.getLatitude());
        Log.i(TAG, "LNG: " + location.getLongitude());
        Log.i(TAG, "PROVIDER: " + location.getProvider());
        Log.i(TAG, "ACCURACY: " + location.getAccuracy());
        Log.i(TAG, "TIME in Sec: " + location.getTime() / 1000);
        Log.i(TAG, "SPEED: " + location.getSpeed());
        Log.i(TAG, "****************************");
        tripDistanceDetails.addLocationData(location.getTime(), location.getLatitude(), location.getLongitude());

        if (driverId != null) {
            if (updatingLocation != null) {
                if (!updatingLocation.isCanceled()) {
                    updatingLocation.cancel();
                }
                updatingLocation = null;
            }
            final String stringLocation = location.getLatitude() + "," + location.getLongitude();
            updatingLocation = API.getInstance().updateLocation(driverId, stringLocation, new RetrofitCallbacks<LocationUpdateResponse>() {

                @Override
                public void onResponse(Call<LocationUpdateResponse> call, Response<LocationUpdateResponse> response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getResponse().getError().getError_code() == 0) {
                                //Success
                            } else {
                                Crashlytics.log(android.util.Log.ERROR, "API", "RESPONSE: " + response.body().getResponse().getError().getMsg());
                            }
                        }
                    } else {
                        Crashlytics.log(android.util.Log.ERROR, "API", "RESPONSE CODE: " + response.code());
                    }
                }

            });
        }

    }

    public class TransportRequestServiceBinder extends Binder {
        public TransportRequestHandlerService getService() {
            return TransportRequestHandlerService.this;
        }

    }

}
