package com.codefactory.devtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.codefactory.devtest.serwisy.SensorServiceConnection;
import com.codefactory.devtest.serwisy.ServiceListener;
import com.codefactory.devtest.serwisy.ServiceManager;
import com.codefactory.devtest.utils.ButtonFactory;
import com.codefactory.devtest.utils.SensiSensors;

public class SensorsActivity extends Activity {

    private TextView tvLight,tvNoise,tvVibration,tvOther;
    private Button btnStop;
    private SensiSensors sensors;
    private boolean serviceStarted = false;
    private SensorServiceConnection mConnection;
    private ServiceListener mCallback;
    private ServiceManager mManager;
    private ButtonFactory btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        btn = new ButtonFactory();
        tvLight = (TextView) findViewById(R.id.sensnor_text_light);
        tvNoise = (TextView) findViewById(R.id.sensnor_text_noise);
        tvVibration = (TextView) findViewById(R.id.sensnor_text_winrations);
        tvOther = (TextView) findViewById(R.id.sensnor_text_other);
        btnStop = (Button) findViewById(R.id.sensors_btn_stop);
        btn.addBtn(R.id.sensors_btn_stop, "sensors_btn_stop");

        //TODO: finish service with sensor listener.....
//		serviceStarted = mManager.

        Buttons button = new Buttons();
        btnStop.setOnClickListener(button);
        sensors = new SensiSensors(this);
        sensors.checkLight(tvLight);

        sensors.checkAcceleration(tvVibration);
        sensors.checkRotation(tvOther);
        sensors.checkNoise(tvNoise);
    }

    class Buttons implements OnClickListener{

        @Override
        public void onClick(View v) {
            if(btn.getBtnId(R.id.sensors_btn_stop)==v.getId()){
                if(sensors!=null) sensors.disableSensorManager();
            }
//			switch(v.getId()){
//			case R.id.sensors_btn_stop:
//				if(sensors!=null) sensors.disableSensorManager();
//				break;
//			}
        }

    }
}
