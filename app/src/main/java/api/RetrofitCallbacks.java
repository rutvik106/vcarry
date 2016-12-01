package api;

/**
 * Created by rutvik on 11/27/2016 at 3:44 PM.
 */


import android.support.annotation.CallSuper;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rutvik on 11/21/2016 at 11:45 AM.
 */

public class RetrofitCallbacks<T> implements Callback<T>
{
    @Override
    @CallSuper
    public void onResponse(Call<T> call, Response<T> response)
    {
        System.out.println("RETROFIT CALLBACKS ON RESPONSE");
        System.out.println("RESPONSE CODE:" + response.code());

        if (response.code() != HttpURLConnection.HTTP_OK)
        {
            System.out.println(response.errorBody().toString());
        } else if (response.code() == HttpURLConnection.HTTP_OK)
        {
            //System.out.println(response.body().toString());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        System.out.println("RETROFIT CALLBACKS ON FAILURE");
        System.out.println(t.toString());
    }
}

