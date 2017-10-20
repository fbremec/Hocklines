package com.example.flo.hocklines.licences.fragment;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.Visibility;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.R;
import com.example.flo.hocklines.events.SearchVisibilityEvent;
import com.example.flo.hocklines.licence.LicenceContentValues;
import com.example.flo.hocklines.licence.LicenceCursor;
import com.example.flo.hocklines.licence.LicenceSelection;
import com.example.flo.hocklines.licences.events.NameOfLicenceEvent;
import com.example.flo.hocklines.licences.events.NotExistLicencie;
import com.example.flo.hocklines.utils.UtilsFunction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LicencesFragment extends Fragment {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<String> nameOfLicences;
    private ArrayList<File> fileOfLicences = new ArrayList<>();

    public LicencesFragment() {
        EventBus.getDefault().register(this);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LicencesFragment newInstance() {
        return new LicencesFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new SearchVisibilityEvent(View.GONE));
        EventBus.getDefault().unregister(this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Subscribe
    public void onNameOfLicenceEvent(NameOfLicenceEvent event) {
        checkIfLicencieExist();
    }

    @Subscribe
    public void onNotExistLicencie(NotExistLicencie event) {
        Log.d("LOG","NOT EXIST");
        try {
            this.dlLicenceFromFirebaseStorage(event.identifiant);
            Log.d("LOG","REPRISE V2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNameOfLicencieFromFirebase(){
        nameOfLicences = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("licences");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                List<String> value = dataSnapshot.getValue(t);
                nameOfLicences.addAll(value);
                EventBus.getDefault().post(new NameOfLicenceEvent());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("LOG","END READ");
    }

    private void dlLicenceFromFirebaseStorage(final String nameOfFile) throws IOException {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child(nameOfFile+".png");
        final File localFile = File.createTempFile(nameOfFile, ".png");

        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d("LOG","SUCCES :: "+localFile.getAbsolutePath());
                fileOfLicences.add(localFile);
                if(getContext() != null) {
                    UtilsFunction.updateLicence(nameOfFile, getContext(), new LicenceContentValues().putNomjoueur(nameOfFile).putPathfile(localFile.getAbsolutePath()));

                    //Glide.with(getContext()).load(localFile).into(i);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("LOG","FAIL");
            }
        });

    }

    private void getLicenceFromBDD(){
        LicenceCursor licence = UtilsFunction.getAllLicence(getContext());
        while(licence.moveToNext()){
            File f = new File(licence.getPathfile());
            fileOfLicences.add(f);
        }
    }

    private void appendAllLicence(){
        for(File f : fileOfLicences){
            Log.d("LOG",f.getAbsolutePath());
            ImageView i = new ImageView(getContext());
            Glide.with(getContext()).load(f).into(i);
            gridLayout.addView(i);
        }

    }

    private void checkIfLicencieExist(){
        for(String nameOfLicencie : nameOfLicences){
            LicenceSelection where = UtilsFunction.checkIfLicenceExist(nameOfLicencie,getContext());
            if(where == null) {
                EventBus.getDefault().post(new NotExistLicencie(nameOfLicencie));

            }
        }
    }

    private GridLayout gridLayout;
    private View v;
    private ImageView searcImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_licences, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


        gridLayout = (GridLayout)rootView.findViewById(R.id.fragment_licences_gridLayout);
        gridLayout.removeAllViews();
        this.getLicenceFromBDD();
        gridLayout.setRowCount(fileOfLicences.size());
        this.appendAllLicence();

        getNameOfLicencieFromFirebase();
        Log.d("LOG","REPRISE");
        return rootView;
    }
}
