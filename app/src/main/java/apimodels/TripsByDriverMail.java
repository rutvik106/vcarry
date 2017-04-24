package apimodels;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import io.fusionbit.vcarry.Constants;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rutvik on 11/27/2016 at 3:54 PM.
 */

public class TripsByDriverMail extends RealmObject implements Comparable<TripsByDriverMail>, Parcelable
{


    public static final Creator<TripsByDriverMail> CREATOR = new Creator<TripsByDriverMail>()
    {
        @Override
        public TripsByDriverMail createFromParcel(Parcel source)
        {
            return new TripsByDriverMail(source);
        }

        @Override
        public TripsByDriverMail[] newArray(int size)
        {
            return new TripsByDriverMail[size];
        }
    };
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
    private Date tripDatetime;
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
    @SerializedName("driver_fare")
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
    /**
     * trip_datetime_dmy : 05/02/2017 11:36:56 AM
     * from_address_line1 : 54, Rangin Park Soc, S.G.Highway. Bodakdev
     * from_address_line2 : Rangin Park Soc
     * from_city_name : Ahmedabad
     * from_area_name : S g highway
     * to_address_line1 : Jodhpur park society
     * to_address_line2 : Jodhpur park society Near Ramdev Nagar BRTS Bus stop
     * to_city_name : Ahmedabad
     * to_area_name : Kankriya
     * customer_contact_no : 9409210477
     * trip_no : 050220170000019
     */

    @SerializedName("trip_datetime_dmy")
    private String tripDatetimeDmy;
    @SerializedName("from_address_line1")
    private String fromAddressLine1;
    @SerializedName("from_address_line2")
    private String fromAddressLine2;
    @SerializedName("from_city_name")
    private String fromCityName;
    @SerializedName("from_area_name")
    private String fromAreaName;
    @SerializedName("to_address_line1")
    private String toAddressLine1;
    @SerializedName("to_address_line2")
    private String toAddressLine2;
    @SerializedName("to_city_name")
    private String toCityName;
    @SerializedName("to_area_name")
    private String toAreaName;
    @SerializedName("customer_contact_no")
    private String customerContactNo;
    @SerializedName("trip_no")
    private String tripNo;
    /**
     * 0 : 783
     * 1 : 2017-03-23 17:10:38
     * 2 : 23/03/2017 05:10:38 PM
     * 3 : 25
     * 4 : Amishi Mehta
     * 5 : Jodhpur park society
     * 6 : Jodhpur park society Near Ramdev Nagar BRTS Bus stop
     * 7 :
     * from_gujarati_address :
     * 8 : 2
     * 9 : Ahmedabad
     * 10 : 567
     * 11 : Kankriya
     * 12 : 35
     * 13 : jeet patel
     * 14 : 54, Rangin Park Soc, S.G.Highway. Bodakdev
     * 15 : Rangin Park Soc
     * 16 :
     * to_gujarati_address :
     * 17 : 2
     * 18 : Ahmedabad
     * 19 : 566
     * 20 : S g highway
     * 21 : 1
     * 22 : Tata Ace
     * vehicle_type : Tata Ace
     * 23 : 2
     * 24 : 212
     * 25 : 27
     * 26 : 27
     * 27 : 2017-03-23 17:11:01
     * 28 : 2017-03-23 17:11:22
     * 29 : 6
     * 30 : 1664
     * 31 : Finished
     * 32 : Amishi Mehta
     * 33 : 9409210477
     * 34 : 201703230000424
     * 35 : 1490269349122
     * trip_start_time : 1490269349122
     * 36 : 1490269386949
     * trip_stop_time : 1490269386949
     * 37 : 23.0260084,72.5149829
     * trip_start_latlong : 23.0260084,72.5149829
     * 38 : 23.0261119,72.5146944
     * trip_stop_latlong : 23.0261119,72.5146944
     * 39 : 0
     * trip_distance : 0
     * 40 : 0
     * memo_amount : 0
     * 41 : 0
     * labour_amount : 0
     * 42 : 212
     * cash_received : 212
     * 43 :
     * lat_long : 23.0333222,72.5104536
     * 44 : 23.0333222,72.5104536
     * 45 : 200
     * weight : 200
     * 46 : 23x123
     * dimensions : 23x123
     */

