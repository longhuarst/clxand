package com.clx.cpal.clxand.SuMaGuanV1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.clx.cpal.clxand.R;



public class SuMaGuanAction extends AppCompatActivity {

    String uuid = "";

    SuMaGuanTcp socket_ = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su_ma_guan_action);


        uuid = SuMaGuanUuid.getInstance().getUUID();


        Log.e("clx",SuMaGuanUuid.getInstance().getUUID());

        if (uuid.equals("")){
            Toast.makeText(this,"无法正确识别设备",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,uuid,Toast.LENGTH_LONG).show();

            socket_ = new SuMaGuanTcp();
            socket_.start();
        }








    }
}
