package firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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


    public interface TransportRequestListener
    {

        void OnReceiveNewTransportRequest(DataSnapshot dataSnapshot);

        void OnRequestChanged();

        void OnRequestRemoved();

    }

}
