package firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.fusionbit.vcarry.Constants;

/**
 * Created by rutvik on 10/27/2016 at 1:03 PM.
 */

public class TransportRequestHandler
{

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
                callback.OnGetRequestDetails(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    public static void startListeningForTripConfirmation(final String tripId, final ConfirmationListener confirmationListener)
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();

        dbRef.child(Constants.FirebaseNames.NODE_ACCEPTED)
                .child(tripId).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public final void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.getKey().equals(Constants.FirebaseNames.KEY_CONFIRM))
                {
                    final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    if (dataSnapshot.getValue().equals(userEmail))
                    {
                        confirmationListener.tripConfirmed();
                    } else
                    {
                        confirmationListener.tripNotConfirmed();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

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

    public static void acceptRequest(final String requestId, final String latLng, final TripAcceptedCallback tripAcceptedCallback)
    {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();

        final Map data = new HashMap<>();

        data.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        data.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        data.put("time", Calendar.getInstance().getTimeInMillis());
        data.put("location", latLng);
        data.put("trip_id", requestId);
        data.put("confirm", 0);


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
                                tripAcceptedCallback.failedToAcceptTrip(databaseError);
                            }
                        }
                    });

                }
            }
        });

    }

    public static void rejectRequest()
    {

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

        void failedToAcceptTrip(DatabaseError databaseError);
    }

    public interface RequestDetailsCallback
    {
        void OnGetRequestDetails(DataSnapshot dataSnapshot);
    }

    public interface ConfirmationListener
    {
        void tripConfirmed();

        void tripNotConfirmed();
    }

}
