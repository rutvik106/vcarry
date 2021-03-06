package api;

import java.util.List;

import apimodels.AccountSummary;
import apimodels.AccountSummaryNew;
import apimodels.DriverDetails;
import apimodels.LocationUpdateResponse;
import apimodels.TripBreakUpDetails;
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
                                                        @Field("page_no") int pageNo,
                                                        @Field("driver_id") String driverEmail);

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
                                              @Field("driver_id") String driverEmail,
                                              @Field("trip_id") String tripId,
                                              @Field("location") String location,
                                              @Field("accepted_time") String acceptedTime);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<List<TripDetails>> getTripsByTripStatus(@Field("method") String method,
                                                 @Field("page_no") int pageNo,
                                                 @Field("trip_status") String tripStatus,
                                                 @Field("driver_id") String driverEmail,
                                                 @Field("customer_id") String customerId,
                                                 @Field("from_date") String fromDate,
                                                 @Field("to_date") String toDate,
                                                 @Field("unactioned_driver_id") String unActionedByEmail);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<AccountSummary> getAccountSummary(@Field("method") String method,
                                           @Field("from") String fromDate,
                                           @Field("to") String toDate,
                                           @Field("driver_id") String email);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<List<TripsByDriverMail>> getTripSummary(@Field("method") String method,
                                                 @Field("driver_id") String email,
                                                 @Field("trip_status") String tripStatus,
                                                 @Field("from_date") String fromDate,
                                                 @Field("to_date") String toDate,
                                                 @Field("unactioned_driver_email") String unActionedByEmail);


    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> insertTripRejectedData(@Field("method") String method,
                                              @Field("driver_id") String driverEmail,
                                              @Field("trip_id") String tripId,
                                              @Field("location") String location,
                                              @Field("accepted_time") String acceptedTime);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> getAcceptedRejectedStatus(@Field("method") String method,
                                                 @Field("driver_id") String driverEmail,
                                                 @Field("trip_id") String tripId);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> updateDeviceTokenDriver(@Field("method") String method,
                                               @Field("driver_id") String driverEmail,
                                               @Field("device_token") String deviceToken);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<Integer> getDriverIdByDriverEmail(@Field("method") String method,
                                           @Field("contact_no") String driverEmail);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> updateShippingLocationLatLng(@Field("method") String method,
                                                    @Field("shipping_location_id") String shippingLocationId,
                                                    @Field("lat_long") String latLng);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<DriverDetails> getDriverDetailsByDriverId(@Field("method") String method,
                                                   @Field("driver_id") String driverId);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<TripDetails> getTripDetailsByTripNo(@Field("method") String method,
                                             @Field("trip_no") String tripNo);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<List<String>> getTripNumberLike(@Field("method") String method,
                                         @Field("driver_id") String driverId,
                                         @Field("customer_id") String customerId,
                                         @Field("no") String tripNo);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> updateDriverLatLng(@Field("method") String method,
                                          @Field("driver_id") String driverId,
                                          @Field("lat_long") String tripNo);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<ResponseBody> updateDriverImage(@Field("method") String method,
                                         @Field("driver_id") String driverId,
                                         @Field("image_url") String imageUrl);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<AccountSummaryNew> getAccountSummary(@Field("method") String method,
                                              @Field("driver_id") String email);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<List<TripBreakUpDetails>> getTripBreakUpDetails(@Field("method") String method,
                                                         @Field("trip_id") String tripId);

    @FormUrlEncoded
    @POST("webservice.php")
    Call<LocationUpdateResponse> updateLocation(@Field("method") String method,
                                                @Field("driver_id") String driverId,
                                                @Field("current_location") String locationLatLng);

}
