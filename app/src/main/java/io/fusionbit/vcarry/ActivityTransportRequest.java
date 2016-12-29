package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseError;

import adapters.NewTripPagerAdapter;
import firebase.TransportRequestHandler;
import view.NewTripAlert;

public class ActivityTransportRequest extends FusedLocation.LocationAwareActivity
        implements GoogleApiClient.OnConnectionFailedListener, TransportRequestHandler.TripAcceptedCallback
{

    private static final String TAG = App.APP_TAG + ActivityTransportRequest.class.getSimpleName();


    NotificationManager notificationManager;

    FusedLocation fusedLocation;

    TransportRequestHandlerService mService;

    boolean mServiceBound = false;

    FusedLocationApiCallbacks fusedLocationApiCallbacks;

    PowerManager.WakeLock wl;

    String requestId = null;

    private ViewPager vpNewTripPager = null;
    private NewTripPagerAdapter adapter = null;

    NewTripRequestReceiver newTripRequestReceiver;

    ImageView ivPreviousTrip;
    ImageView ivNextTrip;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "My_App");
        wl.acquire();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        setContentView(R.layout.activity_transport_request);

        newTripRequestReceiver = new NewTripRequestReceiver();

        adapter = new NewTripPagerAdapter();
        vpNewTripPager = (ViewPager) findViewById(R.id.vp_newTripPager);
        vpNewTripPager.setAdapter(adapter);

        final String initialRequestId = getIntent().getStringExtra("REQUEST_ID");

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        addView(new NewTripAlert(initialRequestId, this, this));

        ivNextTrip = (ImageView) findViewById(R.id.iv_nextTrip);
        ivPreviousTrip = (ImageView) findViewById(R.id.iv_previousTrip);

        ivNextTrip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showNextTrip();
            }
        });

        ivPreviousTrip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPreviousTrip();
            }
        });

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
    protected void onResume()
    {
        super.onResume();

        if (newTripRequestReceiver == null)
        {
            newTripRequestReceiver = new NewTripRequestReceiver();
        }
        registerReceiver(newTripRequestReceiver, new IntentFilter(Constants.NEW_TRIP_REQUEST));
    }

    @Override
    protected void onStop()
    {
        if (mServiceBound)
        {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }

        if (newTripRequestReceiver != null)
        {
            unregisterReceiver(newTripRequestReceiver);
        }

        super.onStop();
    }


    public void acceptRequest(final String requestId)
    {
        this.requestId = requestId;
        fusedLocationApiCallbacks = new FusedLocationApiCallbacks(this);
        fusedLocation = new FusedLocation(this, fusedLocationApiCallbacks
                , this);

        notificationManager.cancel(Integer.valueOf(requestId));
    }

    public void rejectRequest(final String requestId)
    {
        this.requestId = requestId;
        TransportRequestHandler.insertTripRejectedDataUsingApi(requestId);
        notificationManager.cancel(Integer.valueOf(requestId));
        removeView(getCurrentPage());
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i(TAG, "FAILED TO GET LOCATION TRIP ACCEPTED");
        TransportRequestHandler.acceptRequest(requestId, null, this);
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

                Log.i(TAG, "GOT LOCATION TRIP ACCEPTED");

                TransportRequestHandler.acceptRequest(requestId, location.getLatitude() + "," + location.getLongitude()
                        , ActivityTransportRequest.this);

                fusedLocation.stopGettingLocation();
                removeView(getCurrentPage());
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
        Log.i(TAG, "ON DESTROY CALLED!!!!!!!!!!!!");
        if (fusedLocation != null && fusedLocationApiCallbacks != null)
        {
            fusedLocation.stopGettingLocation();
            fusedLocation = null;
            fusedLocationApiCallbacks = null;
        }
        wl.release();
        super.onDestroy();
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to add a view to the ViewPager.
    public void addView(View newPage)
    {
        int pageIndex = adapter.addView(newPage);
        adapter.notifyDataSetChanged();
        // You might want to make "newPage" the currently displayed page:
        vpNewTripPager.setCurrentItem(pageIndex, true);
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to remove a view from the ViewPager.
    public void removeView(View defunctPage)
    {
        int pageIndex = adapter.removeView(vpNewTripPager, defunctPage);
        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == adapter.getCount())
        {
            pageIndex--;
        }
        vpNewTripPager.setCurrentItem(pageIndex);
        adapter.notifyDataSetChanged();

        if (adapter.getCount() == 0)
        {
            finish();
        }
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to get the currently displayed page.
    public View getCurrentPage()
    {
        return adapter.getView(vpNewTripPager.getCurrentItem());
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to set the currently displayed page.  "pageToShow" must
    // currently be in the adapter, or this will crash.
    public void setCurrentPage(View pageToShow)
    {
        vpNewTripPager.setCurrentItem(adapter.getItemPosition(pageToShow), true);
    }

    class NewTripRequestReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ivPreviousTrip.setVisibility(View.VISIBLE);
            ivNextTrip.setVisibility(View.VISIBLE);
            Log.i(TAG, "BROADCAST RECEIVED FOR NEW TRIP REQUEST");
            final String requestId = intent.getStringExtra("REQUEST_ID");
            NewTripAlert newTripAlert = new NewTripAlert(requestId, ActivityTransportRequest.this,
                    ActivityTransportRequest.this);
            addView(newTripAlert);
            setCurrentPage(newTripAlert);
        }
    }


    private void showNextTrip()
    {
        if (vpNewTripPager.getCurrentItem() < adapter.getCount() - 1)
        {
            vpNewTripPager.setCurrentItem(getItem(+1), true);
        }
    }

    private void showPreviousTrip()
    {
        if (vpNewTripPager.getCurrentItem() > 0)
        {
            vpNewTripPager.setCurrentItem(getItem(-1), true);
        }
    }

    private int getItem(int i)
    {
        return vpNewTripPager.getCurrentItem() + i;
    }

}
