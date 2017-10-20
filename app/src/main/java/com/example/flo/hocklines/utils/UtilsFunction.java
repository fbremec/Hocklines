package com.example.flo.hocklines.utils;

import android.content.Context;
import android.util.Log;

import com.example.flo.hocklines.licence.LicenceContentValues;
import com.example.flo.hocklines.licence.LicenceCursor;
import com.example.flo.hocklines.licence.LicenceSelection;

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

    public static LicenceSelection checkIfLicenceExist(String identifiant, Context c){
        LicenceSelection where = new LicenceSelection();
        where.nomjoueur(identifiant);
        LicenceCursor licence = where.query(c);
        if(licence.moveToNext())
            return where;
        else
            return null;
    }

    public static void updateLicence(String identifiant, Context c, LicenceContentValues values){
        LicenceSelection where = checkIfLicenceExist(identifiant,c);
        if(where!=null){
            Log.d("LOG","UPDATE : "+identifiant);
            values.update(c,where);
        }else{
            Log.d("LOG","ERREUR ID n'existe pas : "+identifiant);
            values.insert(c);
        }
    }

    public static LicenceCursor getAllLicence(Context c){
        LicenceSelection where = new LicenceSelection();
        LicenceCursor licence = where.query(c);
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

}
