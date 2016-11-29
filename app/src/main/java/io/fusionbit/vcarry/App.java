package io.fusionbit.vcarry;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import extra.LocaleHelper;
import extra.Log;
import io.realm.Realm;

/**
 * Created by rutvik on 10/26/2016 at 9:41 AM.
 */

public class App extends MultiDexApplication
{

    public static final String APP_TAG = "VCRY ";

    private static final String TAG = APP_TAG + App.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        LocaleHelper.onCreate(this, LocaleHelper.getLanguage(this));

        Realm.init(this);

        Log.i(TAG, "Application Created!!!");
    }


    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
