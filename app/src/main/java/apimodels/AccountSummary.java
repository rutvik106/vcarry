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
    private int received;
    @SerializedName("receivable")
    private int receivable;

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
}
