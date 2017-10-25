package com.example.flo.hocklines.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.example.flo.hocklines.service.Firebase.RealtimeInfoJoueurs;

public class HocklinesService extends Service {



    public HocklinesService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void constructDataBase(){
        RealtimeInfoJoueurs.contruct(getApplicationContext());
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
