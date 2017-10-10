package com.example.flo.hocklines.hocklines_timer.models;

import android.util.Log;

import com.example.flo.hocklines.hocklines_timer.events.SleepTimerEvent;
import com.example.flo.hocklines.hocklines_timer.events.SleepTimerFinishEvent;
import com.example.flo.hocklines.hocklines_timer.events.WorkTimerEvent;
import com.example.flo.hocklines.hocklines_timer.events.WorkTimerFinishEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Flo on 06/10/2017.
 */

public class Timer extends Thread{

    public final static String SLEEP = "sleep";
    public final static String WORK = "work";

    private String typeTimer;

    private int minuteEnd = 0;
    private int secondeEnd = 0;

    private int updateMinute;
    private int updateSeconde;

    private boolean isInterrupted;

    public Timer(int minuteEnd, int secondeEnd) {
        this.minuteEnd = (minuteEnd);
        this.secondeEnd = (secondeEnd);
    }


    @Override
    public void run() {
        updateMinute = 0;
        updateSeconde = 0;
        isInterrupted = false;
        updateTimer();
        for(int currentMinute=0; currentMinute<=minuteEnd; currentMinute++){
            if(currentMinute != minuteEnd){
                Log.d("Timer","premier passage de boucle");
                for(int currentSeconde=0; currentSeconde<59; currentSeconde++){
                    updateTimer();
                }
                updateSeconde = 0;

            }else{
                Log.d("Timer","dernier passage de boucle");
                for(int currentSeconde=0; currentSeconde<secondeEnd; currentSeconde++){
                    updateTimer();
                }
                updateSeconde = 0;
            }
            updateMinute ++;
        }
        timerFinish();
    }


    private void timerFinish(){
        if(typeTimer == WORK)
            EventBus.getDefault().post(new WorkTimerFinishEvent());
        else
            EventBus.getDefault().post(new SleepTimerFinishEvent());
    }

    private void updateTimer(){
        if(!isInterrupted) {
            try {
                this.sleep(1000);
                if (typeTimer == WORK) {
                    updateWorkTimer();
                }else {
                    updateSleepTimer();
                }
                updateSeconde++;
            } catch (InterruptedException e) {
                e.printStackTrace();
                isInterrupted = true;
                timerFinish();
            }
        }

    }

    private void updateSleepTimer() {
        EventBus.getDefault().post(new SleepTimerEvent(updateMinute,updateSeconde));
    }

    private void updateWorkTimer() {
        EventBus.getDefault().post(new WorkTimerEvent(updateMinute,updateSeconde));
    }

    public void setTypeTimer(String typeTimer) {
        this.typeTimer = typeTimer;
    }

    @Override
    public boolean isInterrupted() {
        return isInterrupted;
    }
}