    @SerializedName("from_gujarati_address")
    private String fromGujaratiAddress;
    @SerializedName("to_gujarati_address")
    private String toGujaratiAddress;
    @SerializedName("vehicle_type")
    private String vehicleType;
    @SerializedName("trip_start_time")
    private String tripStartTime;
    @SerializedName("trip_stop_time")
    private String tripStopTime;
    @SerializedName("trip_start_latlong")
    private String tripStartLatlong;
    @SerializedName("trip_stop_latlong")
    private String tripStopLatlong;
    @SerializedName("trip_distance")
    private String tripDistance;
    @SerializedName("memo_amount")
    private String memoAmount;
    @SerializedName("labour_amount")
    private String labourAmount;
    @SerializedName("cash_received")
    private String cashReceived;
    @SerializedName("lat_long")
    private String latLong;
    @SerializedName("weight")
    private String weight;
    @SerializedName("dimensions")
    private String dimensions;
    /**
     * 0 : 378
     * 1 : 2017-02-05 11:36:56
     * trip_datetime : 2017-02-05 11:36:56
     * 2 : 05/02/2017 11:36:56 AM
     * 3 : 35
     * 4 : jeet patel
     * 5 : 54, Rangin Park Soc, S.G.Highway. Bodakdev
     * 6 : Rangin Park Soc
     * 7 :
     * 8 : 2
     * 9 : Ahmedabad
     * 10 : 566
     * 11 : S g highway
     * 12 : 25
     * 13 : Amishi Mehta
     * 14 : Jodhpur park society
     * 15 : Jodhpur park society Near Ramdev Nagar BRTS Bus stop
     * 16 :
     * 17 : 2
     * 18 : Ahmedabad
     * 19 : 567
     * 20 : Kankriya
     * 21 : 1
     * 22 : Tata Ace
     * 23 : 2
     * 24 : 200
     * fare : 200
     * 25 : 27
     * 26 : 27
     * 27 : 2017-02-05 11:36:59
     * 28 : 2017-02-05 11:37:14
     * 29 : 6
     * 30 : 1664
     * 31 : Finished
     * 32 : Amishi Mehta
     * 33 : 9409210477
     * 34 : 050220170000019
     * 35 : 1486274882049
     * 36 : 1486276669613
     * 37 : 23.0265168,72.5147056
     * 38 : 23.026514,72.5147277
     * 39 : 0
     * 40 : 0
     * 41 : 0
     * 42 : 0
     * 43 : 23.0333222,72.5104536
     * 44 : 23.0265273,72.5147981
     * 45 : 0
     * 46 :
     * 47 : 0
     * 48 : https://lh6.googleusercontent.com/-8twv_aWLqtY/AAAAAAAAAAI/AAAAAAAAAQ8/K1r--rxdH3w/s96-c/photo.jpg
     * driver_image : https://lh6.googleusercontent.com/-8twv_aWLqtY/AAAAAAAAAAI/AAAAAAAAAQ8/K1r--rxdH3w/s96-c/photo.jpg
     * 49 : 9824143009
     * from_contact_no : 9824143009
     * 50 : 9409210477
     * to_contact_no : 9409210477
     */

    @SerializedName("driver_image")
    private String driverImage;
    @SerializedName("from_contact_no")
    private String fromContactNo;
    @SerializedName("to_contact_no")
    private String toContactNo;


    public TripsByDriverMail()
    {
    }

    protected TripsByDriverMail(Parcel in)
    {
        this.tripId = in.readString();
        //this.tripDatetime = in.readString();
        this.fromShippingLocationId = in.readString();
        this.fromShippingLocation = in.readString();
        this.fromCityId = in.readString();
        this.fromAreaId = in.readString();
        this.toShippingLocationId = in.readString();
        this.toShippingLocation = in.readString();
        this.toCityId = in.readString();
        this.toAreaId = in.readString();
        this.vehicleTypeId = in.readString();
        this.driverId = in.readString();
        this.fare = in.readString();
        this.createdBy = in.readString();
        this.lastModifiedBy = in.readString();
        this.dateAdded = in.readString();
        this.dateModified = in.readString();
        this.tripStatus = in.readString();
        this.customerId = in.readString();
        this.status = in.readString();
        this.customerName = in.readString();
        this.tripDatetimeDmy = in.readString();
        this.fromAddressLine1 = in.readString();
        this.fromAddressLine2 = in.readString();
        this.fromCityName = in.readString();
        this.fromAreaName = in.readString();
        this.toAddressLine1 = in.readString();
        this.toAddressLine2 = in.readString();
        this.toCityName = in.readString();
        this.toAreaName = in.readString();
        this.customerContactNo = in.readString();
        this.tripNo = in.readString();
    }

