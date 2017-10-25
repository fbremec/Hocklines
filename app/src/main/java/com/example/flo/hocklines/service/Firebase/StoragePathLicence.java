package com.example.flo.hocklines.service.Firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.flo.hocklines.events.CircleProgressEvent;
import com.example.flo.hocklines.licence.LicenceContentValues;
import com.example.flo.hocklines.service.Firebase.model.InfoJoueur;
import com.example.flo.hocklines.utils.UtilsFunction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Flo on 21/10/2017.
 */

public class StoragePathLicence {

    public static HashMap<String,String> listPathLicence = new HashMap<>();

    public static void construct(String equipe, HashMap<String,InfoJoueur> listJoueur, Context context){
        if(listJoueur != null){
            for(String playerName : listJoueur.keySet()){
                if(!UtilsFunction.isLicenceExistInBDD(playerName,context)){
                    dlLicenceFromFirebaseStorage(playerName,listJoueur.get(playerName),equipe, context);
                }
            }
        }
    }

    //DL licence.png of player 'playerName' in Firebase Storage then when sucess put the path file of Downloaded licence in the BDD and append image to View
    private static void dlLicenceFromFirebaseStorage(final String playerName, final InfoJoueur infoJoueur,final String equipe, final Context context) {
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("licences/" + playerName + ".png");
            final File localFile = File.createTempFile(playerName, ".png");

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("LOG", "SUCCES :: " + localFile.getAbsolutePath());
                    if (context != null) {
                        UtilsFunction.updateLicence(playerName, context, new LicenceContentValues().putNomjoueur(playerName)
                                .putEquipe(equipe)
                                .putPathfile(localFile.getAbsolutePath())
                                .putNumeromaillotblanc(infoJoueur.getNumeroMaillotBlanc())
                                .putNumeromaillotnoir(infoJoueur.getNumeroMaillotNoir())
                                .putNumlicence(infoJoueur.getNumLicence()));
                        EventBus.getDefault().post(new CircleProgressEvent(View.GONE));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("LOG", "FAIL");
                }
            });
        }catch (IOException e){

        }

    }


}
