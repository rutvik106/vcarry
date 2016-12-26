package io.fusionbit.vcarry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

import apimodels.TripDetails;
import extra.LocaleHelper;
import extra.Log;
import fragment.FragmentAccBalance;
import fragment.FragmentCompletedTripDetails;
import fragment.FragmentMap;
import fragment.FragmentTrips;
import fragment.FragmentTripsOnOffer;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static io.fusionbit.vcarry.Constants.ON_TRIP_CANCELED;
import static io.fusionbit.vcarry.Constants.ON_TRIP_STOPPED;
import static io.fusionbit.vcarry.Constants.WAS_LANGUAGE_CHANGED;

public class ActivityHome extends FusedLocation.LocationAwareActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentMap.OnTripStopListener,
        FragmentCompletedTripDetails.TripStopDataInsertionCallback, OnCompleteListener<Void>, GoogleApiClient.OnConnectionFailedListener
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

    ProgressDialog progressDialog;

    FirebaseRemoteConfig remoteConfig;

    FusedLocation fusedLocation;

    RelativeLayout notifCount;

    TextView notificationCount;

    MenuItem notificationMenuItem;

    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.i(TAG, "ACTIVITY CONNECTED TO SERVICE");
            mServiceBound = true;
            //get service instance here
            mService = ((TransportRequestHandlerService.TransportRequestServiceBinder) service).getService();
            if (mService != null)
            {
                serviceResultReceiver = new ServiceResultReceiver(null);
                mService.setResultReceiver(serviceResultReceiver);
            }
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


        fusedLocation = new FusedLocation(this, new FusedLocation.ApiConnectionCallbacks(this)
        {
            @Override
            public void onConnected(@Nullable Bundle bundle)
            {
                super.onConnected(bundle);
            }
        }, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarTitle("V-Carry");

/*       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .bitmapTransform(new CropCircleTransformation(this))
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

            navigationView.getMenu().getItem(0).setChecked(true);

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

        final boolean isBillPending = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.IS_BILL_PENDING, false);

        if (isBillPending)
        {
            showCompletedTripDetails();
        }

        //FIREBASE REMOTE CONFIG

        setupFirebaseRemoteConfig();

        ////////////////////////

        //start bound service now
        Intent transportRequestHandlerService = new Intent(this, TransportRequestHandlerService.class);
        transportRequestHandlerService.putExtra(Constants.SERVICE_RESULT_RECEIVER, serviceResultReceiver);
        startService(transportRequestHandlerService);

        //bind activity to service
        bindService(transportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ((App) getApplication()).getTripNotifications(new App.TripNotificationListener()
        {
            @Override
            public void onGetTripNotificationList(List<TripDetails> tripDetailsList)
            {
                if (tripDetailsList.size() > 0)
                {
                    notificationCount.setText(tripDetailsList.size() + "");
                    notificationMenuItem.setVisible(true);
                } else
                {
                    notificationMenuItem.setVisible(false);
                }
            }
        });
    }

    private void setupFirebaseRemoteConfig()
    {
        remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig.setConfigSettings(configSettings);

        remoteConfig.setDefaults(Constants.getFirebaseRemoteValuesMap());

        if (remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled())
        {
            remoteConfig.fetch(0).addOnCompleteListener(this);
        } else
        {
            remoteConfig.fetch(2000).addOnCompleteListener(this);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } /*else if (isShowingCompletedTripDetails)
        {
            hideCompletedTripDetails();
        }*/ else
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

        notificationMenuItem = menu.findItem(R.id.action_showTripNotifications);
        MenuItemCompat.setActionView(notificationMenuItem, R.layout.trip_notification_badge);
        RelativeLayout root = (RelativeLayout) MenuItemCompat.getActionView(notificationMenuItem);

        notificationCount = (TextView) root.findViewById(R.id.actionbar_notifcation_textview);

        root.findViewById(R.id.rl_tripNotificationContainer).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(ActivityHome.this, ActivityTripNotifications.class));
            }
        });

        notificationMenuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, ActivitySettings.class), Constants.CHANGE_LANGUAGE);
                break;

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
            setActionBarTitle(getResources().getString(R.string.nav_accountBalance));
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
        if (mServiceBound)
        {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
        super.onStop();
    }

    private void showCompletedTripDetails()
    {

        final String tripId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.CURRENT_TRIP_ID, "");

        if (!isShowingCompletedTripDetails)
        {
            isShowingCompletedTripDetails = true;
            findViewById(R.id.fl_completedTripDetails).setVisibility(View.VISIBLE);
            fragmentCompletedTripDetails = FragmentCompletedTripDetails.newInstance(this, tripId, this);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    .add(R.id.fl_completedTripDetails, fragmentCompletedTripDetails)
                    .show(fragmentCompletedTripDetails)
                    .commitAllowingStateLoss();
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
    public void onTripStop(String tripId)
    {
        if (mService != null)
        {
            progressDialog = ProgressDialog
                    .show(this, getResources().getString(R.string.please_wait),
                            getResources().getString(R.string.generating_trip_details), true, false);
            progressDialog.show();

            mService.stopTrip(tripId);
        }
    }

    @Override
    public void onTripCancel(String tripId)
    {
        if (mService != null)
        {
            progressDialog = ProgressDialog
                    .show(this, getResources().getString(R.string.please_wait),
                            getResources().getString(R.string.please_wait), true, false);
            progressDialog.show();

            mService.cancelTrip(tripId);
        }
    }

    private void setActionBarTitle(final String title)
    {
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task)
    {
        if (task.isSuccessful())
        {

            remoteConfig.activateFetched();

            final boolean showUrgentNotice = remoteConfig.getBoolean("show_urgent_notice");

            if (showUrgentNotice)
            {
                showUrgentNotice();
            }

            final boolean isForceUpdate = remoteConfig.getBoolean("force_update");

            if (isForceUpdate)
            {
                forceUserToUpdateApp();
            }


        }
    }

    private void forceUserToUpdateApp()
    {

        final int versionCode = BuildConfig.VERSION_CODE;

        final long newVersionCode = remoteConfig.getLong("version_code");

        if (versionCode < newVersionCode)
        {

            String updateMessage = "";

            if (LocaleHelper.getLanguage(this).equals("gu"))
            {
                updateMessage = remoteConfig.getString("update_message_gujarati");
            } else
            {
                updateMessage = remoteConfig.getString("update_message_english");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.please_update))
                    .setMessage(updateMessage)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    @Override
    public void locationServiceAlreadyOn()
    {

    }

    @Override
    public void locationServiceTurnedOn()
    {

    }

    @Override
    public void locationSettingChangeUnavailable()
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

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
                    if (progressDialog != null)
                    {
                        if (progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }
                    }
                    final String tripId = resultData.getString(Constants.CURRENT_TRIP_ID);
                    if (tripId != null)
                    {
                        showCompletedTripDetails();
                    }
                    break;

                case ON_TRIP_CANCELED:
                    if (progressDialog != null)
                    {
                        if (progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }
                    }
                    break;

                default:
                    super.onReceiveResult(resultCode, resultData);
            }
        }
    }

    @Override
    public void dataInsertedSuccessfully()
    {
        Toast.makeText(this, "TRIP STOP DATA INSERTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
        if (isShowingCompletedTripDetails)
        {
            hideCompletedTripDetails();
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(Constants.IS_BILL_PENDING, false)
                    .putString(Constants.CURRENT_TRIP_ID, null)
                    .apply();
        }
    }

    @Override
    public void failedToInsertTripStopData()
    {
        Toast.makeText(this, "FAILED TO INSERTED TRIP STOP DATA", Toast.LENGTH_SHORT).show();
    }


    private void showUrgentNotice()
    {
        String message = "";
        String title = "";

        if (LocaleHelper.getLanguage(this).equals("gu"))
        {
            message = remoteConfig.getString("urgent_notice_gujarati");
            title = remoteConfig.getString("urgent_notice_title_gujarati");
        } else
        {
            message = remoteConfig.getString("urgent_notice_english");
            title = remoteConfig.getString("urgent_notice_title_english");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), null);
        AlertDialog alert = builder.create();
        if (!isFinishing())
        {
            alert.show();
        } else
        {
            alert.dismiss();
        }
    }

}
