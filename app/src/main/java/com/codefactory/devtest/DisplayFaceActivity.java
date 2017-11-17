package com.codefactory.devtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.codefactory.devtest.visionface.FaceVisionUtils;

public class DisplayFaceActivity extends Activity {

    private final static String TAG=DisplayFaceActivity.class.getSimpleName();
    private TextView tvInfo;
    private ImageView img;
    private Bitmap face;
    private String bioInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_face);

        img = (ImageView) findViewById(R.id.displayface_img);
        tvInfo = (TextView) findViewById(R.id.displayface_info_txt);

        Bitmap b = FaceVisionUtils.getBtm();
        if(b!=null) {
            img.setImageBitmap(b);
            Log.d(TAG, "display bitmap H: "+b.getHeight()+" W: "+b.getWidth());
        }

        tvInfo.setTextColor(Color.WHITE);
        tvInfo.setBackgroundColor(Color.BLACK);
//		tvInfo.setVisibility(View.GONE);
        tvInfo.setText(FaceVisionUtils.bioTemp);



    }


}
