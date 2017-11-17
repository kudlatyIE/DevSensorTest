package com.codefactory.devtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codefactory.devtest.network.InternetConnection;
import com.codefactory.devtest.utils.ButtonFactory;
import com.codefactory.devtest.utils.DevDetails;
import com.codefactory.devtest.utils.DroidPermissions;

import java.util.UUID;

public class MainActivity extends Activity {

    private TextView tvMsg, tvScreen, tvNet;
    private Button btnCheckNet, btnOpenWifi, btnOpenMob, btnCamera, btnJavaView, btnForResult, btnSd, btnLib,btnSensor;
    private String dev, screen, uniqID;
    private boolean isNet, isWifi=false, isMob=false;
    private InternetConnection ic;
    private ButtonFactory btn;
    private final int requestNet = 10, requestPhone = 11, requestMic = 12, requestCamera  = 14,
                        requestWIFI = 15, requestMobile = 16, requestNativeLib = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btn = new ButtonFactory();

        tvMsg = (TextView) findViewById(R.id.text_msg);
        tvScreen = (TextView) findViewById(R.id.text_screen);
        tvNet = (TextView) findViewById(R.id.text_network_status);
        btnCheckNet = (Button) findViewById(R.id.btn_check_connection);
        btn.addBtn(R.id.btn_check_connection, "btn_check_connection");
        btnOpenWifi = (Button) findViewById(R.id.btn_open_wifi);
        btn.addBtn(R.id.btn_open_wifi, "btn_open_wifi");
        btnOpenMob = (Button) findViewById(R.id.btn_open_mobile);
        btn.addBtn(R.id.btn_open_mobile, "btn_open_mobile");
        btnCamera = (Button) findViewById(R.id.btn_getCameraParams);
        btn.addBtn(R.id.btn_getCameraParams, "btn_getCameraParams");
        btnJavaView = (Button) findViewById(R.id.btn_new_view);
        btn.addBtn(R.id.btn_new_view, "btn_new_view");
        btnForResult = (Button) findViewById(R.id.btn_for_result);
        btn.addBtn(R.id.btn_for_result, "btn_for_result");
        btnOpenWifi.setVisibility(View.GONE);
        btnOpenMob.setVisibility(View.GONE);
        btnSd = (Button) findViewById(R.id.btn_sd);
        btn.addBtn(R.id.btn_sd, "btn_sd");
        btnLib = (Button) findViewById(R.id.btn_check_library);
        btn.addBtn(R.id.btn_check_library, "btn_check_library");
        btnSensor = (Button) findViewById(R.id.btn_sensors_start);
        btn.addBtn(R.id.btn_sensors_start, "btn_sensors_start");

        dev = DevDetails.getDeviceName();
//		screen = DevDetails.getDensity(getApplicationContext());
        uniqID = UUID.randomUUID().toString();

        MyButtons button= new MyButtons();
        btnCheckNet.setOnClickListener(button);
        btnOpenWifi.setOnClickListener(button);
        btnOpenMob.setOnClickListener(button);
        btnCamera.setOnClickListener(button);
        btnJavaView.setOnClickListener(button);
        btnForResult.setOnClickListener(button);
        btnSd.setOnClickListener(button);
        btnLib.setOnClickListener(button);
        btnSensor.setOnClickListener(button);

