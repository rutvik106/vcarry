package io.fusionbit.vcarry;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import extra.Log;

public class ActivityLogin extends VCarryActivity implements PermissionListener, View.OnClickListener
{

    private static final String TAG = App.APP_TAG + ActivityLogin.class.getSimpleName();

    private boolean isConnected = false;

    private FirebaseAuth mFirebaseAuth;

    private Intent intent;

    private static final String GOOGLE_TOS_URL =
            "https://www.google.com/policies/terms/";

    private static final int RC_SIGN_IN = 100;

    BroadcastReceiver networkChangeReceiver;

    Button btnTryLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.i(TAG, "OnCreate");

        checkForPermissions();

    }

    private void checkForPermissions()
    {
        TedPermission.with(this)
                .setPermissionListener(this)
                .setDeniedMessage("If you reject any permission, you can not use this App\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.DISABLE_KEYGUARD,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED)
                .check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RC_SIGN_IN:
                handleSignInResponse(resultCode, data);
                break;

            default:
                break;
        }

    }


    private void tryLogin()
    {
        if (!isConnected)
        {
            return;
        }

        final FusedLocation.Status status = FusedLocation.isGooglePlayServiceAvailable(this);
        if (status == FusedLocation.Status.UPDATE_REQUIRE)
        {
            final String appPackageName = "com.google.android.gms";
            try
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return;
        }

        // Choose authentication providers
        /*List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());*/

        //user not logged in
        //show firebase login methods using firebase auth UI
        /*startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setTosUrl(getSelectedTosUrl())
                        .setLogo(getSelectedLogo())
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);*/


    }

    //user signed in response
    @MainThread
    private void handleSignInResponse(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            startActivity(intent);
        }

        if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }


    @MainThread
    private String getSelectedTosUrl()
    {

        return GOOGLE_TOS_URL;

    }

    @MainThread
    @StyleRes
    private int getSelectedTheme()
    {

        return 0; //R.style.LoginTheme; // AuthUI.getDefaultTheme();

    }

    @MainThread
    @DrawableRes
    private int getSelectedLogo()
    {
        return R.drawable.logo;
    }

    //set login methods/providers (GOOGLE/FACEBOOK)
    @MainThread
    private String[] getSelectedProviders()
    {
        ArrayList<String> selectedProviders = new ArrayList<>();

        //selectedProviders.add(AuthUI.GOOGLE_PROVIDER);

        return selectedProviders.toArray(new String[selectedProviders.size()]);
    }

    @Override
    public void onPermissionGranted()
    {

        intent = new Intent(ActivityLogin.this, ActivityHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        btnTryLogin = (Button) findViewById(R.id.btn_googleSignIn);
        btnTryLogin.setOnClickListener(this);


        //check internet connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            isConnected = true;
        } else
        {
            isConnected = false;
        }

        //listen to network changes
        networkChangeReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getBooleanExtra("is_active", false))
                {
                    isConnected = true;
                } else
                {
                    isConnected = false;
                }
            }
        };

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null)
        {
            startActivity(intent);
        }


    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions)
    {
        finish();
    }

    @Override
    public void onClick(View view)
    {
        if (isConnected)
        {
            tryLogin();
        } else
        {
            Toast.makeText(ActivityLogin.this, "check internet connection", Toast.LENGTH_SHORT).show();
        }

    }
}
