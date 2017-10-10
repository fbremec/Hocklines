package com.example.flo.hocklines.hocklines_timer.models;

import android.util.Log;

import com.example.flo.hocklines.hocklines_timer.events.IncrementWorkEvent;
import com.example.flo.hocklines.hocklines_timer.events.SleepTimerFinishEvent;
import com.example.flo.hocklines.hocklines_timer.events.StopSeanceEvent;
import com.example.flo.hocklines.hocklines_timer.events.WorkTimerFinishEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Flo on 06/10/2017.
 */

public class TimerGestionnaire {


    private Timer workTimer;
    private Timer sleepTimer;

    private int nbExerciceMax;
    private int nbSerieMax;

    private int currentExercice = 1;
    private int currentSerie = 1;



    public TimerGestionnaire(Timer workTimer, Timer sleepTimer, int nbExerciceMax, int nbSerieMax) {
        this.workTimer = workTimer;
        this.workTimer.setTypeTimer(Timer.WORK);
        this.sleepTimer = sleepTimer;
        this.sleepTimer.setTypeTimer(Timer.SLEEP);
        this.nbExerciceMax = nbExerciceMax;
        this.nbSerieMax = nbSerieMax;
        EventBus.getDefault().register(this);
    }

    public void startSeance(){
        workTimer.start();
    }

    @Subscribe
    public void onWorkTimerFinishEvent(WorkTimerFinishEvent event) {
        Log.d("onWorkTimerFinishEvent",":::::::::::");
        if(workTimer != null && !workTimer.isInterrupted()) {
            sleepTimer.start();
            currentExercice++;
        }
    }

    @Subscribe
    public void onSleepTimerFinishEvent(SleepTimerFinishEvent event) {
        Log.d("onSleepTimerFinishEvent",":::::::::::");
        if(sleepTimer != null && !sleepTimer.isInterrupted()) {
            if (currentExercice > nbExerciceMax) {
                currentExercice = 1;
                currentSerie++;
            }
            if (currentSerie <= nbSerieMax) {
                workTimer.start();
                EventBus.getDefault().post(new IncrementWorkEvent());
            } else {
                currentSerie++;
                stopSeance();
            }
        }


    }

    public void stopSeance(){

        workTimer.interrupt();
        sleepTimer.interrupt();

        EventBus.getDefault().post(new StopSeanceEvent());
    }

    public void setWorkTimer(Timer workTimer) {
        this.workTimer = workTimer;
    }

    public void setSleepTimer(Timer sleepTimer) {
        this.sleepTimer = sleepTimer;
    }
}
