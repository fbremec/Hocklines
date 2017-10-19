package com.example.flo.hocklines;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.file_descriptor.FileDescriptorModelLoader;
import com.example.flo.hocklines.hocklines_timer.fragment.HocklinesTimerFragment;
import com.example.flo.hocklines.hocklines_timer.events.IncrementWorkEvent;
import com.example.flo.hocklines.hocklines_timer.events.SleepTimerEvent;
import com.example.flo.hocklines.hocklines_timer.events.StopSeanceEvent;
import com.example.flo.hocklines.hocklines_timer.events.WorkTimerEvent;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int FRAGMENT_TIMER = 0;
    private static final int NO_FRAGMENT = -1;

    private HocklinesTimerFragment hocklinesTimerFragment;
    private NavigationView navigationView;
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        EventBus.getDefault().register(this);
        hocklinesTimerFragment = new HocklinesTimerFragment();
        currentFragment = NO_FRAGMENT;
        displayFragment(currentFragment);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(currentFragment != NO_FRAGMENT){
            Log.d("on back press",":::::::::");
            saveBeforeBackPress();
            displayFragment(NO_FRAGMENT);
        }else{
            super.onBackPressed();
        }
    }

    private void saveBeforeBackPress(){
        //Save for fragment timer if timer is start stop seance before display other fragment
        if(hocklinesTimerFragment.seanceIsStarting())
            hocklinesTimerFragment.getTimer().stopSeance();
    }


    private void displayFragment(int fragment){
        switch (fragment){
            case FRAGMENT_TIMER:
                navigationView.getMenu().getItem(FRAGMENT_TIMER).setChecked(true);
                Log.d("FRAGMENT_TIMER",":::::::::");
                setTitle("Hocklines Timer");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, hocklinesTimerFragment.newInstance(3))
                        .commit();
                currentFragment = FRAGMENT_TIMER;
                break;
            case NO_FRAGMENT:
                //test pour premier affichage car current fragment = NO_FRAGMENT
                if(currentFragment !=-1)
                    navigationView.getMenu().getItem(currentFragment).setChecked(false);
                Log.d("NO_FRAGMENT",":::::::::");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(3))
                        .commit();
                currentFragment = NO_FRAGMENT;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("on click ","::::::::::::::");
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_timer:
                displayFragment(FRAGMENT_TIMER);
                break;
            case R.id.nav_share:
                displayFragment(NO_FRAGMENT);
                break;
            case R.id.nav_send:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ArrayList<String> nameOfLicences;
        private ArrayList<File> fileOfLicences = new ArrayList<>();

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private void readNameOfLicences(){
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
                    Log.d("LOG",value.get(0));
                    displayLicences();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.d("LOG","END READ");
        }

        private void dlLicence(String nameOfFile) throws IOException {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child(nameOfFile+".png");
            final File localFile = File.createTempFile(nameOfFile, ".png");

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("LOG","SUCCES :: "+localFile.getAbsolutePath());
                    fileOfLicences.add(localFile);
                    Glide.with(getContext()).load(localFile).into(i);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("LOG","FAIL");
                }
            });

        }

        private void displayLicences(){
            for(String nameOfFile : nameOfLicences){
                File f = new File(nameOfFile);
                if(f.exists()) {
                    Log.d("LOG","EXIST");
                    fileOfLicences.add(f);
                    Glide.with(getContext()).load(f).into(i);
                }else{
                    Log.d("LOG","NOT EXIST");
                    try {
                        this.dlLicence(nameOfFile);
                        Log.d("LOG","REPRISE V2");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ImageView i;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.i = rootView.findViewById(R.id.image);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readNameOfLicences();
                    Log.d("LOG","REPRISE");

                }
            }).start();



//            final Context c = getContext();
//            final ArrayList<String> list = new ArrayList<>();
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            final DatabaseReference myRef = database.getReference("licences");
//            myRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // This method is called once with the initial value and again
//                    // whenever data at this location is updated.
//                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
//                    List<String> value = dataSnapshot.getValue(t);
//                    final Context c = getContext();
//                    Log.d("TAG", "Value is: " + value);
//                    final ArrayList<String> list = new ArrayList<>();
//                    for(String file : value) {
//                        File f = new File(file);
//                        if(f.exists())
//                            Glide.with(c).load(f).into(i);
//                        else{
//                            try {
//                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                                StorageReference pathReference = storageRef.child(file+".png");
//
//                                final File localFile = File.createTempFile(file, ".png");
//                                pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                        // Local temp file has been created
//                                        Log.d("IMAGE","SUCCES :: "+localFile.getAbsolutePath());
//
//                                        list.add(localFile.getAbsolutePath());
//                                        myRef.setValue(list);
//                                        Glide.with(c).load(localFile).into(i);
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception exception) {
//                                        // Handle any errors
//                                        Log.d("IMAGE","FAIL");
//                                    }
//                                });
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Log.w("TAG", "Failed to read value.", error.toException());
//                }
//            });

//            /*File f = new File("/data/user/0/com.example.flo.hocklines/cache/test156392918.png");
//            if(f.exists())
//                Glide.with(this).load(f).into(i);
//            else {*/
//                Log.d("TAG","NOT EXIST");
//                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                StorageReference pathReference = storageRef.child("licences/");
//
//
////            Glide.with(this /* context */)
////                    .using(new FirebaseImageLoader())
////                    .load(pathReference)
////                    .into(i);
//                try {
//                    final Context c = getContext();
//                    final File localFile = File.createTempFile("test", ".png");
//                    pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            // Local temp file has been created
//                            Log.d("IMAGE","SUCCES :: "+localFile.getAbsolutePath());
//                            Glide.with(c).load(localFile).into(i);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                            Log.d("IMAGE","FAIL");
//                        }
//                    });
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            //}






            return rootView;
        }
    }



    public static final int RESUME_AFTER_SAVE = 1;
    public static final int RESUME = 0;




    /**
     * Traitement Fragment HocklinesTimer
     */

    @Subscribe
    public void onIncrementWorkEvent(final IncrementWorkEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int exercice = hocklinesTimerFragment.getCurrentExercice();
                int currentSerie = hocklinesTimerFragment.getCurrentSerie();
                if ((exercice + 1) % hocklinesTimerFragment.getNbExercice() == 0) {
                    int serie = hocklinesTimerFragment.getCurrentSerie();
                    currentSerie = (serie + 1) % hocklinesTimerFragment.getNbSerie();
                    hocklinesTimerFragment.setCurrentSerie(currentSerie);
                    hocklinesTimerFragment.getNbSerieMax().setText(hocklinesTimerFragment.getCurrentSerie() + "");
                }
                int currentExercice = (exercice + 1) % hocklinesTimerFragment.getNbExercice();
                hocklinesTimerFragment.setCurrentExercice(currentExercice);
                hocklinesTimerFragment.getNbExerciceMax().setText(currentExercice + "");

                Log.d("currentExercice",currentExercice+"");
                Log.d("currentSerie",currentSerie+"");
            }
        });

    }

    @Subscribe
    public void onWorkTimerEvent(final WorkTimerEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hocklinesTimerFragment.getTypeTimer().setText("Exercice :");
                hocklinesTimerFragment.getTimerText().setText(event.currentTimer);
            }
        });
    }

    @Subscribe
    public void onSleepTimerEvent(final SleepTimerEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hocklinesTimerFragment.getTypeTimer().setText("Repos :");
                hocklinesTimerFragment.getTimerText().setText(event.currentTimer);
            }
        });


    }

    @Subscribe
    public void onStopSeanceEvent(final StopSeanceEvent event) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hocklinesTimerFragment.getTimer().setSleepTimer(null);
                hocklinesTimerFragment.getTimer().setWorkTimer(null);
                hocklinesTimerFragment.setTimer(null);
                hocklinesTimerFragment.getStop().setEnabled(false);
                hocklinesTimerFragment.getStart().setEnabled(true);
                hocklinesTimerFragment.getTimerText().setText("00:00");
                hocklinesTimerFragment.setCurrentExercice(hocklinesTimerFragment.getCurrentExercice()+1);
                hocklinesTimerFragment.setCurrentSerie(hocklinesTimerFragment.getCurrentSerie()+1);
                hocklinesTimerFragment.getNbSerieMax().setText(hocklinesTimerFragment.getNbSerieMaxText().getText());
                hocklinesTimerFragment.getNbExerciceMax().setText(hocklinesTimerFragment.getNbExerciceMaxText().getText());
                hocklinesTimerFragment.getNbExerciceMaxText().setVisibility(View.INVISIBLE);
                hocklinesTimerFragment.getNbSerieMaxText().setVisibility(View.INVISIBLE);
                hocklinesTimerFragment.getSeparator1().setVisibility(View.INVISIBLE);
                hocklinesTimerFragment.getSeparator2().setVisibility(View.INVISIBLE);
                setEnableAllEditText(true);
            }
        });


    }

    public void setEnableAllEditText(final boolean b){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hocklinesTimerFragment.getNbExerciceMax().setEnabled(b);
                hocklinesTimerFragment.getNbSerieMax().setEnabled(b);
                hocklinesTimerFragment.getWorkTimerMinute().setEnabled(b);
                hocklinesTimerFragment.getWorkTimerSeconde().setEnabled(b);
                hocklinesTimerFragment.getSleepTimerMinute().setEnabled(b);
                hocklinesTimerFragment.getSleepTimerSeconde().setEnabled(b);
            }
        });
    }
}
