package com.example.flo.hocklines.licences.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Visibility;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.flo.hocklines.Firebase.RealtimeInfoJoueurs;
import com.example.flo.hocklines.Firebase.StoragePathLicence;
import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.R;
import com.example.flo.hocklines.events.CircleProgressEvent;
import com.example.flo.hocklines.events.FirebaseRealtimeResultEvent;
import com.example.flo.hocklines.events.SearchVisibilityEvent;
import com.example.flo.hocklines.licence.LicenceBean;
import com.example.flo.hocklines.licence.LicenceContentValues;
import com.example.flo.hocklines.licence.LicenceCursor;
import com.example.flo.hocklines.licence.LicenceSelection;
import com.example.flo.hocklines.licences.events.SearchLicenceEvent;
import com.example.flo.hocklines.utils.UtilsFunction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class LicencesFragment extends Fragment {


    private GridLayout gridLayout;


    public LicencesFragment() {
        EventBus.getDefault().register(this);
    }

    /**
     * Returns a new instance of this fragment
     */
    public static LicencesFragment newInstance() {
        return new LicencesFragment();
    }

    //unregister EventBus and set screen orientation portait
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new SearchVisibilityEvent(View.GONE));
        EventBus.getDefault().unregister(this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //DL licence.png of player 'playerName' in Firebase Storage then when sucess put the path file of Downloaded licence in the BDD and append image to View
    private void dlLicenceFromFirebaseStorage(final String playerName) {
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("licences/" + playerName + ".png");
            final File localFile = File.createTempFile(playerName, ".png");

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("LOG", "SUCCES :: " + localFile.getAbsolutePath());
                    if (getContext() != null) {
                        UtilsFunction.updateLicence(playerName, getContext(), new LicenceContentValues().putNomjoueur(playerName)
                                                                                                        .putEquipe(MainActivity.equipe)
                                                                                                        .putPathfile(localFile.getAbsolutePath())
                                                                                                        .putNumeromaillotblanc(RealtimeInfoJoueurs.listJoueur.get(playerName).getNumeroMaillotBlanc())
                                                                                                        .putNumeromaillotnoir(RealtimeInfoJoueurs.listJoueur.get(playerName).getNumeroMaillotNoir())
                                                                                                        .putNumlicence(RealtimeInfoJoueurs.listJoueur.get(playerName).getNumLicence()));

                        appendLicence(constructBean(playerName,RealtimeInfoJoueurs.listJoueur.get(playerName).getNumeroMaillotBlanc()
                                ,RealtimeInfoJoueurs.listJoueur.get(playerName).getNumeroMaillotNoir(),localFile.getAbsolutePath()
                                ,RealtimeInfoJoueurs.listJoueur.get(playerName).getNumLicence()));
                        //Glide.with(getContext()).load(localFile).into(i);
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

    //put licence file png to view
    private void appendLicence(LicenceBean bean){
        if(gridLayout != null){
            StoragePathLicence.listPathLicence.put(bean.getNomjoueur(), bean.getPathfile());
            gridLayout.setRowCount(gridLayout.getRowCount()+3);
            final ImageView i = new ImageView(getContext());
            Glide.with(getContext()).load(new File(bean.getPathfile())).into(i);
            i.setVisibility(View.GONE);
            TextView t = new TextView(getContext());
            t.setText("Nom : "+bean.getNomjoueur()+" \nN°lic : "+bean.getNumlicence()+" \nBlanc : "+bean.getNumeromaillotblanc()+" Noir : "+bean.getNumeromaillotnoir()+"");
            t.setPadding(40,0,0,0);
            t.setLayoutParams(new GridLayout.LayoutParams());
            t.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
            t.setBackgroundColor(getResources().getColor(R.color.red));
            t.setTextSize(20);
            t.setTextColor(Color.WHITE);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TextView t = (TextView)view;
                    if(i.getVisibility() == View.GONE)
                        i.setVisibility(View.VISIBLE);
                    else
                        i.setVisibility(View.GONE);

                }
            });

            View v = new View(getContext());
            v.setLayoutParams(new GridLayout.LayoutParams());
            v.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
            v.getLayoutParams().height = 40;

            gridLayout.addView(t);
            gridLayout.addView(i);
            gridLayout.addView(v);
        }



    }

    // for all result of Firebase Realtime database check
    //      if player exist in BDD
    //          then true getPathFile from BDD, put path in StoragePathLicence and append file to view
    //          then false download licence.png from Firebase Storage
    private void constructLicencesListFromRealtime(){
        gridLayout.removeAllViews();
        if(RealtimeInfoJoueurs.listJoueur != null){
            for(String nomJoueur : RealtimeInfoJoueurs.listJoueur.keySet()){
                if(!UtilsFunction.isLicenceExistInBDD(nomJoueur,getContext())){
                    dlLicenceFromFirebaseStorage(nomJoueur);
                }
            }
        }

    }


    private void constructLicenceFromDatabase(){
        LicenceCursor cursor = UtilsFunction.getAllLicence(getContext());
        do{
            if(cursor.getEquipe().equals(MainActivity.equipe)){
                this.appendLicence(constructBean(cursor.getNomjoueur(),cursor.getNumeromaillotblanc(),cursor.getNumeromaillotnoir(),cursor.getPathfile(),cursor.getNumlicence()));
            }
        }while(cursor.moveToNext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_licences, container, false);
        EventBus.getDefault().post(new SearchVisibilityEvent(View.VISIBLE));
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        gridLayout = (GridLayout)rootView.findViewById(R.id.fragment_licences_gridLayout);
        LicenceCursor cursor = UtilsFunction.getAllLicenceByEquipe(getContext(),MainActivity.equipe);
        if(cursor.moveToFirst()){
            constructLicenceFromDatabase();
        }else{
            if(RealtimeInfoJoueurs.listJoueur != null){
                EventBus.getDefault().post(new CircleProgressEvent(View.VISIBLE));
                this.constructLicencesListFromRealtime();
            }
            else
                EventBus.getDefault().post(new CircleProgressEvent(View.GONE));
        }


        Log.d("LOG","REPRISE");
        return rootView;
    }

    //If result of Realtime become later than onCreate Licence Fragment this event are declenche when result Firebase Realtime finish
    //Send by RealtimeInfoJoueur
    @Subscribe
    public void onFirebaseRealtimeResultEvent(FirebaseRealtimeResultEvent event){
        if(getContext() != null){
            LicenceCursor cursor = UtilsFunction.getAllLicenceByEquipe(getContext(),MainActivity.equipe);
            if(!cursor.moveToNext()){
                if(RealtimeInfoJoueurs.listJoueur != null)
                    this.constructLicencesListFromRealtime();
                else{
                    EventBus.getDefault().post(new CircleProgressEvent(View.GONE));
                    Toast.makeText(getContext(),"Impossible de télécharger les données des joueurs de l'équipe "+MainActivity.equipe.toUpperCase(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Event declanche to search a licence
    //Send by MainActivity
    @Subscribe
    public void onSearchLicenceEvent(SearchLicenceEvent event){
        Log.d("Search::::::::::",event.nomLicence);
        if(gridLayout != null)
            gridLayout.removeAllViews();
        for(String key : StoragePathLicence.listPathLicence.keySet()){
            String keyTest = key.toLowerCase();
            if(keyTest.contains(event.nomLicence) && RealtimeInfoJoueurs.listJoueur.get(key) != null){
                this.appendLicence(constructBean(key,RealtimeInfoJoueurs.listJoueur.get(key).getNumeroMaillotBlanc()
                        ,RealtimeInfoJoueurs.listJoueur.get(key).getNumeroMaillotNoir(),StoragePathLicence.listPathLicence.get(key)
                        ,RealtimeInfoJoueurs.listJoueur.get(key).getNumLicence()));
            }
        }
    }

    private LicenceBean constructBean(String nomJoueur, int numMaillotBlanc, int numMaillotNoir,String pathFile, int numLicence){
        LicenceBean bean = new LicenceBean();
        bean.setNumeromaillotblanc(numMaillotBlanc);
        bean.setNumeromaillotnoir(numMaillotNoir);
        bean.setPathfile(pathFile);
        bean.setNomjoueur(nomJoueur);
        bean.setNumlicence(numLicence);
        return bean;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            this.adaptImageViewToLandscape();
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            this.adaptImageViewToPortrait();
        }

    }

    private void adaptImageViewToPortrait(){
        for(int i = 0; i<gridLayout.getChildCount();i++){
            View v = gridLayout.getChildAt(i);
            if(v instanceof ImageView){
                ImageView image = (ImageView)v;
                image.setLayoutParams(new GridLayout.LayoutParams());
                image.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
                image.getLayoutParams().height = GridLayout.LayoutParams.MATCH_PARENT;

            }

        }
    }

    private void adaptImageViewToLandscape(){
        for(int i = 0; i<gridLayout.getChildCount();i++){
            View v = gridLayout.getChildAt(i);
            if(v instanceof ImageView){
                ImageView image = (ImageView)v;
                image.setLayoutParams(new GridLayout.LayoutParams());
                image.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
                image.getLayoutParams().height = UtilsFunction.getScreenSizeInPixel(getActivity()).y-60;
            }
        }
    }
}
