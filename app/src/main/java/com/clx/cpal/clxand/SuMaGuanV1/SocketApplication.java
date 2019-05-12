package com.clx.cpal.clxand.SuMaGuanV1;

import android.app.Application;
import android.content.Intent;

public class SocketApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this,SocketService.class));
    }
}
