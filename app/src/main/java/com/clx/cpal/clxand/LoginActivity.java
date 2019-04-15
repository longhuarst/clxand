package com.clx.cpal.clxand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//登陆界面
//登陆界面
//登陆界面
//登陆界面




public class LoginActivity extends AppCompatActivity {


    Button btnRegister = null;
    Button btnLogin = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        btnRegister = findViewById(R.id.button_register);
        btnLogin = findViewById(R.id.button_login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);

            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//清楚之前的界面
                startActivity(intent);


            }
        });










    }
}
