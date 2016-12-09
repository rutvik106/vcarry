package api;

import java.util.List;

import apimodels.TripDetails;
import apimodels.TripsByDriverMail;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by rutvik on 11/27/2016 at 3:47 PM.
 */

public interface ApiInterface
{

    @FormUrlEncoded
    @POST("webservice.php")
    Call<List<TripsByDriverMail>> getTripsByDriverEmail(@Field("method") String method,
                                                        @Field("driver_email") String driverEmail);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<TripDetails> getTripDetailsByTripId(@Field("method") String method,
                                             @Field("trip_id") String tripId);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> updateTripStatus(@Field("method") String method,
                                        @Field("status_id") String statusId,
                                        @Field("trip_id") String tripId);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> stopTripAndSendDetails(@Field("method") String method,
                                              @Field("trip_id") String tripId,
                                              @Field("start_time") String starTime,
                                              @Field("stop_time") String stopTime,
                                              @Field("start_loc") String startLoc,
                                              @Field("stop_loc") String stopLoc,
                                              @Field("distance") String distance,
                                              @Field("memo_amt") String memoAmount,
                                              @Field("labour_amt") String labourAmount,
                                              @Field("cash_received") String cashReceived);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> insertTripAcceptedData(@Field("method") String method,
                                              @Field("driver_email") String driverEmail,
                                              @Field("trip_id") String tripId,
                                              @Field("location") String location,
                                              @Field("accepted_time") String acceptedTime);


}
