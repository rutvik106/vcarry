package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

import firebase.TransportRequestHandler;

public class ActivityTransportRequest extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TransportRequestHandler.RequestDetailsCallback
{

    private String requestId;

    NotificationManager notificationManager;

    FusedLocation fusedLocation;

    TextView tvFrom, tvTo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "My_App");
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


        getRequestDetails();


    }

    private void getRequestDetails()
    {

        TransportRequestHandler.getRequestDetails(requestId, this);

    }


    private void acceptRequest()
    {
        fusedLocation = new FusedLocation(this, this, this);

        notificationManager.cancel(Integer.valueOf(requestId));
        finish();
    }

    private void rejectRequest()
    {
        notificationManager.cancel(Integer.valueOf(requestId));
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        fusedLocation.startGettingLocation(new FusedLocation.GetLocation()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                TransportRequestHandler.acceptRequest(requestId, location.getLatitude() + "," + location.getLongitude()
                        , null);

                fusedLocation.stopGettingLocation();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        TransportRequestHandler.acceptRequest(requestId, null, null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        TransportRequestHandler.acceptRequest(requestId, null, null);
    }

    @Override
    public void OnGetRequestDetails(DataSnapshot dataSnapshot)
    {
        Map details = new HashMap();
        details = (Map) dataSnapshot.getValue();
        tvFrom.setText(getResources().getString(R.string.request_from) + ": " + details.get("from").toString());
        tvTo.setText(getResources().getString(R.string.request_to) + ": " + details.get("to").toString());
    }
}
