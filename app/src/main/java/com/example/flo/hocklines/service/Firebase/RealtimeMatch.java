package com.example.flo.hocklines.service.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.match.MatchContentValues;
import com.example.flo.hocklines.service.Firebase.model.MatchInfo;
import com.example.flo.hocklines.utils.UtilsFunction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

/**
 * Created by Flo on 27/10/2017.
 */

public class RealtimeMatch {

    private static HashMap<String,MatchInfo> listMatch;
    private static HashMap<String, HashMap<String,MatchInfo>> listMatchByEquipe = new HashMap<String, HashMap<String,MatchInfo>>();

    //we must link to RealtimeDatabase 1 time for each equip to get notify from here
    public static void contruct(Context context) {
        Log.d("RealtimeMatch","je suis la");
        listMatch = new HashMap<>();
        if(listMatchByEquipe.get(MainActivity.equipe) == null)
            getListMatchFromFirebase(context);
    }

    private static void getListMatchFromFirebase(final Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("resultMatch/"+ MainActivity.equipe);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("on search :::::::::::::",":::::::::::::");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<HashMap<String,MatchInfo>> t = new GenericTypeIndicator<HashMap<String,MatchInfo>>(){};
                listMatch = dataSnapshot.getValue(t);
                if(listMatch != null){
                    listMatchByEquipe.put(MainActivity.equipe,listMatch);
                    for(String idDateHeure : listMatch.keySet()){
                        MatchInfo m = listMatch.get(idDateHeure);
                        UtilsFunction.updateMatch(idDateHeure,idDateHeure,context,new MatchContentValues().putAdversaire(m.getAdversaire())
                                .putDate(idDateHeure).putHeure(idDateHeure).putEquipe(MainActivity.equipe)
                                .putMyscore(m.getMyScore()).putScoreadversaire(m.getScoreAdverse()));
                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
