package com.codefactory.devtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.codefactory.devtest.visionface.CameraSourcePreview;
import com.codefactory.devtest.visionface.FaceInterfaces;
import com.codefactory.devtest.visionface.FaceMarkers;
import com.codefactory.devtest.visionface.FaceOverlay;
import com.codefactory.devtest.visionface.FaceVisionUtils;
import com.codefactory.devtest.visionface.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.CameraSource.PictureCallback;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.HashMap;

public class VisionFaceTrackerActivity extends Activity{// implements PicDone{

    private final static String TAG = VisionFaceTrackerActivity.class.getSimpleName();
    private final static int RC_HANDLE_GSM = 9001, RC_HANDLE_CAMERA_PERM = 2;//camera permission request code must be less than 256!
    private CameraSource cameraSource = null;
    private CameraSourcePreview cameraPreview;
    private GraphicOverlay overlay;
    private TextView tvInfo;
    private int width, height;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision_face_tracker);


        FaceVisionUtils.resetFaces();
        FaceVisionUtils.resetSmile();
        //set properly display size;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x; height = size.y;
        Log.d(TAG, "display size W: "+width+" H: "+height);

        cameraPreview = (CameraSourcePreview) findViewById(R.id.visionface_preview);
        overlay = (GraphicOverlay) findViewById(R.id.visionface_overlay);
        tvInfo = (TextView) findViewById(R.id.visionface_text_info);

        tvInfo.setVisibility(View.GONE);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(rc==PackageManager.PERMISSION_GRANTED){
            createCameraSource();
        }else{
            requestCameraPermission(); //TODO: be continue.....
        }

    }

    private void requestCameraPermission(){
        Log.w(TAG, "requesting permission for camera....");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        final Activity suomi = this;
        View.OnClickListener listener = new OnClickListener(){

            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(suomi, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(overlay, R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, listener).show();
    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResults) {
        if(requestCode!=RC_HANDLE_CAMERA_PERM){
            Log.d(TAG, "Got unexpected permission result: "+requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantedResults);
            return;
        }
        if(grantedResults.length!=0 && grantedResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Camera permission granted - start initialize calera source....");
            createCameraSource();
            return;
        }
        Log.d(TAG, "Camera permission not granted: result lenght: "+grantedResults.length+" result code: "+
                (grantedResults.length>0 ? grantedResults[0] : "empty") );

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This App o_O");
        builder.setMessage(R.string.no_camera_permission);
        builder.setPositiveButton(R.string.ok, listener);
        builder.show();
    }



    private void createCameraSource(){

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(true)
                .setMinFaceSize(0.35f)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE).build();

        detector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());

        if(!detector.isOperational()){
            Log.w(TAG, "Face detector dependenicies are not yet avaliable!");
        }
        cameraSource = new CameraSource.Builder(this, detector).setRequestedPreviewSize(width, height)
                .setFacing(CameraSource.CAMERA_FACING_FRONT).setRequestedFps(30f).build();

    }

    @Override
    protected void onResume(){
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraPreview.stop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cameraSource!=null) {
            cameraSource.release();;
            cameraSource=null;
        }
    }

    private void startCameraSource(){

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(code!=ConnectionResult.SUCCESS){
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GSM);
            dlg.show();
        }
        if(cameraSource!=null){
            try {
                cameraPreview.start(cameraSource, overlay);

            } catch (IOException e) {
                Log.e(TAG, "unable to start camera source: "+e.getMessage());
                cameraSource.release();
                cameraSource=null;
            }
        }
    }

    private class SensiFaceTracker implements MultiProcessor.Factory<Face>{

        @Override
        public Tracker<Face> create(Face arg0) {
            return null;
        }

    }

    private class BioExctractor extends Tracker<Face>{

        private FaceVisionUtils mFaceUtils;
        private FaceOverlay mOverlay;
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>{

        @Override
        public Tracker<Face> create(Face arg0) {
            // TODO Auto-generated method stub
            return new GraphicFaceTracker(overlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face>{
        private GraphicOverlay mOverlay;
        private FaceMarkers mGraphic;
        private HashMap<Integer, PointF> map;
        private double[] bio;
        private final FaceInterfaces.SmileEvent event = new FaceInterfaces.SmileEvent(){

            @Override
            public void smiling(boolean isSmile) {
                if(isSmile){
                    if(FaceVisionUtils.landmarkValidator(VisionFaceTrackerActivity.this)){
                        map = FaceVisionUtils.getBioLandmark();
                        bio = FaceVisionUtils.extracSensiBio(map);
                        for(double d: bio){
                            Log.i(TAG, "Biometric: "+d);
                        }
                        //extract biometric from landmarks hashMap and pass it on
                        Toast.makeText(getApplicationContext(), "it is smiling!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VisionFaceTrackerActivity.this, DisplayFaceActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        Log.d(TAG, "didn't found full landmark, try agian....");
                    }
                }
                // this is working shit, just try do the same in FaceMarker!
//				cameraPreview.setSave(done);
                // stuff below was used to take pic (in CameraSourcePreview) when smile was detected (in FaceMarker)
                // now picture is taken with camerasource and canvas in FaceMarker and here is only time to finish activity
//				mGraphic.setSaveFace(done);
            }
        };

        private final FaceInterfaces.PicDone done = new FaceInterfaces.PicDone(){

            @Override
            public void isSaved(boolean status) {
                Log.d(TAG, "pic is saved!");
                Intent intent = new Intent(VisionFaceTrackerActivity.this, DisplayFaceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        };

        GraphicFaceTracker(GraphicOverlay overlay){
            this.mOverlay = overlay;

//			this.mGraphic = new FaceMarkers(cameraSource, event, done, overlay);
            this.mGraphic = new FaceMarkers(cameraSource, event, overlay);
        }

        @Override
        public void onDone() {
            this.mOverlay.remove(mGraphic);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detections) {
            this.mOverlay.remove(mGraphic);
        }

        @Override
        public void onNewItem(int id, Face item) {
            this.mGraphic.setId(id);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detections, Face item) {
            this.mOverlay.add(mGraphic);
            this.mGraphic.updateFace(item);
        }


    }

    private class SavePic extends AsyncTask<byte[],Void, Void>{

        @Override
        protected Void doInBackground(byte[]... params) {
            Log.d(TAG, "start async task....");
            FaceVisionUtils.setByteFace(params[0]);
            if(cameraSource!=null) {
                cameraSource.release();;
                cameraSource=null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "post extecite of async....");
        }
    }


    private PictureCallback pictureCallback = new CameraSource.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0) {
            if(arg0==null){
                Log.d(TAG, "picture callback - byte[] is NULL: "+(arg0==null));
            }else{
                Log.d(TAG, "picture callback!");
                Log.d(TAG, "picture callback - byte[] size: "+arg0.length);
                new SavePic().execute(arg0);
            }

        }
    };


//	@Override
//	public void isSaved(boolean status) {
//		if(status){
//			Log.d(TAG, "pic is saved!");
//			Intent intent = new Intent(VisionFaceTrackerActivity.this, DisplayFaceActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			finish();
//		}
//
//	}
}
