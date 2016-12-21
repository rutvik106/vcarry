package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Map;

import extra.Utils;
import firebase.TransportRequestHandler;

public class ActivityTransportRequest extends FusedLocation.LocationAwareActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        TransportRequestHandler.RequestDetailsCallback, TransportRequestHandler.TripAcceptedCallback
{

    private static final String TAG = App.APP_TAG + ActivityTransportRequest.class.getSimpleName();

    private String requestId;

    NotificationManager notificationManager;

    FusedLocation fusedLocation;

    TextView tvFrom, tvTo, tvTime;

    TransportRequestHandlerService mService;

    boolean mServiceBound = false;

    FusedLocationApiCallbacks fusedLocationApiCallbacks;

    PowerManager.WakeLock wl;

    ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mServiceBound = true;
            mService = ((TransportRequestHandlerService.TransportRequestServiceBinder) iBinder)
                    .getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mServiceBound = false;
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "My_App");
        wl.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        setContentView(R.layout.activity_transport_request);

        requestId = getIntent().getStringExtra("REQUEST_ID");

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        findViewById(R.id.btn_accept).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                acceptRequest();
            }
        });


        findViewById(R.id.btn_reject).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                rejectRequest();
            }
        });

        tvFrom = (TextView) findViewById(R.id.tv_from);
        tvTo = (TextView) findViewById(R.id.tv_to);
        tvTime = (TextView) findViewById(R.id.tv_time);


        getRequestDetails();


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //start bound service now
        Intent transportRequestHandlerService = new Intent(this, TransportRequestHandlerService.class);
        startService(transportRequestHandlerService);

        //bind activity to service
        bindService(transportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        if (mServiceBound)
        {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }

        super.onStop();
    }

    private void getRequestDetails()
    {
        TransportRequestHandler.getRequestDetails(requestId, this);
    }


    private void acceptRequest()
    {
        fusedLocationApiCallbacks = new FusedLocationApiCallbacks(this);
        fusedLocation = new FusedLocation(this, fusedLocationApiCallbacks
                , this);

        notificationManager.cancel(Integer.valueOf(requestId));
    }

    private void rejectRequest()
    {
        TransportRequestHandler.rejectRequest();
        notificationManager.cancel(Integer.valueOf(requestId));
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        TransportRequestHandler.acceptRequest(requestId, null, this);
    }

    @Override
    public void OnGetRequestDetails(DataSnapshot dataSnapshot)
    {
        Map details = (Map) dataSnapshot.getValue();
        tvFrom.setText(getResources().getString(R.string.request_from) + ": " + details.get("from").toString());
        tvTo.setText(getResources().getString(R.string.request_to) + ": " + details.get("to").toString());
        if (details.get("date_time") != null)
        {
            tvTime.setText(getResources().getString(R.string.time) + ": " + Utils.convertDateToRequireFormat(details.get("date_time").toString()));
        }
    }

    @Override
    public void tripAcceptedSuccessfully(String tripId)
    {
        Log.i(TAG, "TRIP ID: " + tripId + " WAS ACCEPTED SUCCESSFULLY");
        //Toast.makeText(this, "TRIP ACCEPTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
        mService.startListeningForTripConfirmation(tripId);
    }

    @Override
    public void failedToAcceptTrip(String tripId, String location, String
            acceptedTime, DatabaseError databaseError)
    {
        Log.i(TAG, "TRIP ID: " + tripId + " FAILED TO ACCEPT");
        //Toast.makeText(this, "FAILED TO ACCEPT TRIP", Toast.LENGTH_SHORT).show();
        //TransportRequestHandler.insertTripAcceptedDataUsingApi(tripId, location, acceptedTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void acceptTrip()
    {
        fusedLocation.startGettingLocation(new FusedLocation.GetLocation()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                TransportRequestHandler.acceptRequest(requestId, location.getLatitude() + "," + location.getLongitude()
                        , ActivityTransportRequest.this);

                fusedLocation.stopGettingLocation();
                finish();
            }
        });
    }

    @Override
    public void locationServiceAlreadyOn()
    {
        acceptTrip();
    }

    @Override
    public void locationServiceTurnedOn()
    {
        acceptTrip();
    }

    @Override
    public void locationSettingChangeUnavailable()
    {
        Toast.makeText(mService, "Please turn on location service", Toast.LENGTH_SHORT).show();
    }


    private class FusedLocationApiCallbacks extends FusedLocation.ApiConnectionCallbacks
    {

        public FusedLocationApiCallbacks(FusedLocation.LocationAwareActivity locationAwareActivity)
        {
            super(locationAwareActivity);
        }

        @Override
        public void onConnectionSuspended(int i)
        {

        }
    }

    @Override
    protected void onDestroy()
    {
        wl.release();
        super.onDestroy();
    }
}
