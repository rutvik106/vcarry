package apimodels;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.fusionbit.vcarry.Constants;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by rutvik on 11/27/2016 at 3:54 PM.
 */

public class TripsByDriverMail extends RealmObject implements Comparable<TripsByDriverMail>
{


    /**
     * trip_id : 2
     * trip_datetime : 2016-09-07 16:17:18
     * from_shipping_location_id : 15
     * from_shipping_location : Jeet Patel
     * from_city_id : 2
     * from_area_id : 566
     * to_shipping_location_id : 16
     * to_shipping_location : shankar chemicals
     * to_city_id : 2
     * to_area_id : 569
     * vehicle_type_id : 2
     * driver_id : 2
     * fare : 250
     * created_by : 27
     * last_modified_by : 27
     * date_added : 2016-09-07 16:17:25
     * date_modified : 2016-09-07 16:17:25
     * trip_status : 1
     * customer_id : 1663
     * status : New
     * customer_name : Jeet Patel
     */

    @PrimaryKey
    @SerializedName("trip_id")
    private String tripId;
    @SerializedName("trip_datetime")
    private String tripDatetime;
    @SerializedName("from_shipping_location_id")
    private String fromShippingLocationId;
    @SerializedName("from_shipping_location")
    private String fromShippingLocation;
    @SerializedName("from_city_id")
    private String fromCityId;
    @SerializedName("from_area_id")
    private String fromAreaId;
    @SerializedName("to_shipping_location_id")
    private String toShippingLocationId;
    @SerializedName("to_shipping_location")
    private String toShippingLocation;
    @SerializedName("to_city_id")
    private String toCityId;
    @SerializedName("to_area_id")
    private String toAreaId;
    @SerializedName("vehicle_type_id")
    private String vehicleTypeId;
    @SerializedName("driver_id")
    private String driverId;
    @SerializedName("fare")
    private String fare;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("last_modified_by")
    private String lastModifiedBy;
    @SerializedName("date_added")
    private String dateAdded;
    @SerializedName("date_modified")
    private String dateModified;
    @SerializedName("trip_status")
    private String tripStatus;
    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("status")
    private String status;
    @SerializedName("customer_name")
    private String customerName;

    public String getTripId()
    {
        return tripId;
    }

    public void setTripId(String tripId)
    {
        this.tripId = tripId;
    }

    public String getTripDatetime()
    {
        return tripDatetime;
    }

    public void setTripDatetime(String tripDatetime)
    {
        this.tripDatetime = tripDatetime;
    }

    public String getFromShippingLocationId()
    {
        return fromShippingLocationId;
    }

    public void setFromShippingLocationId(String fromShippingLocationId)
    {
        this.fromShippingLocationId = fromShippingLocationId;
    }

    public String getFromShippingLocation()
    {
        return fromShippingLocation;
    }

    public void setFromShippingLocation(String fromShippingLocation)
    {
        this.fromShippingLocation = fromShippingLocation;
    }

    public String getFromCityId()
    {
        return fromCityId;
    }

    public void setFromCityId(String fromCityId)
    {
        this.fromCityId = fromCityId;
    }

    public String getFromAreaId()
    {
        return fromAreaId;
    }

    public void setFromAreaId(String fromAreaId)
    {
        this.fromAreaId = fromAreaId;
    }

    public String getToShippingLocationId()
    {
        return toShippingLocationId;
    }

    public void setToShippingLocationId(String toShippingLocationId)
    {
        this.toShippingLocationId = toShippingLocationId;
    }

    public String getToShippingLocation()
    {
        return toShippingLocation;
    }

    public void setToShippingLocation(String toShippingLocation)
    {
        this.toShippingLocation = toShippingLocation;
    }

    public String getToCityId()
    {
        return toCityId;
    }

    public void setToCityId(String toCityId)
    {
        this.toCityId = toCityId;
    }

    public String getToAreaId()
    {
        return toAreaId;
    }

    public void setToAreaId(String toAreaId)
    {
        this.toAreaId = toAreaId;
    }

    public String getVehicleTypeId()
    {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId)
    {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getDriverId()
    {
        return driverId;
    }

    public void setDriverId(String driverId)
    {
        this.driverId = driverId;
    }

    public String getFare()
    {
        return fare;
    }

    public void setFare(String fare)
    {
        this.fare = fare;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy()
    {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy)
    {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getDateAdded()
    {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded)
    {
        this.dateAdded = dateAdded;
    }

    public String getDateModified()
    {
        return dateModified;
    }

    public void setDateModified(String dateModified)
    {
        this.dateModified = dateModified;
    }

    public String getTripStatus()
    {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus)
    {
        this.tripStatus = tripStatus;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    @Override
    public int compareTo(@NonNull TripsByDriverMail tripDetails)
    {
        int status = Integer.valueOf(tripDetails.getTripStatus());
        if (status <= Integer.valueOf(Constants.TRIP_STATUS_TRIP_STARTED))
        {
            return 1;
        } else
        {
            return -1;
        }
    }

    /*@Override
    public boolean equals(Object c)
    {
        if (!(c instanceof TripsByDriverMail))
        {
            return false;
        }

        TripsByDriverMail that = (TripsByDriverMail) c;
        return this.getTripId().equals(that.getTripId());
    }*/

}
