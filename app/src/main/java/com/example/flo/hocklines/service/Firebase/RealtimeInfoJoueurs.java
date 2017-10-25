package com.example.flo.hocklines.service.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.licence.LicenceContentValues;
import com.example.flo.hocklines.service.Firebase.model.InfoJoueur;
import com.example.flo.hocklines.utils.UtilsFunction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Flo on 20/10/2017.
 */

public class RealtimeInfoJoueurs {

    private static HashMap<String,InfoJoueur> listJoueur;
    private static HashMap<String,HashMap<String,InfoJoueur>> listPlayerByEquipe = new HashMap<>();

    //we must link to RealtimeDatabase 1 time for each equip to get notify from here
    public static void contruct(Context context) {
        listJoueur = new HashMap<>();
        if(listPlayerByEquipe.get(MainActivity.equipe) == null)
            getListJoueurFromFirebase(context);
    }

    private static void getListJoueurFromFirebase(final Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("infoJoueur/"+ MainActivity.equipe);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("on search :::::::::::::",":::::::::::::");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<HashMap<String,InfoJoueur>> t = new GenericTypeIndicator<HashMap<String,InfoJoueur>>(){};
                listJoueur = dataSnapshot.getValue(t);
                Log.d("Firebase","result ok !");
                if(listJoueur != null){
                    StoragePathLicence.construct(MainActivity.equipe,listJoueur,context);
                    listPlayerByEquipe.put(MainActivity.equipe,listJoueur);
                    for(String playerName : listJoueur.keySet()){
                        UtilsFunction.updateLicence(playerName, context, new LicenceContentValues().putNomjoueur(playerName)
                                .putEquipe(MainActivity.equipe)
                                .putNumeromaillotblanc(listPlayerByEquipe.get(MainActivity.equipe).get(playerName).getNumeroMaillotBlanc())
                                .putNumeromaillotnoir(listPlayerByEquipe.get(MainActivity.equipe).get(playerName).getNumeroMaillotNoir())
                                .putNumlicence(listPlayerByEquipe.get(MainActivity.equipe).get(playerName).getNumLicence()));
                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
