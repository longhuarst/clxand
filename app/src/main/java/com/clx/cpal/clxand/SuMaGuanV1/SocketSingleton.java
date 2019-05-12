package com.clx.cpal.clxand.SuMaGuanV1;

import android.util.Log;

public class SocketSingleton {

    private static class SocketSingletonHolder {
        private  static SocketSingleton instance = new SocketSingleton();
    }


    private SocketSingleton(){

    }

    public static SocketSingleton getInstance(){
        return SocketSingletonHolder.instance;
    }


    ///==========================================


    SocketService service;


    public SocketService getService(){
        return service;
    }


    public void setService(SocketService service){
        Log.e("clx","setService");
        this.service = service;
    }


}
