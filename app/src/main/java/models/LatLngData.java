package models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rutvik on 12/3/2016 at 9:40 AM.
 */

public class LatLngData extends RealmObject
{

    @PrimaryKey
    long timestamp;

    double lat, lng;

    public LatLngData()
    {

    }

    public LatLngData(final long timestamp, final double lat, final double lng)
    {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
