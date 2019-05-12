package com.clx.cpal.clxand.SuMaGuanV1;



import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


public class SuMaGuanTcp extends Thread{


    Socket socket_;
    PrintWriter printWriter;
    int timeout = 0;


    Handler mHandler = null;

    SuMaGuanTcp(Handler handler){
        mHandler = handler;
    }





    @Override
    public void run() {
        //super.run();


        //TCP 连接


        Log.e("clx","run()...");


        while(true){
            try {
                timeout  =0;
                socket_ = new Socket();

                InetSocketAddress socketAddress = new InetSocketAddress("www.hongyiweichuang.com",20000);

                socket_.setSoTimeout(2000);

                socket_.connect(socketAddress);

                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(   //步骤二
                        socket_.getOutputStream(), "UTF-8")), true);

                printWriter.println("sub/"+SuMaGuanUuid.getInstance().getUUID()
                );


                if (socket_.isConnected()){
                    Log.e("clx","Connected!");




                    while(true){



                        try {

                            if (!socket_.isConnected()){
                                Log.e("clx","disconnected!");
                                break;
                            }

                            if (timeout >= 20){
                                Log.e("clx","timeout reconenct");
                                break;
                            }

                            BufferedReader in = new BufferedReader(new InputStreamReader(socket_.getInputStream(), "UTF-8"));
                            String receiveMsg = in.readLine();

                            if (receiveMsg != null) {
                                if (receiveMsg.equals("")){

                                }else{
                                    if (receiveMsg.endsWith("$")){
                                        receiveMsg = receiveMsg.substring(0,receiveMsg.length()-1);
                                        String result[] = receiveMsg.split("&");

                                        for (String str:
                                                result) {
                                            Log.e("clx","result[]="+str);
                                        }

                                        if (result.length == 3){

                                            String ressult2[] = result[2].split("=");

                                            if (ressult2.length == 2){

                                                String result3[] = ressult2[1].split("/");

                                                if (result3.length == 2){
                                                    for (String res: result3
                                                         ) {
                                                        Log.e("clx","result3[]="+res);
                                                    }

                                                    if (result3[0] == "s"){
                                                        Message msg = new Message();
                                                        msg.what = 0x1000;
                                                        msg.obj = result3[1];
                                                        mHandler.sendMessage(msg);
                                                    }
                                                }

                                            }
                                        }

                                    }

                                }
                            }



                            Log.e("clx", "tcp recv = |" + receiveMsg);
                        }catch (IOException e){

                            Log.e("clx",e.getMessage());

                            if (e.getMessage().equals("Read timed out")){
                                timeout++;
                            }

                            e.printStackTrace();
                        }






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
