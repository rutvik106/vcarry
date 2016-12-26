package apimodels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rutvik on 12/24/2016 at 9:11 AM.
 */

public class AccountSummary extends RealmObject
{


    /**
     * received : 300
     * receivable : 320
     */

    @PrimaryKey
    @SerializedName("key")
    private int key;

    @SerializedName("received")
    private int received = 0;
    @SerializedName("receivable")
    private int receivable = 0;

    private int receivedToday = 0, receivableToday = 0, receivedThisMonth = 0, receivableThisMonth = 0,
            totalReceived = 0, totalReceivable = 0;

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
}
