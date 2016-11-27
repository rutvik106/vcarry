package api;

/**
 * Created by rutvik on 11/27/2016 at 3:46 PM.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.fusionbit.vcarry.Constants.API_BASE_URL;


/**
 * Created by rutvik on 11/17/2016 at 3:52 PM.
 */

public class ApiClient
{

    private static Retrofit retrofit = null;

    public static Retrofit getClient()
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}

