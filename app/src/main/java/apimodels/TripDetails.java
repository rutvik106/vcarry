package apimodels;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import extra.Utils;
import io.fusionbit.vcarry.App;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rutvik on 11/27/2016 at 4:01 PM.
 */

public class TripDetails extends RealmObject implements Comparable<TripDetails>
{


    /**
     * trip_id : 24
     * trip_datetime : 2016-11-30 15:09:40
     * from_shipping_location_id : 15
     * to_shipping_location_id : 16
     * from_shipping_location : Jeet Patel
     * from_city_id : 2
     * from_area_id : 566
     * to_shipping_location : shankar chemicals
     * to_city_id : 2
     * to_area_id : 569
     * vehicle_type_id : 2
     * driver_id : 2
     * driver_name : jj
     * fare : 250
     * created_by : 27
     * last_modified_by : 27
     * date_added : 2016-11-30 15:09:49
     * date_modified : 2016-11-30 15:10:14
     * trip_status : 2
     * customer_id : 1663
     * status : Driver Allocated
     * customer_name : Jeet Patel
     */

    @PrimaryKey
    @SerializedName("trip_id")
    private String tripId;
    @SerializedName("trip_datetime")
    private String tripDatetime;
    @SerializedName("from_shipping_location_id")
    private String fromShippingLocationId;
    @SerializedName("to_shipping_location_id")
    private String toShippingLocationId;
    @SerializedName("from_shipping_location")
    private String fromShippingLocation;
    @SerializedName("from_city_id")
    private String fromCityId;
    @SerializedName("from_area_id")
    private String fromAreaId;
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
    @SerializedName("driver_name")
    private String driverName;
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
     * from_city_name : Ahmedabad
     * from_area_name : S g highway
     * to_city_name : Ahmedabad
     * to_area_name : Vatva
     * customer_contact_no : 9824143009
     */

    @SerializedName("from_city_name")
    private String fromCityName;
    @SerializedName("from_area_name")
    private String fromAreaName;
    @SerializedName("to_city_name")
    private String toCityName;
    @SerializedName("to_area_name")
    private String toAreaName;
    @SerializedName("customer_contact_no")
    private String customerContactNo;
    /**
     * trip_datetime_dmy : 05/02/2017 11:36:56 AM
     * from_address_line1 : 54, Rangin Park Soc, S.G.Highway. Bodakdev
     * from_address_line2 : Rangin Park Soc
     * to_address_line1 : Jodhpur park society
     * to_address_line2 : Jodhpur park society Near Ramdev Nagar BRTS Bus stop
     * vehicle_type : Tata Ace
     * trip_no : 050220170000019
     */

    @SerializedName("trip_datetime_dmy")
    private String tripDatetimeDmy;
    @SerializedName("from_address_line1")
    private String fromAddressLine1;
    @SerializedName("from_address_line2")
    private String fromAddressLine2;
    @SerializedName("to_address_line1")
    private String toAddressLine1;
    @SerializedName("to_address_line2")
    private String toAddressLine2;
    @SerializedName("vehicle_type")
    private String vehicleType;
    @SerializedName("trip_no")
    private String tripNo;
    /**
     * trip_start_time :
     * trip_stop_time :
     * trip_start_latlong :
     * trip_stop_latlong :
     * trip_distance :
     * memo_amount :
     * labour_amount :
     * cash_received :
     * lat_long :
     * weight : 980
     * dimensions : 70x90
     */

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
    private boolean returnGujaratiAddress;
    /**
     * to_gujarati_address :
     * from_gujarati_address :
     */

    @SerializedName("to_gujarati_address")
    private String toGujaratiAddress;
    @SerializedName("from_gujarati_address")
    private String fromGujaratiAddress;
    /**
     * 0 : 875
     * 1 : 2017-04-03 11:52:08
     * 2 : 03/04/2017 11:52:08 AM
     * 3 : 163
     * 4 : 139
     * 5 : Home
     * 6 : 52/53 Jodhpur park society, Opp courtyard marriott,
     * 7 :
     * 8 : ૫૨,૫૩ જોધપુર પાર્ક સોસાયટી, રામદેવ નગર, સ્ટૅલલિતે રોડ.
     * 9 :
     * from_gujarati_name :
     * 10 : 2
     * 11 : Ahmedabad
     * 12 : 568
     * 13 : Ramdevnagar
     * 14 : to location from map
     * 15 : Narmadashanker Road, Girdhar Nagar, Ahmedabad, Gujarat 380004
     * 16 :
     * 17 :
     * 18 :
     * to_gujarati_name :
     * 19 : 2
     * 20 : Ahmedabad
     * 21 : 576
     * 22 : Rakhiyal
     * 23 : 2
     * 24 : Atul Loading
     * 25 : 2
     * 26 : Rutvik Mehta
     * 27 :
     * vehicle_reg_no :
     * 28 :
     * licence_no :
     * 29 : 1
     * 30 : 27
     * 31 : 27
     * 32 : 2017-04-03 11:52:50
     * 33 : 2017-04-06 14:32:42
     * 34 : 6
     * 35 : 1664
     * 36 : Finished
     * 37 : Amishi Mehta
     * 38 : 9409210477
     * 39 : 201704030000516
     * 40 :
     * cancel_desc :
     * 41 : 1
     * 42 : 1
     */