    public String getTripId()
    {
        return tripId;
    }

    public void setTripId(String tripId)
    {
        this.tripId = tripId;
    }

    public Date getTripDatetime()
    {
        return tripDatetime;
    }

    public void setTripDatetime(Date tripDatetime)
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
        return getFromAddressLine1() + ", " +
                getFromAddressLine2() + ", " +
                getFromAreaName() + ", " + getFromCityName();
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
        return getToAddressLine1() + ", " +
                getToAddressLine2() + ", " +
                getToAreaName() + ", " + getToCityName();
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

    public String getTripDatetimeDmy()
    {
        return tripDatetimeDmy;
    }

    public void setTripDatetimeDmy(String tripDatetimeDmy)
    {
        this.tripDatetimeDmy = tripDatetimeDmy;
    }

    public String getFromAddressLine1()
    {
        return fromAddressLine1;
    }

    public void setFromAddressLine1(String fromAddressLine1)
    {
        this.fromAddressLine1 = fromAddressLine1;
    }

    public String getFromAddressLine2()
    {
        return fromAddressLine2;
    }

    public void setFromAddressLine2(String fromAddressLine2)
    {
        this.fromAddressLine2 = fromAddressLine2;
    }

    public String getFromCityName()
    {
        return fromCityName;
    }

    public void setFromCityName(String fromCityName)
    {
        this.fromCityName = fromCityName;
    }

    public String getFromAreaName()
    {
        return fromAreaName;
    }

    public void setFromAreaName(String fromAreaName)
    {
        this.fromAreaName = fromAreaName;
    }

    public String getToAddressLine1()
    {
        return toAddressLine1;
    }

    public void setToAddressLine1(String toAddressLine1)
    {
        this.toAddressLine1 = toAddressLine1;
    }

    public String getToAddressLine2()
    {
        return toAddressLine2;
    }

    public void setToAddressLine2(String toAddressLine2)
    {
        this.toAddressLine2 = toAddressLine2;
    }

    public String getToCityName()
    {
        return toCityName;
    }

    public void setToCityName(String toCityName)
    {
        this.toCityName = toCityName;
    }

    public String getToAreaName()
    {
        return toAreaName;
    }

    public void setToAreaName(String toAreaName)
    {
        this.toAreaName = toAreaName;
    }

    public String getCustomerContactNo()
    {
        return customerContactNo;
    }

    public void setCustomerContactNo(String customerContactNo)
    {
        this.customerContactNo = customerContactNo;
    }

    public String getTripNo()
    {
        return tripNo;
    }

