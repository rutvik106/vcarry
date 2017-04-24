package io.fusionbit.vcarry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.API;
import api.RetrofitCallbacks;
import apimodels.DriverDetails;
import apimodels.TripDetails;
import extra.LocaleHelper;
import extra.Log;
import extra.Utils;
import fragment.FragmentAccBalance;
import fragment.FragmentCompletedTripDetails;
import fragment.FragmentMap;
import fragment.FragmentTrips;
import fragment.FragmentTripsOnOffer;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static io.fusionbit.vcarry.Constants.ON_TRIP_CANCELED;
import static io.fusionbit.vcarry.Constants.ON_TRIP_STOPPED;
import static io.fusionbit.vcarry.Constants.WAS_LANGUAGE_CHANGED;
import static io.fusionbit.vcarry.Constants.WAS_REALM_DATABASE_CLEARED;

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

    CoordinatorLayout clActivityHome;

    boolean doubleBackToExitPressedOnce = false;

    List<Fragment> fragmentList = new ArrayList<>();
    ServiceResultReceiver serviceResultReceiver;
    ProgressDialog progressDialog;
    FirebaseRemoteConfig remoteConfig;
    FusedLocation fusedLocation;
    RelativeLayout notifCount;
    TextView notificationCount;
    MenuItem notificationMenuItem;
    SearchView searchView;
    MenuItem searchMenu;
    String driverId;
    Call<List<String>> tripSearchCall;
    private boolean isShowingCompletedTripDetails = false;
    private SimpleCursorAdapter tripSearchResultCursorAdapter;
    private String[] strArrData = {"No Suggestions"};
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

    private Snackbar snackbarNoInternet;

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

        final String[] from = new String[]{"tripNumber"};
        final int[] to = new int[]{android.R.id.text1};

        tripSearchResultCursorAdapter =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                        null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        clActivityHome = (CoordinatorLayout) findViewById(R.id.cl_activityHome);

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

    private void updateDriverProfilePicture(final String driverId)
    {
        API.getInstance().updateDriverImage(driverId,
                FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null ?
                        FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() :
                        "https://lh3.googleusercontent.com/-Wlkp-_tMv-Y/AAAAAAAAAAI/AAAAAAAAAAA/NbvcGT31kjM/s120-c/photo.jpg",
                new RetrofitCallbacks<ResponseBody>()
                {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            try
                            {
                                Log.i(TAG, response.body().string());
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
                    if (notificationMenuItem != null)
                    {
                        notificationCount.setText(tripDetailsList.size() + "");
                        notificationMenuItem.setVisible(true);
                    }
                } else
                {
                    if (notificationMenuItem != null)
                    {
                        notificationMenuItem.setVisible(false);
                    }
                }
            }
        });

        getDriverIdByDriverEmail();

    }

    private void getDriverIdByDriverEmail()
    {

        final String driverEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final RetrofitCallbacks<Integer> onGetDriverIdCallback =
                new RetrofitCallbacks<Integer>()
                {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            if (response.body() > 0)
                            {
                                driverId = String.valueOf(response.body());
                                PreferenceManager.getDefaultSharedPreferences(ActivityHome.this)
                                        .edit()
                                        .putString(Constants.DRIVER_ID, driverId)
                                        .apply();

                                updateFcmDeviceToken();

                                getUserDetails(String.valueOf(response.body()));
                                updateDriverProfilePicture(driverId);

                            } else
                            {
                                PreferenceManager.getDefaultSharedPreferences(ActivityHome.this)
                                        .edit()
                                        .putString(Constants.DRIVER_ID, null)
                                        .apply();
                                showDriverNotRegistered();
                            }
                        } else
                        {
                            showDriverNotRegistered();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t)
                    {
                        super.onFailure(call, t);
                        driverId = PreferenceManager
                                .getDefaultSharedPreferences(ActivityHome.this)
                                .getString(Constants.DRIVER_ID, null);
                        if (driverId == null)
                        {
                            showDriverNotRegistered();
                        }
                    }
                };

        API.getInstance().getDriverIdByDriverEmail(driverEmail, onGetDriverIdCallback);

    }

    private void getUserDetails(String driverId)
    {
        Log.i(TAG, "GETTING DRIVER DETAILS FOR DRIVER ID: " + driverId);
        final RetrofitCallbacks<DriverDetails> onGetDriverDetails =
                new RetrofitCallbacks<DriverDetails>()
                {
                    @Override
                    public void onResponse(Call<DriverDetails> call, Response<DriverDetails> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            Log.i(TAG, "SUCCESSFULLY GOT DRIVER DETAILS SAVING TO REALM");
                            final Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(response.body());
                            realm.commitTransaction();
                            realm.close();
                            Log.i(TAG, "DRIVER DETAILS SAVED AND CLOSED REALM");
                        }
                    }
                };

        API.getInstance().getDriverDetailsByDriverId(driverId, onGetDriverDetails);
    }


    private void updateFcmDeviceToken()
    {
        final String driverEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final String fcmDeviceToken = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.FCM_DRIVER_INSTANCE_ID, null);

        final RetrofitCallbacks<ResponseBody> onUpdateDeviceTokenCallback =
                new RetrofitCallbacks<ResponseBody>()
                {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        super.onResponse(call, response);
                    }
                };

        if (fcmDeviceToken != null)
        {
            API.getInstance().updateDeviceTokenDriver(driverEmail, fcmDeviceToken, onUpdateDeviceTokenCallback);
        } else
        {
            Toast.makeText(this, "FCM Instance ID not found!", Toast.LENGTH_SHORT).show();
        }


    }

    private void setupFirebaseRemoteConfig()
    {
        remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                //.setDeveloperModeEnabled(BuildConfig.DEBUG)
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
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

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

        // Retrieve the SearchView and plug it into SearchManager
        searchMenu = menu.findItem(R.id.action_search);
        searchMenu.setVisible(false);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(tripSearchResultCursorAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener()
        {
            @Override
            public boolean onSuggestionClick(int position)
            {
                // Add clicked text to search box
                CursorAdapter ca = searchView.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                searchView.setQuery(cursor.getString(cursor.getColumnIndex("tripNumber")), false);

                ActivityTripDetails.start(ActivityHome.this,
                        cursor.getString(cursor.getColumnIndex("tripNumber")));

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position)
            {
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s)
            {
                if (tripSearchCall != null)
                {
                    tripSearchCall.cancel();
                }

                tripSearchCall = API.getInstance().getTripNumberLike(s, driverId, new RetrofitCallbacks<List<String>>()
                {

                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            if (response.body() == null)
                            {
                                return;
                            }
                            strArrData = response.body().toArray(new String[0]);
                            // Filter data
                            final MatrixCursor mc = new MatrixCursor(new String[]{BaseColumns._ID, "tripNumber"});
                            for (int i = 0; i < strArrData.length; i++)
                            {
                                if (strArrData[i].toLowerCase().contains(s.toLowerCase()))
                                {
                                    mc.addRow(new Object[]{i, strArrData[i]});
                                }
                            }
                            tripSearchResultCursorAdapter.changeCursor(mc);
                            tripSearchResultCursorAdapter.notifyDataSetChanged();
                        }
                    }
                });
                return false;
            }
        });

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

        switch (item.getItemId())
        {
            case R.id.nav_home:
                fragmentMap.checkIfDriverOnTrip();
                showFragment(fragmentMap);
                setActionBarTitle("V-Carry");
                searchMenu.setVisible(false);
                break;
            case R.id.nav_trips:
                fragmentTrips.getTrips();
                showFragment(fragmentTrips);
                setActionBarTitle(getResources().getString(R.string.actionbar_title_trips));
                //searchView.setOnQueryTextListener(fragmentTrips);
                searchMenu.setVisible(true);
                break;
            case R.id.nav_accountBalance:
                showFragment(fragmentAccBalance);
                setActionBarTitle(getResources().getString(R.string.nav_accountBalance));
                searchMenu.setVisible(false);
                break;
            case R.id.nav_tripsOnOffer:
                showFragment(fragmentTripsOnOffer);
                searchMenu.setVisible(false);
                break;
            case R.id.nav_share:
                Utils.ShareApp(this);
                break;
            case R.id.nav_sendFeedback:
                Utils.sendFeedback(this);
                break;
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

    @Override
    protected void internetNotAvailable()
    {
        if (snackbarNoInternet == null)
        {
            snackbarNoInternet = Snackbar.make(clActivityHome, R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
            snackbarNoInternet.show();
        }
    }

    @Override
    protected void internetAvailable()
    {
        if (snackbarNoInternet != null)
        {
            if (snackbarNoInternet.isShown())
            {
                snackbarNoInternet.dismiss();
                snackbarNoInternet = null;
            }
        }
    }

    private void showCompletedTripDetails()
    {

        final String tripId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.CURRENT_TRIP_ID, "");

        if (!isShowingCompletedTripDetails)
        {
            isShowingCompletedTripDetails = true;
            findViewById(R.id.fl_completedTripDetails).setVisibility(View.VISIBLE);
            fragmentCompletedTripDetails = FragmentCompletedTripDetails.newInstance(this, tripId,
                    false, this);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(Constants.IS_BILL_PENDING, true)
                    .apply();

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
                    boolean wasLanguageChanged = data.getExtras().getBoolean(WAS_LANGUAGE_CHANGED, false);
                    boolean wasRealmDatabaseCleared = data.getExtras()
                            .getBoolean(WAS_REALM_DATABASE_CLEARED, false);
                    if (wasLanguageChanged || wasRealmDatabaseCleared)
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

    public void showDriverNotRegistered()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.unauthorized)
                .setMessage(R.string.not_registered)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
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
                    if (resultData != null)
                    {
                        final String tripId = resultData.getString(Constants.CURRENT_TRIP_ID);
                        if (tripId != null)
                        {
                            showCompletedTripDetails();
                        }
                    } else
                    {

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

}
