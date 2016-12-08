package api;

import java.util.List;

import apimodels.TripDetails;
import apimodels.TripsByDriverMail;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<TripDetails> getTripDetailsByTripId(@Query("method") String method,
                                             @Query("trip_id") String tripId);

    @GET("webservice.php")
    Call<ResponseBody> updateTripStatus(@Query("method") String method,
                                        @Query("status_id") String statusId,
                                        @Query("trip_id") String tripId);

    @POST("webservice.php")
    Call<ResponseBody> stopTripAndSendDetails(@Query("method") String method,
                                              @Query("trip_id") String tripId,
                                              @Query("start_time") String starTime,
                                              @Query("stop_time") String stopTime,
                                              @Query("start_loc") String startLoc,
                                              @Query("stop_loc") String stopLoc,
                                              @Query("distance") String distance,
                                              @Query("memo_amt") String memoAmount,
                                              @Query("labour_amt") String labourAmount,
                                              @Query("cash_received") String cashReceived);

}
