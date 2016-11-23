package io.fusionbit.vcarry;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import extra.CircleTransform;
import extra.Log;
import fragment.FragmentMap;
import fragment.FragmentTrips;

import static io.fusionbit.vcarry.Constants.WAS_LANGUAGE_CHANGED;

public class ActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = App.APP_TAG + ActivityHome.class.getSimpleName();

    //is activity connected to service
    boolean mServiceBound = false;

    //main STICKY service running in foreground (also shows up in notifications)
    TransportRequestHandlerService mService;

    FragmentTransaction ft;

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
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //start bound service now
        Intent TransportRequestHandlerService = new Intent(this, TransportRequestHandlerService.class);
        startService(TransportRequestHandlerService);

        //bind activity to service
        bindService(TransportRequestHandlerService, mServiceConnection, BIND_AUTO_CREATE);


        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("V-Carry");
        }

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

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_mapView, FragmentMap.newInstance(0, this));
            ft.commit();

        } else
        {
            finish();
        }

    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
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
        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            fragment = FragmentMap.newInstance(0, this);
        } else if (id == R.id.nav_trips)
        {
            fragment = FragmentTrips.newInstance(1, this);
        } else if (id == R.id.nav_accountBalance)
        {

        } else if (id == R.id.nav_tripsOnOffer)
        {

        } else if (id == R.id.nav_share)
        {

        } else if (id == R.id.nav_sendFeedback)
        {

        }

        if (fragment != null)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_mapView, fragment);
            ft.commit();
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
                        this.recreate();
                    }
                    break;


            }
        }
    }

}