        tvMsg.setText(dev);
        tvScreen.setText(uniqID);
        tvNet.setText("No internet connection :(");


    }

    private boolean checkNet(){
       return DroidPermissions.permissionGranted(this, Manifest.permission.INTERNET, requestNet);
    }
    private boolean checkWifi(){
        return DroidPermissions.permissionGranted(this, Manifest.permission.INTERNET, requestWIFI);
    }
    private boolean checkMobile(){
        return DroidPermissions.permissionGranted(this, Manifest.permission.INTERNET, requestMobile);
    }
    private boolean checkMic(){
        return DroidPermissions.permissionGranted(this, Manifest.permission.RECORD_AUDIO, requestMic);
    }

    private boolean checkStorage(){
        return  DroidPermissions.permissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                requestPhone);
    }
    private boolean checkNativeLib(){
        return DroidPermissions.permissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                requestNativeLib);
    }
    private boolean checkCamera(){
        return DroidPermissions.permissionGranted(this, Manifest.permission.CAMERA, requestCamera);
    }

    private void runNet(){
        isNet = ic.isConnection();
        if(isNet) {
            setVisibleBtn(btnOpenWifi,true);
            setVisibleBtn(btnOpenMob,true);
            tvNet.setText("internet connection ON");
            if(isWifi) btnOpenWifi.setText("WiFi OFF");
            if (isMob) btnOpenMob.setText("3G OFF");
        }
        else {
            tvNet.setText("Buuuuu");
            if(ic.getWifi()!=null) setVisibleBtn(btnOpenWifi,true);
            if(ic.getMob()!=null) setVisibleBtn(btnOpenMob,true);
        }
        if(isWifi) btnOpenWifi.setText("WiFi OFF"); else btnOpenWifi.setText("WiFi ON");
        if(isMob) btnOpenMob.setText("3G OFF"); else btnOpenMob.setText("3G ON");
    }

    private void runWifi(){
        if(isWifi) {
            isWifi=ic.openWiFi(false);
            tvNet.setText("WiFi connection OFF!");
            btnOpenWifi.setText("WiFi ON");
        }else{
            isWifi = ic.openWiFi(true);
            tvNet.setText("WiFi connection ON!");
            btnOpenWifi.setText("WiFi OFF");
        }
    }
    private void runCamera(){
        Intent intent = new Intent(getApplicationContext(), CameraStuffActivity.class);
        startActivity(intent);
    }
    private void runMobile(){
        if(isMob) {
            isMob=ic.openMobile(false);
            btnOpenMob.setText("3G ON");
            tvNet.setText("MobileData connection OFF!");
        }else{
            isMob = ic.openMobile(true);
            btnOpenMob.setText("3G OFF");
            tvNet.setText("MobileData connection ON!");
        }
    }

    private void setVisibleBtn(Button btn,boolean b){
        if(b) btn.setVisibility(View.VISIBLE);
        else  btn.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case requestNet: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runNet();
                } else {
                    Toast.makeText(this, "INTERNET permission not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case requestWIFI: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    runWifi();
                } else {
                    Toast.makeText(this, "INTERNET permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
            return;
            case requestMobile: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    runMobile();

                } else {
                    Toast.makeText(this, "INTERNET permission not granted", Toast.LENGTH_SHORT).show();
                }
            } return;
            case requestMic: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(getApplicationContext(),SensorsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "RECORD_AUDIO permission not granted", Toast
                            .LENGTH_SHORT).show();
                }
            } return;
            case requestPhone: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(getApplicationContext(),SDActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAG permission not granted", Toast.LENGTH_SHORT)
                            .show();
                }
            } return;
            case requestCamera: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    runCamera();
                } else {
                    Toast.makeText(this, "CAMERA permission not granted", Toast.LENGTH_SHORT).show();
                }
            } return;
            case requestNativeLib: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(getApplicationContext(), NativeLibActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "READ_PHONE_STATE permission not granted", Toast.LENGTH_SHORT).show();
                }
            } return;


        }
    }

    private class MyButtons implements OnClickListener{
        Intent intent;
        MyButtons(){
            ic = new InternetConnection(getApplicationContext());
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(btn.getBtnId(R.id.btn_check_connection)==id){
                if(checkNet()) runNet();

            }
            if(btn.getBtnId(R.id.btn_open_wifi)==id){
                if(checkWifi()) runWifi();
            }
            if(btn.getBtnId(R.id.btn_open_mobile)==id){
                if(checkMobile()) runMobile();
            }
            if(btn.getBtnId(R.id.btn_getCameraParams)==id){
               if(checkCamera()) runCamera();
            }
            if (btn.getBtnId(R.id.btn_new_view)==id){
                intent = new Intent(getApplicationContext(),JavaViewActivity.class);
                startActivity(intent);
            }
            if(btn.getBtnId( R.id.btn_for_result)==id){
                intent = new Intent(getApplicationContext(), GetResultActivity.class);
                startActivity(intent);
            }
            if(btn.getBtnId(R.id.btn_sd)==id){
                checkStorage();
            }
            if(btn.getBtnId(R.id.btn_check_library)==id){
                checkNativeLib();
            }
            if(btn.getBtnId(R.id.btn_sensors_start)==id){
                checkMic();
            }


        }

    }

}
