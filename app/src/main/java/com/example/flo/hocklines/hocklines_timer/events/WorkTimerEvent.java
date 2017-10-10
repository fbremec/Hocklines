package com.example.flo.hocklines.hocklines_timer.events;

import com.example.flo.hocklines.utils.UtilsFunction;

/**
 * Created by Flo on 06/10/2017.
 */

public class WorkTimerEvent {


    public String currentTimer;

    public WorkTimerEvent(int minute, int seconde) {
        currentTimer = UtilsFunction.miseEnFormeTimer(minute,seconde);
    }


}
