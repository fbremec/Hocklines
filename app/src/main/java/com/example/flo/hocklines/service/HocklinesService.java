package com.example.flo.hocklines.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.example.flo.hocklines.service.Firebase.RealtimeInfoJoueurs;

public class HocklinesService extends Service {



    public HocklinesService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RealtimeInfoJoueurs.contruct(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
