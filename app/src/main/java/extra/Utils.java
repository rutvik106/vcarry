package extra;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.fusionbit.vcarry.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by rutvik on 12/3/2016 at 8:09 AM.
 */

public class Utils
{

    public static void checkIfGpsEnabled(final Context context)
    {

        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled)
        {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("GPS settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });


            // Showing Alert Message
            alertDialog.show().getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public static String convertDateToRequireFormat(final String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try
        {
            Date d = sdf.parse(date);
            sdf.applyPattern("d-E, h:mm a");
            System.out.println("CONVERTED DATE: " + sdf.format(d));
            return sdf.format(d);
        } catch (ParseException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getDateFromMills(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void showSimpleNotification(final Context context, int tripId,
                                              String title, String message, PendingIntent pendingIntent)
    {
        Notification n = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo_small)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(tripId, n);
    }

}
