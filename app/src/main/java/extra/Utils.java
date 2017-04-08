package extra;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
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

    public static Date convertToDate(String stringDate)
    {
        Date date = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try
        {
            date = sdf.parse(stringDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date;
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
        NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo_small)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(tripId, n.build());
    }

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static String getDate(Date d)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return sdf.format(d);
    }

    public static void ShareApp(final Activity activity)
    {
        try
        {
            int applicationNameId = activity.getApplicationInfo().labelRes;
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, activity.getString(applicationNameId));
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + activity.getPackageName() + "\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            activity.startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e)
        {
            //e.toString();
        }
    }

    public static void sendFeedback(Activity activity)
    {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sales@vcarry.co.in"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
        activity.startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }

}
