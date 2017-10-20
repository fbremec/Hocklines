package com.example.flo.hocklines;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flo.hocklines.events.SearchVisibilityEvent;
import com.example.flo.hocklines.hocklines_timer.fragment.HocklinesTimerFragment;
import com.example.flo.hocklines.licences.events.SearchLicenceEvent;
import com.example.flo.hocklines.licences.fragment.LicencesFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int FRAGMENT_TIMER = 0;
    private static final int NO_FRAGMENT = -1;
    private static final int FRAGMENT_LICENCES = 1;

    private HocklinesTimerFragment hocklinesTimerFragment;
    private NavigationView navigationView;
    private int currentFragment;
    private LicencesFragment licencesFragment;
    private ImageView searchImage;
    private EditText searchEdit;

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
        hocklinesTimerFragment = HocklinesTimerFragment.newInstance();
//        licencesFragment = LicencesFragment.newInstance();
        currentFragment = NO_FRAGMENT;
        displayFragment(currentFragment);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(currentFragment == FRAGMENT_TIMER){
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
                        .replace(R.id.container, hocklinesTimerFragment)
                        .commit();
                currentFragment = FRAGMENT_TIMER;
                break;
            case FRAGMENT_LICENCES:
                navigationView.getMenu().getItem(FRAGMENT_LICENCES).setChecked(true);
                Log.d("FRAGMENT_LICENCE",":::::::::");
                setTitle("Licences");
                searchEdit = (EditText)findViewById(R.id.toolbar_editText_searc);
                searchEdit.addTextChangedListener(new SearchEditTextWatcher());
                searchImage = (ImageView)findViewById(R.id.toolbar_imageView_searc);
                searchImage.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, LicencesFragment.newInstance())
                        .commit();
                currentFragment = FRAGMENT_LICENCES;
                break;
            /*case NO_FRAGMENT:
                //test pour premier affichage car current fragment = NO_FRAGMENT
                if(currentFragment !=-1)
                    navigationView.getMenu().getItem(currentFragment).setChecked(false);
                Log.d("NO_FRAGMENT",":::::::::");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(3))
                        .commit();
                currentFragment = NO_FRAGMENT;
                break;*/
        }
    }

    class SearchEditTextWatcher implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EventBus.getDefault().post(new SearchLicenceEvent(s.toString().toLowerCase()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Subscribe
    public void onSearchVisibilityEvent(SearchVisibilityEvent event){
        if(searchEdit != null){
            searchEdit.setVisibility(event.visibility);
            searchImage.setVisibility(event.visibility);
            searchEdit.setText("");
        }

    }

    private void activateSearchEdit(){
        if(searchEdit.getVisibility() == View.GONE){
            Animation slideLeft = AnimationUtils.loadAnimation(MainActivity.this,R.anim.right_to_left);
            searchEdit.setVisibility(View.VISIBLE);
            searchEdit.startAnimation(slideLeft);
            searchEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        else{
            Animation slideRight = AnimationUtils.loadAnimation(MainActivity.this,R.anim.left_to_right);
            searchEdit.startAnimation(slideRight);
            searchEdit.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
        }
    }

    public void toolbarSearch(View v){
        this.activateSearchEdit();
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        Log.d("on click ","::::::::::::::");
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_timer:
                if(currentFragment != FRAGMENT_TIMER)
                    displayFragment(FRAGMENT_TIMER);
                break;
            case R.id.nav_licence:
                if(currentFragment != FRAGMENT_LICENCES)
                    displayFragment(FRAGMENT_LICENCES);
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


//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//    }



    public static final int RESUME_AFTER_SAVE = 1;
    public static final int RESUME = 0;




    /**
     * Traitement Fragment HocklinesTimer
     */

//    @Subscribe
//    public void onIncrementWorkEvent(final IncrementWorkEvent event) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                int exercice = hocklinesTimerFragment.getCurrentExercice();
//                int currentSerie = hocklinesTimerFragment.getCurrentSerie();
//                if ((exercice + 1) % hocklinesTimerFragment.getNbExercice() == 0) {
//                    int serie = hocklinesTimerFragment.getCurrentSerie();
//                    currentSerie = (serie + 1) % hocklinesTimerFragment.getNbSerie();
//                    hocklinesTimerFragment.setCurrentSerie(currentSerie);
//                    hocklinesTimerFragment.getNbSerieMax().setText(hocklinesTimerFragment.getCurrentSerie() + "");
//                }
//                int currentExercice = (exercice + 1) % hocklinesTimerFragment.getNbExercice();
//                hocklinesTimerFragment.setCurrentExercice(currentExercice);
//                hocklinesTimerFragment.getNbExerciceMax().setText(currentExercice + "");
//
//                Log.d("currentExercice",currentExercice+"");
//                Log.d("currentSerie",currentSerie+"");
//            }
//        });
//    }

//    @Subscribe
//    public void onWorkTimerEvent(final WorkTimerEvent event) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                hocklinesTimerFragment.getTypeTimer().setText("Exercice :");
//                hocklinesTimerFragment.getTimerText().setText(event.currentTimer);
//            }
//        });
//    }
//
//    @Subscribe
//    public void onSleepTimerEvent(final SleepTimerEvent event) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                hocklinesTimerFragment.getTypeTimer().setText("Repos :");
//                hocklinesTimerFragment.getTimerText().setText(event.currentTimer);
//            }
//        });
//
//
//    }
//
//    @Subscribe
//    public void onStopSeanceEvent(final StopSeanceEvent event) {
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                hocklinesTimerFragment.getTimer().setSleepTimer(null);
//                hocklinesTimerFragment.getTimer().setWorkTimer(null);
//                hocklinesTimerFragment.setTimer(null);
//                hocklinesTimerFragment.getStop().setEnabled(false);
//                hocklinesTimerFragment.getStart().setEnabled(true);
//                hocklinesTimerFragment.getTimerText().setText("00:00");
//                hocklinesTimerFragment.setCurrentExercice(hocklinesTimerFragment.getCurrentExercice()+1);
//                hocklinesTimerFragment.setCurrentSerie(hocklinesTimerFragment.getCurrentSerie()+1);
//                hocklinesTimerFragment.getNbSerieMax().setText(hocklinesTimerFragment.getNbSerieMaxText().getText());
//                hocklinesTimerFragment.getNbExerciceMax().setText(hocklinesTimerFragment.getNbExerciceMaxText().getText());
//                hocklinesTimerFragment.getNbExerciceMaxText().setVisibility(View.INVISIBLE);
//                hocklinesTimerFragment.getNbSerieMaxText().setVisibility(View.INVISIBLE);
//                hocklinesTimerFragment.getSeparator1().setVisibility(View.INVISIBLE);
//                hocklinesTimerFragment.getSeparator2().setVisibility(View.INVISIBLE);
//                setEnableAllEditText(true);
//            }
//        });
//
//
//    }
//
//    public void setEnableAllEditText(final boolean b){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                hocklinesTimerFragment.getNbExerciceMax().setEnabled(b);
//                hocklinesTimerFragment.getNbSerieMax().setEnabled(b);
//                hocklinesTimerFragment.getWorkTimerMinute().setEnabled(b);
//                hocklinesTimerFragment.getWorkTimerSeconde().setEnabled(b);
//                hocklinesTimerFragment.getSleepTimerMinute().setEnabled(b);
//                hocklinesTimerFragment.getSleepTimerSeconde().setEnabled(b);
//            }
//        });
//    }
}
