package io.fusionbit.vcarry;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import firebase.TransportRequestHandler;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by rutvik on 11/16/2016 at 2:32 PM.
 */

public class TransportRequestResponseReceiver extends BroadcastReceiver
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    FusedLocation fusedLocation;

    String requestId, response;

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        response = intent.getStringExtra(Constants.T_RESPONSE);
        requestId = intent.getStringExtra(Constants.TRANSPORT_REQUEST_ID);

        notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        fusedLocation = new FusedLocation(context, this, this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        fusedLocation.startGettingLocation(new FusedLocation.GetLocation()
        {
            @Override
            public void onLocationChanged(Location location)
            {

                if (response.equals(Constants.ACCEPT))
                {
                    TransportRequestHandler
                            .acceptRequest(requestId,
                                    location.getLatitude() + "," + location.getLongitude()
                                    , null);
                } else if (response.equals(Constants.REJECT))
                {
                    TransportRequestHandler.rejectRequest();
                }

                fusedLocation.stopGettingLocation();
                notificationManager.cancel(Integer.valueOf(requestId));
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        if (response.equals(Constants.ACCEPT))
        {
            TransportRequestHandler.acceptRequest(requestId, null, null);
        } else if (response.equals(Constants.REJECT))
        {
            TransportRequestHandler.rejectRequest();
        }
        TransportRequestHandler.acceptRequest(requestId, null, null);
        notificationManager.cancel(Integer.valueOf(requestId));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        if (response.equals(Constants.ACCEPT))
        {
            TransportRequestHandler.acceptRequest(requestId, null, null);
        } else if (response.equals(Constants.REJECT))
        {
            TransportRequestHandler.rejectRequest();
        }
        TransportRequestHandler.acceptRequest(requestId, null, null);
        notificationManager.cancel(Integer.valueOf(requestId));
    }

}
