package fcm;

import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.fusionbit.vcarry.Constants;

/**
 * Created by rutvik on 1/21/2017 at 9:41 AM.
 */

public class VcarryDriverFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(Constants.FCM_DRIVER_INSTANCE_ID, refreshedToken)
                .apply();
    }
}
