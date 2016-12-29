package apimodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.fusionbit.vcarry.Constants;

/**
 * Created by rutvik on 12/24/2016 at 9:11 AM.
 */

public class AccountSummary
{


    /**
     * received : 300
     * receivable : 320
     */

    @SerializedName("received")
    private int received = 0;
    @SerializedName("receivable")
    private int receivable = 0;

    private int receivedToday = 0, receivableToday = 0, receivedThisMonth = 0, receivableThisMonth = 0,
            totalReceived = 0, totalReceivable = 0;

    private final List<TripsByDriverMail> tripToday = new ArrayList<>();
    private final List<TripsByDriverMail> tripThisMonth = new ArrayList<>();
    private final List<TripsByDriverMail> totalTrips = new ArrayList<>();

    public List<TripsByDriverMail> getTripToday()
    {
        return tripToday;
    }

    public List<TripsByDriverMail> getTripThisMonth()
    {
        return tripThisMonth;
    }

    public List<TripsByDriverMail> getTotalTrips()
    {
        return totalTrips;
    }

    public int getReceived()
    {
        return received;
    }

    public void setReceived(int received)
    {
        this.received = received;
    }

    public int getReceivable()
    {
        return receivable;
    }

    public void setReceivable(int receivable)
    {
        this.receivable = receivable;
    }

    public int getReceivedToday()
    {
        return receivedToday;
    }

    public void setReceivedToday(int receivedToday)
    {
        this.receivedToday = receivedToday;
    }

    public int getReceivableToday()
    {
        return receivableToday;
    }

    public void setReceivableToday(int receivableToday)
    {
        this.receivableToday = receivableToday;
    }

    public int getReceivedThisMonth()
    {
        return receivedThisMonth;
    }

    public void setReceivedThisMonth(int receivedThisMonth)
    {
        this.receivedThisMonth = receivedThisMonth;
    }

    public int getReceivableThisMonth()
    {
        return receivableThisMonth;
    }

    public void setReceivableThisMonth(int receivableThisMonth)
    {
        this.receivableThisMonth = receivableThisMonth;
    }

    public int getTotalReceived()
    {
        return totalReceived;
    }

    public void setTotalReceived(int totalReceived)
    {
        this.totalReceived = totalReceived;
    }

    public int getTotalReceivable()
    {
        return totalReceivable;
    }

    public void setTotalReceivable(int totalReceivable)
    {
        this.totalReceivable = totalReceivable;
    }


    //TOTAL
    public int getTotalIncompleteTrips()
    {
        int count = 0;
        for (int i = 0; i < totalTrips.size(); i++)
        {
            if (totalTrips.get(i).getTripStatus().equals(Constants.TRIP_STATUS_CANCELLED_BY_CUSTOMER) ||
                    totalTrips.get(i).getTripStatus().equals(Constants.TRIP_STATUS_CANCELLED_BY_DRIVER))
            {
                count++;
            }
        }

        return count;
    }

    public int getTotalCompletedTrips()
    {
        int count = 0;
        for (int i = 0; i < totalTrips.size(); i++)
        {
            if (totalTrips.get(i).getTripStatus().equals(Constants.TRIP_STATUS_FINISHED))
            {
                count++;
            }
        }

        return count;
    }


    //TODAY
    public int getTodayIncompleteTrips()
    {
        int count = 0;
        for (int i = 0; i < tripToday.size(); i++)
        {
            if (tripToday.get(i).getTripStatus().equals(Constants.TRIP_STATUS_CANCELLED_BY_CUSTOMER) ||
                    tripToday.get(i).getTripStatus().equals(Constants.TRIP_STATUS_CANCELLED_BY_DRIVER))
            {
                count++;
            }
        }

        return count;
    }

    public int getTodayCompletedTrips()
    {
        int count = 0;
        for (int i = 0; i < tripToday.size(); i++)
        {
            if (tripToday.get(i).getTripStatus().equals(Constants.TRIP_STATUS_FINISHED))
            {
                count++;
            }
        }

        return count;
    }


    //THIS MONTH
    public int getThisMonthIncompleteTrips()
    {
        int count = 0;
        for (int i = 0; i < tripThisMonth.size(); i++)
        {
            if (tripThisMonth.get(i).getTripStatus().equals(Constants.TRIP_STATUS_CANCELLED_BY_CUSTOMER) ||
                    tripThisMonth.get(i).getTripStatus().equals(Constants.TRIP_STATUS_CANCELLED_BY_DRIVER))
            {
                count++;
            }
        }

        return count;
    }

    public int getThisMonthCompletedTrips()
    {
        int count = 0;
        for (int i = 0; i < tripThisMonth.size(); i++)
        {
            if (totalTrips.get(i).getTripStatus().equals(Constants.TRIP_STATUS_FINISHED))
            {
                count++;
            }
        }

        return count;
    }

}
