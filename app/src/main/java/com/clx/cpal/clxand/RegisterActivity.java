package com.clx.cpal.clxand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
//注册界面
public class RegisterActivity extends AppCompatActivity {
    Button btnBack = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnBack = findViewById(R.id.button_back);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();//结束当前的界面

            }
        });

    }
}