    @SerializedName("from_gujarati_name")
    private String fromGujaratiName;
    @SerializedName("to_gujarati_name")
    private String toGujaratiName;
    @SerializedName("vehicle_reg_no")
    private String vehicleRegNo;
    @SerializedName("licence_no")
    private String licenceNo;
    @SerializedName("cancel_desc")
    private String cancelDesc;
    /**
     * 0 : 422
     * 1 : 2017-02-09 14:00:00
     * 2 : 09/02/2017 02:00:00 PM
     * 3 : 29
     * 4 : 30
     * 5 : Pioneer Hydraulics
     * 6 : 21,Shayam Ind.Estate
     * 7 : Near Soni Ni Chali BRTS, NH.No - 8
     * 8 :  ૨૧ ,શયમ  ઇન્ડ .એસ્ટેટ  નેર  સોની  ની ચાલી  BRTS , NH.નો  - ૮
     * 9 :
     * 10 : 2
     * 11 : Ahmedabad
     * 12 : 580
     * 13 : Soni ani chali
     * 14 :
     * from_lat_long :
     * 15 : Perfect Honing
     * 16 : 36 Gopinath Estate II,
     * 17 : Near Soni Ni Chali BRTS, NH.No - 8
     * 18 : ૩૬  ગોપીનાથ  એસ્ટેટ  II,   Near સોની  ની  ચાલી  BRTS, NH.No - 8
     * 19 :
     * 20 : 2
     * 21 : Ahmedabad
     * 22 : 580
     * 23 : Soni ani chali
     * 24 : 23.0232021,72.6393817
     * to_lat_long : 23.0232021,72.6393817
     * 25 : 2
     * 26 : Three Wheel Tempo
     * 27 : 9
     * 28 : Desai Govind D
     * 29 :
     * 30 :
     * 31 : 50
     * 32 : 27
     * 33 : 27
     * 34 : 2017-02-09 13:48:41
     * 35 : 2017-02-09 13:49:46
     * 36 : 6
     * 37 : 1667
     * 38 : Finished
     * 39 : Pioneer Hydraulics
     * 40 : 9879017267
     * 41 : 090220170000063
     * 42 :
     * 43 : 0
     * 44 :
     */

    @SerializedName("from_lat_long")
    private String fromLatLong;
    @SerializedName("to_lat_long")
    private String toLatLong;


    public boolean isReturnGujaratiAddress()
    {
        return returnGujaratiAddress;
    }

    public void setReturnGujaratiAddress(boolean returnGujaratiAddress)
    {
        this.returnGujaratiAddress = returnGujaratiAddress;
    }

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

    public String getToShippingLocationId()
    {
        return toShippingLocationId;
    }

    public void setToShippingLocationId(String toShippingLocationId)
    {
        this.toShippingLocationId = toShippingLocationId;
    }

    public String getFromShippingLocation()
    {
        if (returnGujaratiAddress)
        {
            return getFromGujaratiAddress();
        }
        return getFromAddressLine1() + ", " +
                getFromAddressLine2() + ", " +
                getFromAreaName() + ", " +
                getFromCityName();
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

    public String getToShippingLocation()
    {
        if (returnGujaratiAddress)
        {
            return getToGujaratiAddress();
        }
        return getToAddressLine1() + ", " +
                getToAddressLine2() + ", " +
                getToAreaName() + ", " +
                getToCityName();
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

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
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
    public int compareTo(@NonNull TripDetails tripDetails)
    {
        Date date1 = Utils.convertToDate(tripDetails.getTripDatetime());
        Date date2 = Utils.convertToDate(getTripDatetime());
        return date1.compareTo(date2);
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

    public String getVehicleType()
    {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType)
    {
        this.vehicleType = vehicleType;
    }

    public String getTripNo()
    {
        return tripNo;
    }

    public void setTripNo(String tripNo)
    {
        this.tripNo = tripNo;
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

    public String getFromGujaratiAddress()
    {
        Log.i(App.APP_TAG, "getFromGujaratiAddress()");

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
        return weight != null ? !weight.isEmpty() ? weight : "NA" : "NA";
    }

    public void setWeight(String weight)
    {
        this.weight = weight;
    }

    public String getDimensions()
    {
        return dimensions != null ? !dimensions.isEmpty() ? dimensions : "NA" : "NA";
    }

    public void setDimensions(String dimensions)
    {
        this.dimensions = dimensions;
    }

    public String getFromGujaratiName()
    {
        return fromGujaratiName != null ? !fromGujaratiName.isEmpty() ? fromGujaratiName : fromShippingLocation : fromShippingLocation;
    }

    public void setFromGujaratiName(String fromGujaratiName)
    {
        this.fromGujaratiName = fromGujaratiName;
    }

    public String getToGujaratiName()
    {
        return toGujaratiName != null ? !toGujaratiName.isEmpty() ? toGujaratiName : toShippingLocation : toShippingLocation;
    }

    public void setToGujaratiName(String toGujaratiName)
    {
        this.toGujaratiName = toGujaratiName;
    }

    public String getVehicleRegNo()
    {
        return vehicleRegNo;
    }

    public void setVehicleRegNo(String vehicleRegNo)
    {
        this.vehicleRegNo = vehicleRegNo;
    }

    public String getLicenceNo()
    {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo)
    {
        this.licenceNo = licenceNo;
    }

    public String getCancelDesc()
    {
        return cancelDesc;
    }

    public void setCancelDesc(String cancelDesc)
    {
        this.cancelDesc = cancelDesc;
    }


    public String getFromCompanyName()
    {
        return fromShippingLocation;
    }

    public String getToCompanyName()
    {
        return toShippingLocation;
    }

    public String getFromLatLong()
    {
        return fromLatLong;
    }

    public void setFromLatLong(String fromLatLong)
    {
        this.fromLatLong = fromLatLong;
    }

    public String getToLatLong()
    {
        return toLatLong;
    }

    public void setToLatLong(String toLatLong)
    {
        this.toLatLong = toLatLong;
    }
}
