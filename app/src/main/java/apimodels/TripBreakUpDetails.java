package apimodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rutvik on 5/9/2017 at 8:11 PM.
 */

public class TripBreakUpDetails
{


    /**
     * item_name : Trip Charges
     * item_desc : Soni ani chali - Na
     * discounted_customer_fare : 1300
     * driver_fare : 0
     * driver_share : 0
     * premium : 1300
     * driver_premium : 650
     * driver_share_percent : 95
     * total_driver_share : 650
     * discount : 0
     * amount : 300
     * net_amount : 300
     * tax_group_name : null
     * tax_amount : null
     */


    @SerializedName("item_name")
    private String itemName;
    @SerializedName("item_desc")
    private String itemDesc;
    @SerializedName("discounted_customer_fare")
    private String discountedCustomerFare;
    @SerializedName("driver_share")
    private double driverShare;
    @SerializedName("driver_premium")
    private double driverPremium;
    @SerializedName("total_driver_share")
    private double totalDriverShare;
    @SerializedName("discount")
    private String discount;
    @SerializedName("net_amount")
    private String netAmount;

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemDesc()
    {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc)
    {
        this.itemDesc = itemDesc;
    }

    public String getDiscountedCustomerFare()
    {
        return discountedCustomerFare;
    }

    public void setDiscountedCustomerFare(String discountedCustomerFare)
    {
        this.discountedCustomerFare = discountedCustomerFare;
    }

    public double getDriverShare()
    {
        return driverShare;
    }

    public void setDriverShare(int driverShare)
    {
        this.driverShare = driverShare;
    }

    public double getDriverPremium()
    {
        return driverPremium;
    }

    public void setDriverPremium(int driverPremium)
    {
        this.driverPremium = driverPremium;
    }

    public double getTotalDriverShare()
    {
        return totalDriverShare;
    }

    public void setTotalDriverShare(int totalDriverShare)
    {
        this.totalDriverShare = totalDriverShare;
    }

    public String getDiscount()
    {
        return discount;
    }

    public void setDiscount(String discount)
    {
        this.discount = discount;
    }

    public String getNetAmount()
    {
        return netAmount;
    }

    public void setNetAmount(String netAmount)
    {
        this.netAmount = netAmount;
    }
}
