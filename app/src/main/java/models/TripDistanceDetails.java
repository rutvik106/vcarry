package models;

import android.location.Location;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rutvik on 12/3/2016 at 8:34 AM.
 */

public class TripDistanceDetails extends RealmObject
{

    @PrimaryKey
    private String tripId;

    private long tripStartTime;

    private long tripStopTime;

    private RealmList<LatLngData> latLngDataList;

    public TripDistanceDetails()
    {

    }

    public TripDistanceDetails(final String tripId, final long tripStartTime)
    {
        this.tripId = tripId;
        this.tripStartTime = tripStartTime;
        latLngDataList = new RealmList<>();
    }

    public void addLocationData(final long timestamp, final double lat, final double lng)
    {
        latLngDataList.add(new LatLngData(timestamp, lat, lng));
    }

    public String getTripId()
    {
        return tripId;
    }

    public long getTripStartTime()
    {
        return tripStartTime;
    }

    private List<LatLngData> getLatLngDataList()
    {
        return latLngDataList;
    }

    public void stopTrip(final long timestamp)
    {
        tripStopTime = timestamp;
    }

    public float getDistanceTravelled()
    {
        float distanceTravelled = 0.0f;
        int totalIterationCount = 0;

        if (getLatLngDataList().size() % 2 == 0)
        {
            totalIterationCount = getLatLngDataList().size() / 2;
        } else
        {
            totalIterationCount = (getLatLngDataList().size() - 1) / 2;
        }

        for (int i = 0; i < totalIterationCount; i++)
        {
            final int start = i * 2;
            final int end = (i * 2) + 1;
            distanceTravelled = distanceTravelled +
                    getDistanceInMeters(getLatLngDataList().get(start).getLat(),
                            getLatLngDataList().get(start).getLng(),
                            getLatLngDataList().get(end).getLat(),
                            getLatLngDataList().get(end).getLng());
        }

        return distanceTravelled;

    }

    private float getDistanceInMeters(double startLat, double startLng, double endLat, double endLng)
    {
        double lat1 = startLat / 1e6;
        double lng1 = startLng / 1e6;
        double lat2 = endLat / 1e6;
        double lng2 = endLng / 1e6;

        float[] dist = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);
        return dist[0]; //* 0.000621371192f;
    }

}
