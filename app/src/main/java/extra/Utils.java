package extra;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

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

}
