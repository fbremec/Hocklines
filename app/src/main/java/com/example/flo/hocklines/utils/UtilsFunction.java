package com.example.flo.hocklines.utils;

/**
 * Created by Flo on 06/10/2017.
 */

public class UtilsFunction {

    public static String miseEnFormeTimer(int minute, int seconde){
        String sSeconde = seconde+"";
        String sMinute = minute+"";
        if(seconde < 10){
            sSeconde = "0"+seconde;
        }
        if(minute<10){
            sMinute = "0"+minute;
        }
        return sMinute+":"+sSeconde;
    }
}
