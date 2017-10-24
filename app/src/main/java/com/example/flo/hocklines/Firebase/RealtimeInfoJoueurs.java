package com.example.flo.hocklines.Firebase;

import android.util.Log;

import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.events.FirebaseRealtimeResultEvent;
import com.example.flo.hocklines.model.InfoJoueur;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Created by Flo on 20/10/2017.
 */

public class RealtimeInfoJoueurs {

    public static HashMap<String,InfoJoueur> listJoueur;

    public static void contruct() {
        listJoueur = new HashMap<>();
        getListJoueurFromFirebase();
    }

    private static void getListJoueurFromFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("infoJoueur/"+ MainActivity.equipe);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<HashMap<String,InfoJoueur>> t = new GenericTypeIndicator<HashMap<String,InfoJoueur>>(){};
                listJoueur = dataSnapshot.getValue(t);
                Log.d("Firebase","result ok !");
                EventBus.getDefault().post(new FirebaseRealtimeResultEvent());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
