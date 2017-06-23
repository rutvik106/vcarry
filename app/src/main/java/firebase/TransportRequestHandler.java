package firebase;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import api.API;
import api.RetrofitCallbacks;
import extra.Log;
import io.fusionbit.vcarry.App;
import io.fusionbit.vcarry.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rutvik on 10/27/2016 at 1:03 PM.
 */

public class TransportRequestHandler
{

    private static final String TAG = App.APP_TAG + TransportRequestHandler.class.getSimpleName();

    private long totalRequest;

    private long requestCount = 0;

    public TransportRequestHandler(final TransportRequestListener transportRequestListener)
    {

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();

        dbRef.child("request").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                totalRequest = dataSnapshot.getChildrenCount();
                dbRef.child("request").addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        requestCount++;
                        if (requestCount > totalRequest)
                        {
                            totalRequest++;
                            transportRequestListener.OnReceiveNewTransportRequest(dataSnapshot);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        transportRequestListener.OnRequestChanged();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {
                        transportRequestListener.OnRequestRemoved();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s)
                    {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    public static void getRequestDetails(final String requestId, final RequestDetailsCallback callback)
    {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();

        dbRef.child("request").child(requestId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                callback.onGetRequestDetails(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                callback.onRequestDetailsNotFound(databaseError);
            }
        });

    }

    //Not Using this function As we are receiving FCM notification

    /**
     * public static void startListeningForTripConfirmation(final String tripId, final ConfirmationListener confirmationListener)
     * {
     * DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
     * dbRef.getRoot();
     * <p>
     * dbRef.child(Constants.FirebaseNames.NODE_ACCEPTED)
     * .child(tripId).addChildEventListener(new ChildEventListener()
     * {
     *
     * @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
     * {
     * <p>
     * }
     * @Override public final void onChildChanged(DataSnapshot dataSnapshot, String s)
     * {
     * if (dataSnapshot.getKey().equals(Constants.FirebaseNames.KEY_CONFIRM))
     * {
     * final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
     * if (dataSnapshot.getValue().equals(userEmail))
     * {
     * confirmationListener.tripConfirmed(tripId);
     * } else
     * {
     * confirmationListener.tripNotConfirmed(tripId);
     * }
     * }
     * }
     * @Override public void onChildRemoved(DataSnapshot dataSnapshot)
     * {
     * <p>
     * }
     * @Override public void onChildMoved(DataSnapshot dataSnapshot, String s)
     * {
     * <p>
     * }
     * @Override public void onCancelled(DatabaseError databaseError)
     * {
     * <p>
     * }
     * });
     * }
     */

    public static void acceptRequest(final Context context, final String requestId, final String latLng, final TripAcceptedCallback tripAcceptedCallback)
    {
        final String acceptedTime = Calendar.getInstance().getTimeInMillis() + "";

        final Map data = new HashMap<>();

        insertTripAcceptedDataUsingApi(context, requestId, latLng, acceptedTime);

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();

        data.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        data.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        data.put("time", acceptedTime);
        data.put("location", latLng);
        data.put("trip_id", requestId);
        data.put("confirm", 0);

        dbRef.child("request").child(requestId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChildren())
                {
                    dbRef.getRoot();
                    dbRef.child("request").child(requestId).removeValue(new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {
                            if (databaseError == null)
                            {
                                dbRef.getRoot();

                                dbRef.child("accepted").child(requestId).updateChildren(data, new DatabaseReference.CompletionListener()
                                {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                    {
                                        if (databaseError == null)
                                        {
                                            tripAcceptedCallback.tripAcceptedSuccessfully(requestId);
                                        } else
                                        {
                                            tripAcceptedCallback.failedToAcceptTrip(requestId, latLng, acceptedTime, databaseError);
                                        }
                                    }
                                });

                            } else
                            {
                                tripAcceptedCallback.failedToAcceptTrip(requestId, latLng, acceptedTime, databaseError);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    public static void setupConnectivityLogic()
    {
        Log.i(TAG, "SETTING UP CONNECTIVITY LOGIC");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //final DatabaseReference lastSeenRef = FirebaseDatabase.getInstance().getReference("/users/" + uid + "/last-seen");
        final DatabaseReference connectivity = FirebaseDatabase.getInstance().getReference("/users/" + uid + "/connectivity");

        //get refrence to firebase .info/connected database
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        //add value listener on server side (will be triggered when user is disconnected/connected)
        connectedRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected)
                {
                    Log.i(TAG, "CONNECTIVITY LOGIC SUCCESSFULLY SET");

                    //if connected to databse set value to "online" and onDisconnect set value to "offline"
                    final Map connectivityMap = new HashMap();
                    connectivityMap.put("email", email);
                    connectivityMap.put("connected", 1);
                    connectivityMap.put("last-connected", ServerValue.TIMESTAMP);
                    connectivity.updateChildren(connectivityMap);

                    // when I disconnect, update the last time I was seen online
                    final Map connectivityMap2 = new HashMap();
                    connectivityMap2.put("email", email);
                    connectivityMap2.put("connected", 0);
                    connectivityMap2.put("last-connected", ServerValue.TIMESTAMP);
                    connectivity.onDisconnect().updateChildren(connectivityMap2);

                }
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.i(TAG, "CONNECTIVITY Listener was cancelled at .info/connected");
            }
        });
    }

    public static void insertTripRejectedDataUsingApi(final Context context, final String tripId)
    {
        final String driverId = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.DRIVER_ID, "");
        API.getInstance().insertTripRejectedData(driverId, tripId,
                new RetrofitCallbacks<ResponseBody>()
                {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            Log.i(TAG, "REJECTED TRIP DATA WAS INSERTED IN DATABASE");
                            try
                            {
                                Log.i(TAG, "RESPONSE: " + response.body().string());
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        super.onFailure(call, t);
                        Log.i(TAG, "FAILED TO INSERTED REJECTED TRIP DATA IN DATABASE");
                    }
                });
    }

    private static void insertTripAcceptedDataUsingApi(final Context context, final String tripId, final String location,
                                                       final String acceptedTime)
    {
        final String driverId = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.DRIVER_ID, "");
        API.getInstance().insertTripAcceptedData(driverId, tripId, location, acceptedTime,
                new RetrofitCallbacks<ResponseBody>()
                {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        super.onResponse(call, response);
                        if (response.isSuccessful())
                        {
                            Log.i(TAG, "ACCEPTED TRIP DATA WAS INSERTED IN DATABASE");
                            try
                            {
                                Log.i(TAG, "RESPONSE: " + response.body().string());
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        super.onFailure(call, t);
                        Log.i(TAG, "FAILED TO INSERTED ACCEPTED TRIP DATA IN DATABASE");
                    }
                });
    }

    public interface TransportRequestListener
    {

        void OnReceiveNewTransportRequest(DataSnapshot dataSnapshot);

        void OnRequestChanged();

        void OnRequestRemoved();

    }


    public interface TripAcceptedCallback
    {
        void tripAcceptedSuccessfully(final String tripId);

        void failedToAcceptTrip(final String tripId, final String location,
                                final String acceptedTime, final DatabaseError databaseError);
    }

    public interface RequestDetailsCallback
    {
        void onGetRequestDetails(DataSnapshot dataSnapshot);

        void onRequestDetailsNotFound(DatabaseError databaseError);
    }

    public interface ConfirmationListener
    {
        void tripConfirmed(String tripId);

        void tripNotConfirmed(String tripId);
    }


}
