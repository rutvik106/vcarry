package apimodels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rutvik on 3/29/2017 at 11:10 PM.
 */

public class DriverDetails extends RealmObject
{

    /**
     * 0 : 2
     * driver_id : 2
     * 1 : Rutvik Mehta
     * driver_name : Rutvik Mehta
     * 2 : 9824143009
     * contact_no_1 : 9824143009
     * 3 : 0
     * contact_no_2 : 0
     * 4 : 2
     * vehicle_type_id : 2
     * 5 : 567
     * area_id : 567
     * 6 : 2
     * type : 2
     * 7 : 200
     * fixed_amount : 200
     * 8 : 50
     * share_expense : 50
     * 9 : 27
     * created_by : 27
     * 10 : 27
     * last_updated_by : 27
     * 11 : 2016-07-21 19:57:49
     * date_added : 2016-07-21 19:57:49
     * 12 : 2017-02-21 16:44:17
     * date_modified : 2017-02-21 16:44:17
     * 13 : 5
     * ledger_id : 5
     * 14 : rutvik1061992@gmail.com
     * email : rutvik1061992@gmail.com
     * 15 : 0
     * multi_trip : 0
     * 16 : e0NWMj3QuG8:APA91bH6SvHTj3Dt-RNirTjIxJDs-1iPsUsjQuZvcX_pq5yt8yTaQBIylI5vE7xLUg6zBkAjW124gQIZ4LZdSCYvpWkPLiwwrtFWcQVBwDDTy1f-e9apwhwrYYjNKs5yyMWjIjMCVto8
     * device_token : e0NWMj3QuG8:APA91bH6SvHTj3Dt-RNirTjIxJDs-1iPsUsjQuZvcX_pq5yt8yTaQBIylI5vE7xLUg6zBkAjW124gQIZ4LZdSCYvpWkPLiwwrtFWcQVBwDDTy1f-e9apwhwrYYjNKs5yyMWjIjMCVto8
     * 17 : 1
     * active : 1
     * 18 :
     * vehicle_reg_no :
     * 19 :
     * licence_no :
     * 20 : Atul Loading
     * vehicle_type : Atul Loading
     * 21 : 23.0265168,72.5147056
     * lat_long : 23.0265168,72.5147056
     * 22 : 2017-04-02 16:33:20
     * lat_long_datetime : 2017-04-02 16:33:20
     */

    @PrimaryKey
    @SerializedName("driver_id")
    private String driverId;
    @SerializedName("driver_name")
    private String driverName;
    @SerializedName("contact_no_1")
    private String contactNo1;
    @SerializedName("contact_no_2")
    private String contactNo2;
    @SerializedName("vehicle_type_id")
    private String vehicleTypeId;
    @SerializedName("area_id")
    private String areaId;
    @SerializedName("type")
    private String type;
    @SerializedName("fixed_amount")
    private String fixedAmount;
    @SerializedName("share_expense")
    private String shareExpense;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("last_updated_by")
    private String lastUpdatedBy;
    @SerializedName("date_added")
    private String dateAdded;
    @SerializedName("date_modified")
    private String dateModified;
    @SerializedName("ledger_id")
    private String ledgerId;
    @SerializedName("email")
    private String email;
    @SerializedName("multi_trip")
    private String multiTrip;
    @SerializedName("device_token")
    private String deviceToken;
    @SerializedName("active")
    private String active;
    @SerializedName("vehicle_reg_no")
    private String vehicleRegNo;
    @SerializedName("licence_no")
    private String licenceNo;
    @SerializedName("vehicle_type")
    private String vehicleType;
    @SerializedName("lat_long")
    private String latLong;
    @SerializedName("lat_long_datetime")
    private String latLongDatetime;

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

    public String getContactNo1()
    {
        return contactNo1;
    }

    public void setContactNo1(String contactNo1)
    {
        this.contactNo1 = contactNo1;
    }

    public String getContactNo2()
    {
        return contactNo2;
    }

    public void setContactNo2(String contactNo2)
    {
        this.contactNo2 = contactNo2;
    }

    public String getVehicleTypeId()
    {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId)
    {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getAreaId()
    {
        return areaId;
    }

    public void setAreaId(String areaId)
    {
        this.areaId = areaId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getFixedAmount()
    {
        return fixedAmount;
    }

    public void setFixedAmount(String fixedAmount)
    {
        this.fixedAmount = fixedAmount;
    }

    public String getShareExpense()
    {
        return shareExpense;
    }

    public void setShareExpense(String shareExpense)
    {
        this.shareExpense = shareExpense;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy)
    {
        this.lastUpdatedBy = lastUpdatedBy;
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

    public String getLedgerId()
    {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId)
    {
        this.ledgerId = ledgerId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getMultiTrip()
    {
        return multiTrip;
    }

    public void setMultiTrip(String multiTrip)
    {
        this.multiTrip = multiTrip;
    }

    public String getDeviceToken()
    {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken)
    {
        this.deviceToken = deviceToken;
    }

    public String getActive()
    {
        return active;
    }

    public void setActive(String active)
    {
        this.active = active;
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

    public String getVehicleType()
    {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType)
    {
        this.vehicleType = vehicleType;
    }

    public String getLatLong()
    {
        return latLong;
    }

    public void setLatLong(String latLong)
    {
        this.latLong = latLong;
    }

    public String getLatLongDatetime()
    {
        return latLongDatetime;
    }

    public void setLatLongDatetime(String latLongDatetime)
    {
        this.latLongDatetime = latLongDatetime;
    }
}
