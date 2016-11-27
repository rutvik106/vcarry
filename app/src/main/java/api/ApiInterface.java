package api;

import java.util.List;

import apimodels.TripsByDriverMail;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rutvik on 11/27/2016 at 3:47 PM.
 */

public interface ApiInterface
{

    @GET("webservice.php")
    Call<List<TripsByDriverMail>> getTripsByDriverEmail(@Query("method") String method,
                                                        @Query("driver_email") String driverEmail);

    @GET("webservice.php")
    Call<ResponseBody> getTripDetailsByTripId(@Query("method") String method,
                                              @Query("trip_id") String tripId);

}
