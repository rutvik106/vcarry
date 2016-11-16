package firebase;

import android.app.NotificationManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import extra.Log;
import io.fusionbit.vcarry.App;

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

    public static void acceptRequest(final String requestId)
    {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();

        Map data = new HashMap<>();
        data.put("name", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        dbRef.child("accepted").child(requestId).updateChildren(data, new DatabaseReference.CompletionListener()
        {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
                if (databaseError == null)
                {
                    Log.i(App.APP_TAG, "REQUEST ACCEPTED");

                    dbRef.getRoot();

                    dbRef.child("request").child(requestId).removeValue();

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

}
