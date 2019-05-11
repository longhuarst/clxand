package com.clx.cpal.clxand.SuMaGuanV1;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SuMaGuanTcp extends Thread{


    Socket socket_;

    @Override
    public void run() {
        super.run();


        //TCP 连接

        while(true){
            try {
                socket_ = new Socket();

                InetSocketAddress socketAddress = new InetSocketAddress("www.hongyiweichuang.com",20000);

                socket_.setSoTimeout(2000);

                socket_.connect(socketAddress);


                if (socket_.isConnected()){
                    Log.e("clx","Connected!");


                    BufferedReader in = new BufferedReader(new InputStreamReader(socket_.getInputStream(), "UTF-8"));

                    while(true){

                        String receiveMsg = in.readLine();

                        






                    }

                }else{
                    Log.e("clx","NotConnected!");
                }





            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }





        //int i = 0;
//
//        while(true){
////            i++;
////
////            Log.e("clx", String.valueOf(i));
//
//
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }






    }
}
