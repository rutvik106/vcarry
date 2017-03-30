package api;

import java.util.Calendar;
import java.util.List;

import apimodels.AccountSummary;
import apimodels.DriverDetails;
import apimodels.TripDetails;
import apimodels.TripsByDriverMail;
import io.fusionbit.vcarry.App;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by rutvik on 11/27/2016 at 3:43 PM at 7:13 PM.
 */

public class API
{

    private static final String TAG = App.APP_TAG + API.class.getSimpleName();
    //create an object of SingleObject
    private static API instance = new API();
    private ApiInterface apiService;
    private String authToken = "";

    private API()
    {
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    //Get the only object available
    public static API getInstance()
    {
        return instance;
    }

    public void getTripsByDriverMail(final String driverMail,
                                     final RetrofitCallbacks<List<TripsByDriverMail>> callback)
    {
        Call<List<TripsByDriverMail>> call = apiService.getTripsByDriverEmail("get_trips_by_driver_email",
                driverMail);

        call.enqueue(callback);
    }

    public void getTripDetailsByTripId(final String tripId,
                                       final RetrofitCallbacks<TripDetails> callback)
    {
        Call<TripDetails> call = apiService.getTripDetailsByTripId("get_trip_details_by_trip_id",
                tripId);

        call.enqueue(callback);
    }

    public void updateTripStatus(final String statusId, final String tripId,
                                 final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.updateTripStatus("edit_trip_status", statusId, tripId);

        call.enqueue(callback);
    }

    public Call<ResponseBody> stopTripAndSendDetails(final String tripId, final String startTime, final String stopTime,
                                                     final String startLoc, final String stopLoc, final String distance,
                                                     final String memoAmount, final String labourAmount, final String cashReceived,
                                                     final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.stopTripAndSendDetails("insert_trip_stop_data",
                tripId, startTime, stopTime, startLoc, stopLoc, distance, memoAmount, labourAmount, cashReceived);

        call.enqueue(callback);

        return call;
    }

    public void insertTripAcceptedData(final String driverEmail, final String tripId,
                                       final String location, final String acceptedTime,
                                       final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.insertTripAcceptedData("insert_trip_accepted_data",
                driverEmail, tripId, location, acceptedTime);

        call.enqueue(callback);
    }

    public void insertTripRejectedData(final String driverEmail, final String tripId,
                                       final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.insertTripRejectedData("insert_trip_rejected_data",
                driverEmail, tripId, "0,0", Calendar.getInstance().getTimeInMillis() + "");

        call.enqueue(callback);
    }

    public void getTripsByTripStatus(final String tripStatus, final String driverEmail,
                                     final String customerId, final String fromDate,
                                     final String toDate,
                                     final String unActionedByEmail,
                                     final RetrofitCallbacks<List<TripDetails>> callback)
    {
        Call<List<TripDetails>> call = apiService.getTripsByTripStatus("get_trips_by_trip_status", tripStatus,
                driverEmail, customerId, fromDate, toDate, unActionedByEmail);

        call.enqueue(callback);
    }

    public void getAccountSummary(final String email,
                                  final String fromDate,
                                  final String toDate, final RetrofitCallbacks<AccountSummary> callback)
    {
        Call<AccountSummary> call = apiService.getAccountSummary("get_driver_balance",
                fromDate, toDate, email);

        call.enqueue(callback);
    }

    public void getTripSummary(final String email,
                               final String tripStatus,
                               final String fromDate,
                               final String toDate,
                               final String unActionedByEmail,
                               final RetrofitCallbacks<List<TripsByDriverMail>> callback)
    {
        Call<List<TripsByDriverMail>> call = apiService.getTripSummary("get_trips_by_trip_status",
                email, tripStatus, fromDate, toDate, unActionedByEmail);

        call.enqueue(callback);
    }

    public void getAcceptedRejectedStatus(final String email,
                                          final String tripId,
                                          final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.getAcceptedRejectedStatus("getAcceptedRejectedStatus",
                email, tripId);

        call.enqueue(callback);
    }

    public Call<ResponseBody> updateDeviceTokenDriver(final String driverEmail,
                                                      final String deviceToken,
                                                      RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call =
                apiService.updateDeviceTokenDriver("update_device_token_driver",
                        driverEmail, deviceToken);

        call.enqueue(callback);

        return call;
    }

    public Call<Integer> getDriverIdByDriverEmail(final String driverEmail,
                                                  RetrofitCallbacks<Integer> callback)
    {
        Call<Integer> call =
                apiService.getDriverIdByDriverEmail("get_driver_id_by_driver_email",
                        driverEmail);

        call.enqueue(callback);

        return call;
    }

    public Call<ResponseBody> updateShippingLocationLatLng(final String shippingLocationId,
                                                           final String latLng,
                                                           RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call =
                apiService.updateShippingLocationLatLng("update_lat_long_shipping_location",
                        shippingLocationId, latLng);

        call.enqueue(callback);

        return call;
    }

    public Call<DriverDetails> getDriverDetailsByDriverId(final String driverId,
                                                          final RetrofitCallbacks<DriverDetails> callback)
    {
        Call<DriverDetails> call =
                apiService.getDriverDetailsByDriverId("get_driver_details_by_driver_id",
                        driverId);

        call.enqueue(callback);

        return call;
    }

    public Call<TripDetails> getTripDetailsByTripNo(final String tripNo,
                                                    final RetrofitCallbacks<TripDetails> callback)
    {
        Call<TripDetails> call =
                apiService.getTripDetailsByTripNo("get_trip_details_by_trip_no",
                        tripNo);

        call.enqueue(callback);

        return call;
    }

}
