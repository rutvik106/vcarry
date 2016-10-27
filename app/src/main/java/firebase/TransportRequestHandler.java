package firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rutvik on 10/27/2016 at 1:03 PM.
 */

public class TransportRequestHandler
{

    public TransportRequestHandler(final TransportRequestListener transportRequestListener)
    {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.getRoot();
        dbRef.child("request").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                transportRequestListener.OnReceiveNewTransportRequest();
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


    public interface TransportRequestListener
    {

        void OnReceiveNewTransportRequest();

        void OnRequestChanged();

        void OnRequestRemoved();

    }

}
