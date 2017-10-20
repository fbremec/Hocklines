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
import com.example.flo.hocklines.hocklines_timer.events.IncrementWorkEvent;
import com.example.flo.hocklines.hocklines_timer.events.SleepTimerEvent;
import com.example.flo.hocklines.hocklines_timer.events.StopSeanceEvent;
import com.example.flo.hocklines.hocklines_timer.events.WorkTimerEvent;
import com.example.flo.hocklines.hocklines_timer.models.Timer;
import com.example.flo.hocklines.hocklines_timer.models.TimerGestionnaire;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onIncrementWorkEvent(final IncrementWorkEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int exercice = currentExercice;
                int currentSerie = getCurrentSerie();
                if ((exercice + 1) % getNbExercice() == 0) {
                    int serie = getCurrentSerie();
                    currentSerie = (serie + 1) % getNbSerie();
                    setCurrentSerie(currentSerie);
                    getNbSerieMax().setText(getCurrentSerie() + "");
                }
                int currentExercice = (exercice + 1) % getNbExercice();
                setCurrentExercice(currentExercice);
                getNbExerciceMax().setText(currentExercice + "");

                Log.d("currentExercice",currentExercice+"");
                Log.d("currentSerie",currentSerie+"");
            }
        });
    }

    @Subscribe
    public void onWorkTimerEvent(final WorkTimerEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTypeTimer().setText("Exercice :");
                getTimerText().setText(event.currentTimer);
            }
        });
    }

    @Subscribe
    public void onSleepTimerEvent(final SleepTimerEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTypeTimer().setText("Repos :");
                getTimerText().setText(event.currentTimer);
            }
        });


    }

    @Subscribe
    public void onStopSeanceEvent(final StopSeanceEvent event) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTimer().setSleepTimer(null);
                getTimer().setWorkTimer(null);
                setTimer(null);
                getStop().setEnabled(false);
                getStart().setEnabled(true);
                getTimerText().setText("00:00");
                setCurrentExercice(getCurrentExercice()+1);
                setCurrentSerie(getCurrentSerie()+1);
                getNbSerieMax().setText(getNbSerieMaxText().getText());
                getNbExerciceMax().setText(getNbExerciceMaxText().getText());
                getNbExerciceMaxText().setVisibility(View.INVISIBLE);
                getNbSerieMaxText().setVisibility(View.INVISIBLE);
                getSeparator1().setVisibility(View.INVISIBLE);
                getSeparator2().setVisibility(View.INVISIBLE);
                setEnableAllEditText(true);
            }
        });


    }

    public void setEnableAllEditText(final boolean b){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getNbExerciceMax().setEnabled(b);
                getNbSerieMax().setEnabled(b);
                getWorkTimerMinute().setEnabled(b);
                getWorkTimerSeconde().setEnabled(b);
                getSleepTimerMinute().setEnabled(b);
                getSleepTimerSeconde().setEnabled(b);
            }
        });
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HocklinesTimerFragment newInstance() {
        return new HocklinesTimerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hocklines_timer_fragment, container, false);
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

