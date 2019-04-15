/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clx.cpal.clxand;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clx.cpal.clxand.util.UriUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;
//import com.king.zxing.app.util.UriUtils;
import com.king.zxing.util.CodeUtils;

import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//import pub.devrel.easypermissions.AfterPermissionGranted;
//import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;






    void GotoApp(){

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SuMaGuanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//清楚之前的界面
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //依靠DatabaseHelper带全部参数的构造函数创建数据库
         dbHelper = new DatabaseHelper(MainActivity.this, "uuid_db",null,1);
         db = dbHelper.getWritableDatabase();


        //创建游标对象
        Cursor cursor = db.query("uuid", new String[]{"uuid"}, null, null, null, null, null);

        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        String uuid = "";
        if (cursor.moveToNext()){
            uuid =cursor.getString(cursor.getColumnIndex("uuid"));
        }

        //        while(cursor.moveToNext()){
//            String name = cursor.getString(cursor.getColumnIndex("name"));
//            break;
//            //textview_data = textview_data + "\n" + name;
//        }


        Log.e("clx","UUID = "+uuid);


        if (uuid == ""){
            //不存在  首次使用


            this.cls = CustomCaptureActivity.class;
            this.title = "首次绑定设备";
            checkCameraPermissions();

        }else{

            GotoApp();//app跳转

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);

                    if (checkIsUuid(result)){

                        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();

                        //UUID 匹配起来了


                        //写入数据库

                        try{
                            db.delete("uuid", null , null);  //删除所有记录
                        }catch (Exception e){

                        }



                        //创建存放数据的ContentValues对象
                        ContentValues values = new ContentValues();
                        values.put("uuid",result);
                        //数据库执行插入命令
                        db.insert("uuid", null, values);


                        GotoApp();//app跳转



                    }else{
                        Toast.makeText(this,"非法的二维码",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case REQUEST_CODE_PHOTO:
                    parsePhoto(data);
                    break;
            }

        }
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

    private Context getContext(){
        return this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

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

    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
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

    /**
     * 生成二维码/条形码
     * @param isQRCode
     */
    private void startCode(boolean isQRCode){
        Intent intent = new Intent(this,CodeActivity.class);
        intent.putExtra(KEY_IS_QR_CODE,isQRCode);
        intent.putExtra(KEY_TITLE,isQRCode ? getString(R.string.qr_code) : getString(R.string.bar_code));
        startActivity(intent);
    }

    private void startPhotoCode(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }

    @AfterPermissionGranted(RC_READ_PHOTO)
    private void checkExternalStoragePermissions(){
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startPhotoCode();
        }else{
            EasyPermissions.requestPermissions(this, getString(R.string.permission_external_storage),
                    RC_READ_PHOTO, perms);
        }
    }



    //判断是否是UUID

    /**
     * 检查字符串是否是合法的uuid,
     *
     * @param uuidStr
     * @return 是返回true,不是返回false
     */
    public static boolean checkIsUuid(String uuidStr) {
        try {
            UUID.fromString(uuidStr).toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }



    public void OnClick(View v){
        isContinuousScan = false;
        switch (v.getId()){
//            case R.id.btn0:
//                this.cls = CustomCaptureActivity.class;
//                this.title = ((Button)v).getText().toString();
//                isContinuousScan = true;
//                checkCameraPermissions();
//                break;
            case R.id.btn1:
                this.cls = CustomCaptureActivity.class;
                this.title = ((Button)v).getText().toString();
//                this.cls = CaptureActivity.class;
//                this.title = ((Button)v).getText().toString();
                checkCameraPermissions();
                break;
//            case R.id.btn2:
//                this.cls = EasyCaptureActivity.class;
//                this.title = ((Button)v).getText().toString();
//                checkCameraPermissions();
//                break;
//            case R.id.btn3:
//                this.cls = CustomCaptureActivity.class;
//                this.title = ((Button)v).getText().toString();
//                checkCameraPermissions();
//                break;
//            case R.id.btn4:
//                startCode(false);
//                break;
//            case R.id.btn5:
//                startCode(true);
//                break;
//            case R.id.btn6:
//                checkExternalStoragePermissions();
//                break;
        }

    }
}
