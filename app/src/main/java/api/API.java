package api;

import java.util.List;

import apimodels.TripDetails;
import apimodels.TripsByDriverMail;
import io.fusionbit.vcarry.App;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by rutvik on 11/27/2016 at 3:43 PM.
 */

public class API
{

    private ApiInterface apiService;

    //create an object of SingleObject
    private static API instance = new API();

    private static final String TAG = App.APP_TAG + API.class.getSimpleName();

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

    public void stopTripAndSendDetails(final String tripId, final String startTime, final String stopTime,
                                       final String startLoc, final String stopLoc, final String distance,
                                       final String memoAmount, final String labourAmount, final String cashReceived,
                                       final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.stopTripAndSendDetails("insert_trip_stop_data",
                tripId, startTime, stopTime, startLoc, stopLoc, distance, memoAmount, labourAmount, cashReceived);

        call.enqueue(callback);
    }

    public void insertTripAcceptedData(final String driverEmail, final String tripId,
                                       final String location, final String acceptedTime,
                                       final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.insertTripAcceptedData("insert_trip_accepted_data",
                driverEmail, tripId, location, acceptedTime);

        call.enqueue(callback);
    }

    public void getTripsByTripStatus(final String tripStatus, final String driverEmail,
                                     final String customerId, final String fromDate,
                                     final String toDate,
                                     final RetrofitCallbacks<List<TripDetails>> callback)
    {
        Call<List<TripDetails>> call = apiService.getTripsByTripStatus("get_trips_by_trip_status", tripStatus,
                driverEmail, customerId, fromDate, toDate);

        call.enqueue(callback);
    }

}
