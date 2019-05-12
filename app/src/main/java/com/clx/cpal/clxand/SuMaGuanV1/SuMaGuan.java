package com.clx.cpal.clxand.SuMaGuanV1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clx.cpal.clxand.CustomCaptureActivity;
import com.clx.cpal.clxand.MainActivity;
import com.clx.cpal.clxand.R;
import com.clx.cpal.clxand.SuMaGuanActivity;
import com.clx.cpal.clxand.util.UriUtils;
import com.king.zxing.Intents;
import com.king.zxing.util.CodeUtils;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class SuMaGuan extends AppCompatActivity {


    Button btnScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su_ma_guan2);



        btnScan = findViewById(R.id.btnScan);


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cls = CustomCaptureActivity.class;
                title = ((Button)v).getText().toString();
//                this.cls = CaptureActivity.class;
//                this.title = ((Button)v).getText().toString();
                checkCameraPermissions();


            }
        });

        this.cls = CustomCaptureActivity.class;
        this.title = "首次绑定设备";









    }









    public static final String KEY_TITLE = "key_title";
    public static final int RC_CAMERA = 0X01;
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;
    private boolean isContinuousScan = false;
    private Class<?> cls;
    private String title;


    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls,title);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera),
                    RC_CAMERA, perms);
        }
    }


    //完成权限设置回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        checkCameraPermissions();
        //Log.e("clx",permissions.toString());
    }

    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_IS_CONTINUOUS,isContinuousScan);
        ActivityCompat.startActivityForResult(this,intent,REQUEST_CODE_SCAN,optionsCompat.toBundle());
    }



    void GotoApp(){

        Intent intent = new Intent();
        intent.setClass(SuMaGuan.this, SuMaGuanAction.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//清楚之前的界面
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);





                    String result2[] = result.split("/");

                    Log.e("clx", String.valueOf(result2.length));

                    for (int i=0;i<result2.length;++i){
                        Log.e("clx", "result2["+i+"]"+result2[i]);

                    }


                    if (result2.length == 4){

                        if (result2[0].equals("")) {
                        if (result2[1].equals("V1.0")) {
                                if (result2[2].equals("SuMaGuan4GV1.0")) {
                                    Toast.makeText(this,result2[3],Toast.LENGTH_LONG).show();
                                    //跳转到
                                    boolean res = SuMaGuanUuid.getInstance().setUUID(result2[3]);
                                    Log.e("clx",SuMaGuanUuid.getInstance().getUUID());
                                    if (res == false){
                                        Toast.makeText(this,"设置设备参数错误",Toast.LENGTH_LONG).show();
                                    }else {

                                        GotoApp();//app跳转
                                    }
                                }else{
                                    Toast.makeText(this,"无法兼容的设备版本",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(this,"无法兼容的协议版本",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(this,"二维码非法",Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(this,result2[3],Toast.LENGTH_LONG).show();
                    }else{
                        //Toast.makeText(this,result2.length,Toast.LENGTH_LONG).show();
                        Toast.makeText(this,"二维码非法",Toast.LENGTH_LONG).show();
                    }






//                    if (checkIsUuid(result)){
//
//                        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
//
//                        //UUID 匹配起来了
//
//
//                        //写入数据库
//
//                        try{
//                            db.delete("uuid", null , null);  //删除所有记录
//                        }catch (Exception e){
//
//                        }
//
//
//
//                        //创建存放数据的ContentValues对象
//                        ContentValues values = new ContentValues();
//                        values.put("uuid",result);
//                        //数据库执行插入命令
//                        db.insert("uuid", null, values);
//
//
//                        GotoApp();//app跳转
//
//
//
//                    }else{
//                        Toast.makeText(this,"非法的二维码",Toast.LENGTH_SHORT).show();
//                    }

                    break;
                case REQUEST_CODE_PHOTO:
                    parsePhoto(data);
                    break;
            }

        }


    }


    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }


    private Context getContext(){
        return this;
    }

    private void parsePhoto(Intent data){
        final String path = UriUtils.INSTANCE.getImagePath(this,data);
        Log.d("Jenly","path:" + path);
        if(TextUtils.isEmpty(path)){
            return;
        }
        //异步解析
        asyncThread(new Runnable() {
            @Override
            public void run() {
                final String result = CodeUtils.parseCode(path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jenly","result:" + result);
                        Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
