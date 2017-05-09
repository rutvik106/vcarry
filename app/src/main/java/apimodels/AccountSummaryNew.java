package apimodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rutvik on 5/9/2017 at 2:48 PM.
 */

public class AccountSummaryNew
{


    /**
     * today : {"received":0,"receivable":0,"no_of_trips":"0"}
     * this_month : {"received":1451,"receivable":542.5,"no_of_trips":"4"}
     * all_time : {"received":4243,"receivable":5985.5,"no_of_trips":"44"}
     */

    @SerializedName("today")
    private TodayBean today;
    @SerializedName("this_month")
    private ThisMonthBean thisMonth;
    @SerializedName("all_time")
    private AllTimeBean allTime;

    public TodayBean getToday()
    {
        return today;
    }

    public void setToday(TodayBean today)
    {
        this.today = today;
    }

    public ThisMonthBean getThisMonth()
    {
        return thisMonth;
    }

    public void setThisMonth(ThisMonthBean thisMonth)
    {
        this.thisMonth = thisMonth;
    }

    public AllTimeBean getAllTime()
    {
        return allTime;
    }

    public void setAllTime(AllTimeBean allTime)
    {
        this.allTime = allTime;
    }

    public static class TodayBean
    {
        /**
         * received : 0
         * receivable : 0
         * no_of_trips : 0
         */

        @SerializedName("received")
        private double received;
        @SerializedName("receivable")
        private double receivable;
        @SerializedName("no_of_trips")
        private String noOfTrips;

        public double getReceived()
        {
            return received;
        }

        public void setReceived(double received)
        {
            this.received = received;
        }

        public double getReceivable()
        {
            return receivable;
        }

        public void setReceivable(double receivable)
        {
            this.receivable = receivable;
        }

        public String getNoOfTrips()
        {
            return noOfTrips;
        }

        public void setNoOfTrips(String noOfTrips)
        {
            this.noOfTrips = noOfTrips;
        }
    }

    public static class ThisMonthBean
    {
        /**
         * received : 1451
         * receivable : 542.5
         * no_of_trips : 4
         */

        @SerializedName("received")
        private double received;
        @SerializedName("receivable")
        private double receivable;
        @SerializedName("no_of_trips")
        private String noOfTrips;

        public double getReceived()
        {
            return received;
        }

        public void setReceived(double received)
        {
            this.received = received;
        }

        public double getReceivable()
        {
            return receivable;
        }

        public void setReceivable(double receivable)
        {
            this.receivable = receivable;
        }

        public String getNoOfTrips()
        {
            return noOfTrips;
        }

        public void setNoOfTrips(String noOfTrips)
        {
            this.noOfTrips = noOfTrips;
        }
    }

    public static class AllTimeBean
    {
        /**
         * received : 4243
         * receivable : 5985.5
         * no_of_trips : 44
         */

        @SerializedName("received")
        private double received;
        @SerializedName("receivable")
        private double receivable;
        @SerializedName("no_of_trips")
        private String noOfTrips;

        public double getReceived()
        {
            return received;
        }

        public void setReceived(double received)
        {
            this.received = received;
        }

        public double getReceivable()
        {
            return receivable;
        }

        public void setReceivable(double receivable)
        {
            this.receivable = receivable;
        }

        public String getNoOfTrips()
        {
            return noOfTrips;
        }

        public void setNoOfTrips(String noOfTrips)
        {
            this.noOfTrips = noOfTrips;
        }
    }
}
