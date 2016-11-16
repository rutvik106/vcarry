package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import firebase.TransportRequestHandler;

public class ActivityTransportRequest extends AppCompatActivity
{

    private String requestId;

    NotificationManager notificationManager;

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

    }


    private void acceptRequest()
    {
        TransportRequestHandler.acceptRequest(requestId);
        notificationManager.cancel(Integer.valueOf(requestId.substring(requestId.length() - 4, requestId.length())));
        finish();
    }

    private void rejectRequest()
    {
        notificationManager.cancel(Integer.valueOf(requestId.substring(requestId.length() - 4, requestId.length())));
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
