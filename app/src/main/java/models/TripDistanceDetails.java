package models;

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

    public long getTripStopTime()
    {
        return tripStopTime;
    }

    private RealmList<LatLngData> getLatLngDataList()
    {
        return latLngDataList;
    }

    public void stopTrip(final long timestamp)
    {
        tripStopTime = timestamp;
    }

    public double getDistanceTravelled()
    {
        double distanceTravelled = 0;
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

        distanceTravelled = distanceTravelled / 1000;

        double roundOff = Math.round(distanceTravelled * 100.0) / 100.0;

        return roundOff;

    }

    private double getDistanceInMeters(double startLat, double startLng, double endLat, double endLng)
    {
        float pk = (float) (180.f / Math.PI);

        double a1 = startLat / pk;
        double a2 = startLng / pk;
        double b1 = endLat / pk;
        double b2 = endLng / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

}
