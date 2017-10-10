package com.example.flo.hocklines.hocklines_timer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.flo.hocklines.R;
import com.example.flo.hocklines.hocklines_timer.models.Timer;
import com.example.flo.hocklines.hocklines_timer.models.TimerGestionnaire;

/**
 * Created by Flo on 09/10/2017.
 */

public class HocklinesTimerFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int RESUME_AFTER_SAVE = 1;
    public static final int RESUME = 0;

    private static final String TAG = "";
    private TextView timerText;
    private Button start;
    private Button stop;
    private EditText workTimerMinute;
    private EditText workTimerSeconde;
    private EditText sleepTimerMinute;
    private EditText sleepTimerSeconde;
    private EditText nbSerieMax;
    private EditText nbExerciceMax;
    private TimerGestionnaire timer;
    private TextView separator1;
    private TextView separator2;
    private TextView nbExerciceMaxText;
    private TextView nbSerieMaxText;
    private TextView typeTimer;
    private int currentSerie;
    private int currentExercice;
    private Bundle save = new Bundle();

    int nbSerie;
    int nbExercice;

    public HocklinesTimerFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public HocklinesTimerFragment newInstance(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hocklines_timer_fragment, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
        this.init(rootView);
        return rootView;
    }



    private void init(View rootView){

        timerText = (TextView)rootView.getRootView().findViewById(R.id.activity_main_textview_timer);
        start = (Button)rootView.getRootView().findViewById(R.id.activity_main_button_start);
        start.setOnClickListener(new MyOnClickListener());
        stop = (Button)rootView.getRootView().findViewById(R.id.activity_main_button_stop);
        stop.setOnClickListener(new MyOnClickListener());

        workTimerMinute = (EditText)rootView.getRootView().findViewById(R.id.activity_main_edittext_work_minute);
        workTimerMinute.addTextChangedListener(new TextWatcherTimerListsner(workTimerMinute));
        workTimerMinute.addTextChangedListener(new TextWatcherActivateButtonListener());

        workTimerSeconde = (EditText)rootView.getRootView().findViewById(R.id.activity_main_edittext_work_seconde);
        workTimerSeconde.addTextChangedListener(new TextWatcherTimerListsner(workTimerSeconde));
        workTimerSeconde.addTextChangedListener(new TextWatcherActivateButtonListener());

        sleepTimerMinute = (EditText)rootView.getRootView().findViewById(R.id.activity_main_edittext_sleep_minute);
        sleepTimerMinute.addTextChangedListener(new TextWatcherTimerListsner(sleepTimerMinute));
        sleepTimerMinute.addTextChangedListener(new TextWatcherActivateButtonListener());

        sleepTimerSeconde = (EditText)rootView.getRootView().findViewById(R.id.activity_main_edittext_sleep_seconde);
        sleepTimerSeconde.addTextChangedListener(new TextWatcherTimerListsner(sleepTimerSeconde));
        sleepTimerSeconde.addTextChangedListener(new TextWatcherActivateButtonListener());

        nbSerieMax = (EditText)rootView.getRootView().findViewById(R.id.activity_main_edittext_serie);
        nbSerieMax.addTextChangedListener(new TextWatcherActivateButtonListener());

        nbExerciceMax = (EditText)rootView.getRootView().findViewById(R.id.activity_main_edittext_exercice);
        nbExerciceMax.addTextChangedListener(new TextWatcherActivateButtonListener());

        separator1 = (TextView)rootView.getRootView().findViewById(R.id.activity_main_textview_separator1);
        separator2 = (TextView)rootView.getRootView().findViewById(R.id.activity_main_textview_separator2);

        nbExerciceMaxText = (TextView)rootView.getRootView().findViewById(R.id.activity_main_textview_nbExerciceMax);
        nbSerieMaxText = (TextView)rootView.getRootView().findViewById(R.id.activity_main_textview_nbSerieMax);

        typeTimer = (TextView)rootView.getRootView().findViewById(R.id.activity_main_textview_typeTimer);


    }

    public void setEnableAllEditText(final boolean b){
        nbExerciceMax.setEnabled(b);
        nbSerieMax.setEnabled(b);
        workTimerMinute.setEnabled(b);
        workTimerSeconde.setEnabled(b);
        sleepTimerMinute.setEnabled(b);
        sleepTimerSeconde.setEnabled(b);
    }



    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Button b = (Button)view;
            switch (b.getId()){
                case R.id.activity_main_button_start:
                    nbSerie = Integer.parseInt(nbSerieMax.getText().toString());
                    nbExercice = Integer.parseInt(nbExerciceMax.getText().toString());
                    int workMinute = Integer.parseInt(workTimerMinute.getText().toString());
                    int workSeconde = Integer.parseInt(workTimerSeconde.getText().toString());
                    int sleepMinute = Integer.parseInt(sleepTimerMinute.getText().toString());
                    int sleepSeconde = Integer.parseInt(sleepTimerSeconde.getText().toString());

                    separator1.setVisibility(View.VISIBLE);
                    separator2.setVisibility(View.VISIBLE);
                    nbSerieMaxText.setVisibility(View.VISIBLE);
                    nbSerieMaxText.setText(nbSerie+"");
                    nbExerciceMaxText.setVisibility(View.VISIBLE);
                    nbExerciceMaxText.setText(nbExercice+"");

                    nbExerciceMax.setText(0+"");
                    nbSerieMax.setText(0+"");
                    currentExercice = 0;
                    currentSerie = 0;

                    typeTimer.setText("Exercice :");

                    timer = new TimerGestionnaire(new Timer(workMinute,workSeconde),new Timer(sleepMinute,sleepSeconde),nbExercice,nbSerie);
                    timer.startSeance();
                    start.setEnabled(false);
                    stop.setEnabled(true);
                    setEnableAllEditText(false);
                    break;
                case R.id.activity_main_button_stop:
                    timer.stopSeance();
                    break;
            }
        }
    }

    public boolean seanceIsStarting(){
        return timer != null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","::::::::::::::");
        if(timer != null)
            timer.stopSeance();
    }




    class TextWatcherActivateButtonListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!workTimerMinute.getText().toString().equals("") && !workTimerSeconde.getText().toString().equals("")
                    && !sleepTimerMinute.getText().toString().equals("") && !sleepTimerSeconde.getText().toString().equals("")
                    && !nbSerieMax.getText().toString().equals("") && !nbExerciceMax.getText().toString().equals("")){
                start.setEnabled(true);
            }else{
                start.setEnabled(false);
            }
        }
    }

    class TextWatcherTimerListsner implements TextWatcher{

        private EditText edit;

        public TextWatcherTimerListsner(EditText edit) {
            this.edit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!charSequence.toString().equals("")){
                int time = Integer.parseInt(charSequence.toString());
                if(charSequence.toString().length() == 1 && time >= 6){
                    edit.setText("");
                }
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public TextView getTimerText() {
        return timerText;
    }

    public Button getStart() {
        return start;
    }

    public Button getStop() {
        return stop;
    }

    public EditText getWorkTimerMinute() {
        return workTimerMinute;
    }

    public EditText getWorkTimerSeconde() {
        return workTimerSeconde;
    }

    public EditText getSleepTimerMinute() {
        return sleepTimerMinute;
    }

    public EditText getSleepTimerSeconde() {
        return sleepTimerSeconde;
    }

    public EditText getNbSerieMax() {
        return nbSerieMax;
    }

    public EditText getNbExerciceMax() {
        return nbExerciceMax;
    }

    public TimerGestionnaire getTimer() {
        return timer;
    }

    public TextView getSeparator1() {
        return separator1;
    }

    public TextView getSeparator2() {
        return separator2;
    }

    public TextView getNbExerciceMaxText() {
        return nbExerciceMaxText;
    }

    public TextView getNbSerieMaxText() {
        return nbSerieMaxText;
    }

    public TextView getTypeTimer() {
        return typeTimer;
    }

    public int getCurrentSerie() {
        return currentSerie;
    }

    public int getCurrentExercice() {
        return currentExercice;
    }

    public Bundle getSave() {
        return save;
    }

    public int getNbSerie() {
        return nbSerie;
    }

    public int getNbExercice() {
        return nbExercice;
    }

    public void setTimerText(TextView timerText) {
        this.timerText = timerText;
    }

    public void setStart(Button start) {
        this.start = start;
    }

    public void setStop(Button stop) {
        this.stop = stop;
    }

    public void setWorkTimerMinute(EditText workTimerMinute) {
        this.workTimerMinute = workTimerMinute;
    }

    public void setWorkTimerSeconde(EditText workTimerSeconde) {
        this.workTimerSeconde = workTimerSeconde;
    }

    public void setSleepTimerMinute(EditText sleepTimerMinute) {
        this.sleepTimerMinute = sleepTimerMinute;
    }

    public void setSleepTimerSeconde(EditText sleepTimerSeconde) {
        this.sleepTimerSeconde = sleepTimerSeconde;
    }

    public void setNbSerieMax(EditText nbSerieMax) {
        this.nbSerieMax = nbSerieMax;
    }

    public void setNbExerciceMax(EditText nbExerciceMax) {
        this.nbExerciceMax = nbExerciceMax;
    }

    public void setTimer(TimerGestionnaire timer) {
        this.timer = timer;
    }

    public void setSeparator1(TextView separator1) {
        this.separator1 = separator1;
    }

    public void setSeparator2(TextView separator2) {
        this.separator2 = separator2;
    }

    public void setNbExerciceMaxText(TextView nbExerciceMaxText) {
        this.nbExerciceMaxText = nbExerciceMaxText;
    }

    public void setNbSerieMaxText(TextView nbSerieMaxText) {
        this.nbSerieMaxText = nbSerieMaxText;
    }

    public void setTypeTimer(TextView typeTimer) {
        this.typeTimer = typeTimer;
    }

    public void setCurrentSerie(int currentSerie) {
        this.currentSerie = currentSerie;
    }

    public void setCurrentExercice(int currentExercice) {
        this.currentExercice = currentExercice;
    }

    public void setSave(Bundle save) {
        this.save = save;
    }

    public void setNbSerie(int nbSerie) {
        this.nbSerie = nbSerie;
    }

    public void setNbExercice(int nbExercice) {
        this.nbExercice = nbExercice;
    }
}

