package fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import extra.Log;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.NotificationHandler;

/**
 * Created by rutvik on 1/21/2017 at 9:41 AM.
 */

public class VcarryDriverFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = App.APP_TAG +
            VcarryDriverFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        Log.i(TAG, "DATA: " + data.toString() + " FROM: " + from);

        new NotificationHandler(this, remoteMessage)
                .handleNotification();
    }


}
