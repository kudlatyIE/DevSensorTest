package com.codefactory.devtest.utils;

/**
 * Created by kudlaty on 13/10/2016.
 */

import org.opencv.android.JavaCameraView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JavaViewCreator {
    private Activity ac;
    private Context context;
    private LinearLayout ll;
    private TextView tv;
    private RelativeLayout rl;
    private TextView tvHint, tvRepeat;
    private ImageView ivPhoto;
    private EditText etUserName;
    private Button btnSave;
    private JavaCameraView cView;
    private int cameraId;



    public JavaViewCreator(Activity ac){
        this.ac = ac;
    }

    public LinearLayout getFdLayout(){

        try {
            cameraId = CameraStuff.getFrontCameraId();
        } catch (Exception e) {	e.printStackTrace(); }
//		this.cView = new JavaCameraView(ac, cameraId);
        this.cView = new JavaCameraView(ac, JavaCameraView.CAMERA_ID_FRONT);
        this.tv = new TextView(ac);
        ll = new LinearLayout(ac);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llParam);
        LinearLayout.LayoutParams cViewParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,//width
                LinearLayout.LayoutParams.WRAP_CONTENT);//height

        tv.setBackgroundColor(Color.parseColor("#F0E678"));
        tv.setTextColor(Color.parseColor("#4048A8"));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);

        tv.setText("generate text view");
        tv.setId(99999999);
        ll.addView(tv,cViewParam);
        return ll;
    }

    public RelativeLayout getGestureLayout(){
        rl = new RelativeLayout(ac);
        tvHint = new TextView(ac);
        tvRepeat = new TextView(ac);
        ivPhoto = new ImageView(ac);

        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(rlParam);

        RelativeLayout.LayoutParams tvHintParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,//width
                RelativeLayout.LayoutParams.WRAP_CONTENT);//height
        tvHintParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tvHintParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);

//		tvHint.setGravity(Gravity.TOP | Gravity.CENTER);
        tvHint.setGravity(Gravity.CENTER);
        tvHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        tvHint.setBackgroundColor(Color.parseColor("#E8EDB7"));
        tvHint.setText("Please, draw your doodle!!");

        RelativeLayout.LayoutParams tvRepeatParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,//width
                RelativeLayout.LayoutParams.WRAP_CONTENT);//height
        tvRepeatParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvRepeatParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        tvRepeat.setGravity(Gravity.CENTER);
        tvRepeat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        tvRepeat.setBackgroundColor(Color.parseColor("#E8EDB7"));

        RelativeLayout.LayoutParams imgViewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,//width
                RelativeLayout.LayoutParams.MATCH_PARENT);//height
        imgViewParam.addRule(RelativeLayout.CENTER_IN_PARENT);

        ivPhoto.setId(9999900);
        tvHint.setId(9999901);
        tvRepeat.setId(9999902);

        rl.addView(ivPhoto, imgViewParam);
        rl.addView(tvHint, tvHintParams);
        rl.addView(tvRepeat, tvRepeatParams);


        return rl;
    }

    public RelativeLayout getResultLayout(){
        rl = new RelativeLayout(ac);
        tvHint = new TextView(ac);
        tvRepeat = new TextView(ac);
        etUserName = new EditText(ac);
        btnSave = new Button(ac);
//		ivPhoto = new ImageView(ac);
        //--------------------relative layout------------------------------------------------

        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(rlParam);
        //----------------- text view top --------------------------------------------------
        RelativeLayout.LayoutParams tvHintParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,//width
                RelativeLayout.LayoutParams.WRAP_CONTENT);//height
        tvHintParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tvHintParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        tvHint.setGravity(Gravity.CENTER);
        tvHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        tvHint.setTextColor(Color.parseColor("#ffdd56"));
        tvHint.setText("Please, Choose User Name:");

        //-------------------edit text field ---------------------------------------------
        RelativeLayout.LayoutParams etNameParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,//width
                RelativeLayout.LayoutParams.WRAP_CONTENT);//height
        etNameParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        etNameParam.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        etUserName.setGravity(Gravity.CENTER);
        etUserName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        etUserName.setId(999909);
        //------------------ btn save -----------------------------------------------------
        RelativeLayout.LayoutParams btnSaveParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,//width
                RelativeLayout.LayoutParams.WRAP_CONTENT);//height
        btnSaveParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        btnSaveParam.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        btnSaveParam.addRule(RelativeLayout.BELOW, etUserName.getId());
        btnSave.setGravity(Gravity.CENTER);
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        btnSave.setText("SAVE");
        btnSave.setId(999908);

        rl.addView(tvHint, tvHintParams);
        rl.addView(etUserName, etNameParam);
        rl.addView(btnSave, btnSaveParam);
//		rl.addView(tvRepeat, tvRepeatParams);

        return rl;
    }

}
