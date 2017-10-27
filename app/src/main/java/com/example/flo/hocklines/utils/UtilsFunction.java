package com.example.flo.hocklines.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.Display;

import com.example.flo.hocklines.R;
import com.example.flo.hocklines.licence.LicenceContentValues;
import com.example.flo.hocklines.licence.LicenceCursor;
import com.example.flo.hocklines.licence.LicenceSelection;
import com.example.flo.hocklines.match.MatchContentValues;
import com.example.flo.hocklines.match.MatchCursor;
import com.example.flo.hocklines.match.MatchSelection;

import java.io.Writer;

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

    public static LicenceSelection getLicence(String identifiant, Context c){
        LicenceSelection where = new LicenceSelection();
        where.nomjoueur(identifiant);
        LicenceCursor licence = where.query(c);
        if(licence.moveToNext())
            return where;
        else
            return null;
    }

    public static MatchSelection getMatch(String date, String heure, Context c){
        MatchSelection where = new MatchSelection();
        where.date(date).and().heure(heure);
        MatchCursor cursor = where.query(c);
        if(cursor.moveToNext())
            return where;
        else
            return null;
    }

    public static void updateMatch(String date, String heure, Context c, MatchContentValues values){
        MatchSelection where = getMatch(date,heure,c);
        if(where != null){
            values.update(c,where);
        }else{
            values.insert(c);
        }
    }

    public static boolean isLicenceExistInBDD(String identifiant, Context c){
        LicenceSelection where = new LicenceSelection();
        where.nomjoueur(identifiant);
        LicenceCursor licence = where.query(c);
        if(licence.moveToNext() && licence.getPathfile() != null)
            return true;
        else
            return false;
    }


    public static void updateLicence(String identifiant, Context c, LicenceContentValues values){
        LicenceSelection where = getLicence(identifiant,c);
        if(where!=null){
            Log.d("LOG","UPDATE : "+identifiant);
            values.update(c,where);
        }else{
            Log.d("LOG","ERREUR ID n'existe pas : ");
            values.insert(c);
        }
    }

    public static LicenceCursor getAllLicence(Context c){
        LicenceSelection where = new LicenceSelection().orderBy("nomJoueur");
        LicenceCursor licence = where.query(c);
        licence.moveToNext();
        return licence;
    }

    public static String getPathOfLicence(String identifiant, Context c){
        LicenceSelection where = new LicenceSelection();
        where.nomjoueur(identifiant);
        LicenceCursor licence = where.query(c);
        boolean b = licence.moveToFirst();
        if(b)
           return licence.getPathfile();
        else
            return null;
    }

    public static void playSound(Context c){
        MediaPlayer mPlayer = null;
        mPlayer = MediaPlayer.create(c, R.raw.notification);
        mPlayer.start();

    }

    public static Point getScreenSizeInPixel(Activity a){
        Display display = a.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size;
    }

    public static LicenceCursor getAllLicenceByEquipe(Context context, String equipe) {
        LicenceSelection where = new LicenceSelection().equipe(equipe).orderBy("nomJoueur");
        LicenceCursor licence = where.query(context);
        licence.moveToNext();
        return licence;
    }

}
