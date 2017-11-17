package com.codefactory.devtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codefactory.devtest.utils.CameraStuff;
import com.codefactory.devtest.utils.HardwareUtils;
import com.codefactory.devtest.utils.ScreenStuff;
import com.codefactory.devtest.visionface.FaceVisionUtils;

import java.util.ArrayList;

public class CameraStuffActivity extends Activity {

    private ArrayList<CameraStuff> camList;
    private CameraStuff camStuff;
    private TextView tvCamId, tvSensorH, tvSensorW, tvBarH, tvScreenSize;
    private Button btnTwin, btnFaceVision;
    private ListView lv;
    private String cameraName;
    private int barH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_stuff);

        camStuff = new CameraStuff(getApplicationContext());
        camList=camStuff.getCamerasList();
//		barH = ScreenStuff.getSystemBarHeight(getApplicationContext());
        barH = ScreenStuff.getSystemBarHeight(getApplicationContext());

        tvScreenSize = (TextView) findViewById(R.id.camera_text_screen_info);
        tvBarH=(TextView) findViewById(R.id.camera_system_bar_height);
        lv = (ListView) findViewById(R.id.list_cameras);
        btnTwin = (Button) findViewById(R.id.camera_btn_twincamera);
        btnFaceVision = (Button) findViewById(R.id.camera_btn_facevision);

        tvScreenSize.setText("Screen Size: "+ScreenStuff.getScreenSize(this));
        tvBarH.setText("System Bar Height: "+barH);


        Display display = getWindowManager().getDefaultDisplay();
        Point scrSize = new Point();
        display.getSize(scrSize);
        int width = scrSize.x;
        int height = scrSize.y;
        HardwareUtils.screenHeight=height;
        HardwareUtils.screenWidth=width;

        MyAdapter adapter = new MyAdapter(getApplicationContext(),camList);
        lv.setAdapter(adapter);


        btnTwin.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CameraStuffActivity.this, TwinCameraActivity.class);
                startActivity(intent);
            }

        });
        btnFaceVision.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FaceVisionUtils.resetSmile();
                Intent intent = new Intent(CameraStuffActivity.this, VisionFaceTrackerActivity.class);
                startActivity(intent);
            }

        });

    }//end onCreate

    class MyAdapter  extends BaseAdapter{
        private ArrayList<CameraStuff> myList;
        private LayoutInflater inflater;

        MyAdapter(Context context, ArrayList<CameraStuff> list){
            this.myList=list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if(convertView==null) view = inflater.inflate(R.layout.adapter_cameras, parent,false);
            else view = convertView;

            tvCamId = (TextView) view.findViewById(R.id.adaptercamera_text_sensor_id);
            tvSensorH = (TextView) view.findViewById(R.id.adaptercamera_text_sensor_hight);
            tvSensorW = (TextView) view.findViewById(R.id.adaptercamera_text_sensor_width);

            cameraName="ID:"+myList.get(position).getCameraId()+" "+myList.get(position).getCameraName();
            tvCamId.setText(cameraName);
//			camera = myList.get(position).getCamera().open();
//			tvSensorH.setText(String.valueOf(myList.get(position).getCamera().getParameters().getPictureSize().height));
//			tvSensorW.setText(String.valueOf(myList.get(position).getCamera().getParameters().getPictureSize().width));
//			camera.release();
            tvSensorH.setText(String.valueOf(myList.get(position).getHeight()));
            tvSensorW.setText(String.valueOf(myList.get(position).getWidth()));
            return view;
        }

    }
}

//
