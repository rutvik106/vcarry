package io.fusionbit.vcarry;

import android.app.Application;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import api.API;
import api.RetrofitCallbacks;
import apimodels.TripDetails;
import extra.LocaleHelper;
import extra.Log;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rutvik on 10/26/2016 at 9:41 AM.
 */

public class App extends Application
{

    public static final String APP_TAG = "VCRY ";
    private static final String TAG = APP_TAG + App.class.getSimpleName();
    public static String PACKAGE_NAME;
    public RealmConfiguration realmConfig;
    private List<TripDetails> tripNotificationList = new ArrayList<>();
    private TripNotificationListener tripNotificationListener;

    @Override

    public void onCreate()
    {
        super.onCreate();

        PACKAGE_NAME = getApplicationContext().getPackageName();

        LocaleHelper.onCreate(this, LocaleHelper.getLanguage(this));

        Realm.init(this);

        realmConfig = new RealmConfiguration
                .Builder()
                .name(Constants.REALM_DATABASE_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfig);

        Log.i(TAG, "Application Created!!!");
    }


    /**
     * @Override protected void attachBaseContext(Context base)
     * {
     * super.attachBaseContext(base);
     * MultiDex.install(this);
     * }
     */

    public void getTripNotifications(final TripNotificationListener tripNotificationListener)
    {

        RetrofitCallbacks<List<TripDetails>> TripsCallback = new RetrofitCallbacks<List<TripDetails>>()
        {

            @Override
            public void onResponse(Call<List<TripDetails>> call, Response<List<TripDetails>> response)
            {
                super.onResponse(call, response);
                if (response.isSuccessful())
                {
                    tripNotificationList.clear();
                    for (TripDetails singleTrip : response.body())
                    {
                        tripNotificationList.add(singleTrip);
                    }
                    tripNotificationListener.onGetTripNotificationList(tripNotificationList);
                }
            }
        };

        final String driverId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.DRIVER_ID, "");

        API.getInstance().getTripsByTripStatus(Constants.TRIP_STATUS_NEW, 0,
                null, null, null, null, driverId, TripsCallback);

    }

    public List<TripDetails> getTripNotificationList()
    {
        return tripNotificationList;
    }


    public interface TripNotificationListener
    {
        void onGetTripNotificationList(List<TripDetails> tripDetailsList);
    }

}
