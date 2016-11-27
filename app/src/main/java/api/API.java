package api;

import java.util.List;

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
                                       final RetrofitCallbacks<ResponseBody> callback)
    {
        Call<ResponseBody> call = apiService.getTripDetailsByTripId("get_trip_details_by_trip_id",
                tripId);

        call.enqueue(callback);
    }

}