    public void setTripNo(String tripNo)
    {
        this.tripNo = tripNo;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.tripId);
        //dest.writeString(this.tripDatetime);
        dest.writeString(this.fromShippingLocationId);
        dest.writeString(this.fromShippingLocation);
        dest.writeString(this.fromCityId);
        dest.writeString(this.fromAreaId);
        dest.writeString(this.toShippingLocationId);
        dest.writeString(this.toShippingLocation);
        dest.writeString(this.toCityId);
        dest.writeString(this.toAreaId);
        dest.writeString(this.vehicleTypeId);
        dest.writeString(this.driverId);
        dest.writeString(this.fare);
        dest.writeString(this.createdBy);
        dest.writeString(this.lastModifiedBy);
        dest.writeString(this.dateAdded);
        dest.writeString(this.dateModified);
        dest.writeString(this.tripStatus);
        dest.writeString(this.customerId);
        dest.writeString(this.status);
        dest.writeString(this.customerName);
        dest.writeString(this.tripDatetimeDmy);
        dest.writeString(this.fromAddressLine1);
        dest.writeString(this.fromAddressLine2);
        dest.writeString(this.fromCityName);
        dest.writeString(this.fromAreaName);
        dest.writeString(this.toAddressLine1);
        dest.writeString(this.toAddressLine2);
        dest.writeString(this.toCityName);
        dest.writeString(this.toAreaName);
        dest.writeString(this.customerContactNo);
        dest.writeString(this.tripNo);
    }

    public String getFromGujaratiAddress()
    {
        try
        {
            if (fromGujaratiAddress != null)
            {
                if (!fromGujaratiAddress.isEmpty())
                {
                    // Convert from Unicode to UTF-8
                    String string = fromGujaratiAddress;
                    byte[] utf8 = string.getBytes("UTF-8");

                    // Convert from UTF-8 to Unicode
                    return new String(utf8, "UTF-8");
                } else
                {
                    return getFromAddressLine1() + ", " +
                            getFromAddressLine2() + ", " +
                            getFromAreaName() + ", " +
                            getFromCityName();
                }
            } else
            {
                return getFromAddressLine1() + ", " +
                        getFromAddressLine2() + ", " +
                        getFromAreaName() + ", " +
                        getFromCityName();
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return getFromAddressLine1() + ", " +
                    getFromAddressLine2() + ", " +
                    getFromAreaName() + ", " +
                    getFromCityName();
        }
    }

    public void setFromGujaratiAddress(String fromGujaratiAddress)
    {
        this.fromGujaratiAddress = fromGujaratiAddress;
    }

    public String getToGujaratiAddress()
    {
        try
        {
            if (toGujaratiAddress != null)
            {
                if (!toGujaratiAddress.isEmpty())
                {
                    // Convert from Unicode to UTF-8
                    String string = toGujaratiAddress;
                    byte[] utf8 = string.getBytes("UTF-8");

                    // Convert from UTF-8 to Unicode
                    return new String(utf8, "UTF-8");
                } else
                {
                    return getToAddressLine1() + ", " +
                            getToAddressLine2() + ", " +
                            getToAreaName() + ", " +
                            getToCityName();
                }
            } else
            {
                return getToAddressLine1() + ", " +
                        getToAddressLine2() + ", " +
                        getToAreaName() + ", " +
                        getToCityName();
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return getToAddressLine1() + ", " +
                    getToAddressLine2() + ", " +
                    getToAreaName() + ", " +
                    getToCityName();
        }
    }

    public void setToGujaratiAddress(String toGujaratiAddress)
    {
        this.toGujaratiAddress = toGujaratiAddress;
    }

    public String getVehicleType()
    {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType)
    {
        this.vehicleType = vehicleType;
    }

    public String getTripStartTime()
    {
        return tripStartTime;
    }

    public void setTripStartTime(String tripStartTime)
    {
        this.tripStartTime = tripStartTime;
    }

    public String getTripStopTime()
    {
        return tripStopTime;
    }

    public void setTripStopTime(String tripStopTime)
    {
        this.tripStopTime = tripStopTime;
    }

    public String getTripStartLatlong()
    {
        return tripStartLatlong;
    }

    public void setTripStartLatlong(String tripStartLatlong)
    {
        this.tripStartLatlong = tripStartLatlong;
    }

    public String getTripStopLatlong()
    {
        return tripStopLatlong;
    }

    public void setTripStopLatlong(String tripStopLatlong)
    {
        this.tripStopLatlong = tripStopLatlong;
    }

    public String getTripDistance()
    {
        return tripDistance;
    }

    public void setTripDistance(String tripDistance)
    {
        this.tripDistance = tripDistance;
    }

    public String getMemoAmount()
    {
        return memoAmount;
    }

    public void setMemoAmount(String memoAmount)
    {
        this.memoAmount = memoAmount;
    }

    public String getLabourAmount()
    {
        return labourAmount;
    }

    public void setLabourAmount(String labourAmount)
    {
        this.labourAmount = labourAmount;
    }

    public String getCashReceived()
    {
        return cashReceived;
    }

    public void setCashReceived(String cashReceived)
    {
        this.cashReceived = cashReceived;
    }

    public String getLatLong()
    {
        return latLong;
    }

    public void setLatLong(String latLong)
    {
        this.latLong = latLong;
    }

    public String getWeight()
    {
        return weight;
    }

    public void setWeight(String weight)
    {
        this.weight = weight;
    }

    public String getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(String dimensions)
    {
        this.dimensions = dimensions;
    }

    public String getDriverImage()
    {
        return driverImage;
    }

    public void setDriverImage(String driverImage)
    {
        this.driverImage = driverImage;
    }

    public String getFromContactNo()
    {
        return fromContactNo;
    }

    public void setFromContactNo(String fromContactNo)
    {
        this.fromContactNo = fromContactNo;
    }

    public String getToContactNo()
    {
        return toContactNo;
    }

    public void setToContactNo(String toContactNo)
    {
        this.toContactNo = toContactNo;
    }
}
