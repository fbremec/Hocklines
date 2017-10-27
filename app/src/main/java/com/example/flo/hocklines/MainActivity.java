package com.example.flo.hocklines;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.flo.hocklines.events.CircleProgressEvent;
import com.example.flo.hocklines.events.ConnectEvent;
import com.example.flo.hocklines.events.DisconnectEvent;
import com.example.flo.hocklines.events.SearchVisibilityEvent;
import com.example.flo.hocklines.hocklines_timer.fragment.HocklinesTimerFragment;
import com.example.flo.hocklines.licences.events.SearchLicenceEvent;
import com.example.flo.hocklines.licences.fragment.LicencesFragment;
import com.example.flo.hocklines.match.fragment.MatchFragment;
import com.example.flo.hocklines.service.Firebase.RealtimeInfoJoueurs;
import com.example.flo.hocklines.service.LicenceService;
import com.example.flo.hocklines.service.MatchService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static boolean isConnect = false;

    public static String equipe = "N3";
    public static final int FRAGMENT_TIMER = 0;
    private static final int FRAGMENT_MAIN = -1;
    private static final int FRAGMENT_LICENCES = 1;
    private static final int FRAGMENT_RESULTAT = 2;

    private HocklinesTimerFragment hocklinesTimerFragment;
    private NavigationView navigationView;
    private int currentFragment;
    private LicencesFragment licencesFragment;
    private ImageView searchImage;
    private EditText searchEdit;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private MenuItem connectItem;

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

//        Intent intent = new Intent(this, LicenceService.class);
//        startService(intent);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        EventBus.getDefault().register(this);
        hocklinesTimerFragment = HocklinesTimerFragment.newInstance();
        currentFragment = FRAGMENT_MAIN;
        displayFragment(currentFragment);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);

                }

            });

            signIn();

        }




    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();

    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN ) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if(signedIn){
            isConnect = true;
            Toast.makeText(getApplicationContext(),"Connecté",Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new ConnectEvent());
//            connectItem.setTitle("se déconnecter");

        }
        else {
            isConnect = false;
            Toast.makeText(getApplicationContext(), "Déconnecté", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new DisconnectEvent());
//            connectItem.setTitle("se connecter");

        }

    }



    @Subscribe
    public void onCircleProgressEvent(CircleProgressEvent event){
        ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar);
        progress.setVisibility(event.visibility);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(currentFragment == FRAGMENT_TIMER) {
            Log.d("on back press", ":::::::::");
            saveBeforeBackPress();
            displayFragment(FRAGMENT_MAIN);
        }else if(currentFragment == FRAGMENT_LICENCES) {
            displayFragment(FRAGMENT_MAIN);
        }else if(currentFragment == FRAGMENT_RESULTAT){
            displayFragment(FRAGMENT_MAIN);
        }else if(currentFragment == FRAGMENT_MAIN){
            super.onBackPressed();
        }
    }

    private void saveBeforeBackPress(){
        //Save for fragment timer if timer is start stop seance before display other fragment
        if(hocklinesTimerFragment.seanceIsStarting())
            hocklinesTimerFragment.getTimer().stopSeance();
    }


    private void displayFragment(int fragment){
        EventBus.getDefault().post(new CircleProgressEvent(View.GONE));
        switch (fragment){
            case FRAGMENT_TIMER:
                navigationView.getMenu().getItem(FRAGMENT_TIMER).setChecked(true);
                setTitle("Hocklines Timer " + equipe);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, hocklinesTimerFragment)
                        .commit();
                currentFragment = FRAGMENT_TIMER;
                break;
            case FRAGMENT_LICENCES:
                navigationView.getMenu().getItem(FRAGMENT_LICENCES).setChecked(true);
                setTitle("Licences " + equipe);
                searchEdit = (EditText)findViewById(R.id.toolbar_editText_searc);
                searchEdit.addTextChangedListener(new SearchEditTextWatcher());
                searchImage = (ImageView)findViewById(R.id.toolbar_imageView_searc);
                searchImage.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, LicencesFragment.newInstance())
                        .commit();
                currentFragment = FRAGMENT_LICENCES;
                break;
            case FRAGMENT_RESULTAT :
                //navigationView.getMenu().getItem(FRAGMENT_RESULTAT).setChecked(true);
                setTitle("Resultat " + equipe);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MatchFragment.newInstance("",""))
                        .commit();
                currentFragment = FRAGMENT_RESULTAT;
                break;

            case FRAGMENT_MAIN:
                //test pour premier affichage car current fragment = NO_FRAGMENT
                if(currentFragment !=-1)
                    navigationView.getMenu().getItem(currentFragment).setChecked(false);
                Log.d("NO_FRAGMENT",":::::::::");
                setTitle("Hocklines " + equipe);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainFragment.newInstance("",""))
                        .commit();
                currentFragment = FRAGMENT_MAIN;
                break;
        }
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event){
        Intent intent = new Intent(this, LicenceService.class);
        stopService(intent);
    }

    @Subscribe
    public void onConnectEvent(ConnectEvent event){
        Intent intent = new Intent(this, LicenceService.class);
        startService(intent);

        intent = new Intent(this, MatchService.class);
        startService(intent);

    }

    class SearchEditTextWatcher implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            EventBus.getDefault().post(new SearchLicenceEvent(s.toString().toLowerCase()));
        }
    }

    @Subscribe
    public void onSearchVisibilityEvent(SearchVisibilityEvent event){
        if(searchEdit != null){
            searchImage.setVisibility(event.visibility);
            if(event.visibility == View.GONE)
                searchEdit.setVisibility(event.visibility);

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
        switch (id){
            case R.id.action_n1:
                equipe = getResources().getString(R.string.n1);
                break;
            case R.id.action_n2:
                equipe = getResources().getString(R.string.n2);
                break;
            case R.id.action_n3:
                equipe = getResources().getString(R.string.n3);
                break;
            case R.id.action_n4:
                equipe = getResources().getString(R.string.n4);
                break;
        }

        RealtimeInfoJoueurs.contruct(getApplicationContext());
        displayFragment(currentFragment);


        return super.onOptionsItemSelected(item);
    }

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
                displayFragment(FRAGMENT_MAIN);
                break;
            case R.id.nav_send:
                if(isConnect){
                    signOut();
                    item.setTitle("se connecter");

                } else{
                    signIn();
                    item.setTitle("se déconnecter");
                }

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

    public void timerFragmentClick(View v){
        displayFragment(FRAGMENT_TIMER);
    }

    public void licenceFragmentClick(View v){
        displayFragment(FRAGMENT_LICENCES);
    }

    public void matchFragmentClick(View v){ displayFragment(FRAGMENT_RESULTAT);    }

}
