package io.fusionbit.vcarry;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import extra.CircleTransform;
import extra.LocaleHelper;
import extra.Log;
import fragment.FragmentAccBalance;
import fragment.FragmentCompletedTripDetails;
import fragment.FragmentMap;
import fragment.FragmentTrips;
import fragment.FragmentTripsOnOffer;

import static io.fusionbit.vcarry.Constants.ON_TRIP_STOPPED;
import static io.fusionbit.vcarry.Constants.WAS_LANGUAGE_CHANGED;

public class ActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentMap.OnTripStopListener
{
    private static final String TAG = App.APP_TAG + ActivityHome.class.getSimpleName();

    //is activity connected to service
    boolean mServiceBound = false;

    //main STICKY service running in foreground (also shows up in notifications)
    TransportRequestHandlerService mService;

    FragmentManager fragmentManager;

    FragmentMap fragmentMap;
    FragmentTrips fragmentTrips;
    FragmentAccBalance fragmentAccBalance;
    FragmentTripsOnOffer fragmentTripsOnOffer;

    FragmentCompletedTripDetails fragmentCompletedTripDetails;

    boolean doubleBackToExitPressedOnce = false;

    List<Fragment> fragmentList = new ArrayList<>();

    private boolean isShowingCompletedTripDetails = false;

    ServiceResultReceiver serviceResultReceiver;

    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.i(TAG, "ACTIVITY CONNECTED TO SERVICE");
            mServiceBound = true;
            //get service instance here
            mService = ((TransportRequestHandlerService.TransportRequestServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className)
        {
            Log.i(TAG, "ACTIVITY DISCONNECTED FROM SERVICE");
            mServiceBound = false;
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "HOME ACTIVITY ON CREATE");
        setContentView(R.layout.activity_home);

        LocaleHelper.onCreate(this, LocaleHelper.getLanguage(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarTitle("V-Carry");

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {

            Picasso.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .transform(new CircleTransform())
                    .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_userPic));

            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_titleUserName))
                    .setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_subTitle))
                    .setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            fragmentMap = FragmentMap.newInstance(0, this, this);
            fragmentTrips = FragmentTrips.newInstance(1, this);
            fragmentAccBalance = FragmentAccBalance.newInstance(2, this);
            fragmentTripsOnOffer = FragmentTripsOnOffer.newInstance(3, this);

            fragmentList.add(fragmentMap);
            fragmentList.add(fragmentTrips);
            fragmentList.add(fragmentAccBalance);
            fragmentList.add(fragmentTripsOnOffer);

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_mapView, fragmentMap)
                    .add(R.id.fl_mapView, fragmentTrips)
                    .add(R.id.fl_mapView, fragmentAccBalance)
                    .add(R.id.fl_mapView, fragmentTripsOnOffer)
                    .commitAllowingStateLoss();

            showFragment(fragmentMap);

        } else
        {
            finish();
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i(TAG, "HOME ACTIVITY ON START");

        if (serviceResultReceiver == null)
        {
            serviceResultReceiver = new ServiceResultReceiver(null);
        }

        //start bound service now
        Intent transportRequestHandlerService = new Intent(this, TransportRequestHandlerService.class);
        transportRequestHandlerService.putExtra(Constants.SERVICE_RESULT_RECEIVER, serviceResultReceiver);
        startService(transportRequestHandlerService);

        //bind activity to service
        bindService(transportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isShowingCompletedTripDetails)
        {
            hideCompletedTripDetails();
        } else
        {
            if (doubleBackToExitPressedOnce)
            {
                super.onBackPressed();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivityForResult(new Intent(this, ActivitySettings.class), Constants.CHANGE_LANGUAGE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            fragmentMap.checkIfDriverOnTrip();
            showFragment(fragmentMap);
            setActionBarTitle("V-Carry");
        } else if (id == R.id.nav_trips)
        {
            fragmentTrips.getTrips();
            showFragment(fragmentTrips);
            setActionBarTitle(getResources().getString(R.string.actionbar_title_trips));
        } else if (id == R.id.nav_accountBalance)
        {
            showFragment(fragmentAccBalance);
        } else if (id == R.id.nav_tripsOnOffer)
        {
            showFragment(fragmentTripsOnOffer);
        } else if (id == R.id.nav_share)
        {

        } else if (id == R.id.nav_sendFeedback)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        if (mServiceBound)
        {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    private void showCompletedTripDetails(final String tripId)
    {
        if (!isShowingCompletedTripDetails)
        {
            isShowingCompletedTripDetails = true;
            findViewById(R.id.fl_completedTripDetails).setVisibility(View.VISIBLE);
            fragmentCompletedTripDetails = new FragmentCompletedTripDetails();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    .add(R.id.fl_completedTripDetails, fragmentCompletedTripDetails)
                    .show(fragmentCompletedTripDetails)
                    .commitAllowingStateLoss();

            fragmentCompletedTripDetails.showCompletedTripDetails(tripId);
        }
    }

    private void hideCompletedTripDetails()
    {
        if (isShowingCompletedTripDetails)
        {
            isShowingCompletedTripDetails = false;
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    .hide(fragmentCompletedTripDetails)
                    .commitAllowingStateLoss();

            fragmentCompletedTripDetails = null;

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    findViewById(R.id.fl_completedTripDetails).setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {


                case Constants.CHANGE_LANGUAGE:
                    boolean wasChanged = data.getExtras().getBoolean(WAS_LANGUAGE_CHANGED, false);
                    if (wasChanged)
                    {
                        Intent refresh = new Intent(this, ActivityHome.class);
                        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(refresh);
                    }
                    break;


            }
        }
    }

    private void showFragment(Fragment fragmentToShow)
    {
        FragmentTransaction t = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentList)
        {
            if (!fragment.equals(fragmentToShow))
            {
                t.hide(fragment);
            } else
            {
                t.show(fragment);
            }
        }
        t.commitAllowingStateLoss();
    }


    @Override
    public void onStripStop(String tripId)
    {
        if (mService != null)
        {
            mService.stopTrip();
        }
    }

    private void setActionBarTitle(final String title)
    {
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(title);
        }
    }

    public class ServiceResultReceiver extends ResultReceiver
    {
        public ServiceResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            switch (resultCode)
            {

                case ON_TRIP_STOPPED:
                    final String tripId = resultData.getString(Constants.CURRENT_TRIP_ID);
                    if (tripId != null)
                    {
                        showCompletedTripDetails(tripId);
                    }
                    break;

                default:
                    super.onReceiveResult(resultCode, resultData);
            }
        }
    }

}
