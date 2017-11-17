package com.codefactory.devtest.visionface;

/**
 * Created by kudlaty on 13/10/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;

import com.codefactory.devtest.utils.BitmapUtils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class FaceLandmarker {

    private final static String TAG = FaceLandmarker.class.getSimpleName();
    private FaceDetector detector;
    private Paint landmarkPaint;
    private Context c;
    private Activity ac;
    private Bitmap b=null;

    public FaceLandmarker(Activity activity){
        this.c=activity.getApplicationContext();
        this.ac=activity;
    }

    public  Bitmap addMarks(Bitmap bitmap) throws Exception{


        if(bitmap==null) throw new Exception("addMarks(): bitmap is null!");
        Log.d(TAG, "input bitmap_W: "+bitmap.getWidth()+" bitmap_H: "+bitmap.getHeight());

        //TODO: time to resize bitmap! and flip mirror....

        landmarkPaint = new Paint();
        landmarkPaint.setStrokeWidth(10);
        landmarkPaint.setColor(Color.RED);
        landmarkPaint.setStyle(Paint.Style.STROKE);

        Matrix matrix = new Matrix();
        //create mutable bitmap for canvas
//        matrix.postRotate(90);
        matrix.preScale(-1, 1);
        Bitmap temp = BitmapUtils.resizeBitmapForBiometric(ac, bitmap);
        this.b = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
        Bitmap btmWorking = Bitmap.createBitmap(BitmapUtils.resizeBitmapForBiometric(ac, this.b));
        Bitmap btmMut = btmWorking.copy(Bitmap.Config.ARGB_8888, true);
        Canvas  canvas;
        canvas = new Canvas(btmMut);
        canvas.drawBitmap(btmMut, 0, 0, null);
        detector = new FaceDetector.Builder(c).setTrackingEnabled(false)
                .setProminentFaceOnly(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//							.setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE).build();
        SparseArray<Face> faces;
        Frame frame;
        Face f;
        frame = new Frame.Builder().setBitmap(btmWorking).build();
        faces = detector.detect(frame);
        //if face need to be rotate........ for sony and samsung......

        if(!(faces.size()>0)){//rotate bitmap 90dgr...
            Log.d(TAG, "faces size == 0, neeed to rotate bitmap....");


            matrix.postRotate(90);
//			matrix.preScale(-1, 1);
//			temp = BitmapUtils.resizeBitmapForBiometric(ac, bitmap);// no, no, no.. no for sony anyway!
            // use old original INPUT bitmap, resized on first step above!
            this.b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //generate width and height to fit a screen.....
            Log.d(TAG, "No face::::fixing bitmap_W: "+b.getWidth()+" bitmap_H: "+b.getHeight());
//			this.b = Bitmap.createBitmap(b, 0, 0,b.getHeight(), b.getWidth());
            btmWorking = Bitmap.createBitmap(b);
            btmMut = btmWorking.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(btmMut);
            canvas.drawBitmap(btmMut, 0, 0, null);
            frame = new Frame.Builder().setBitmap(b).build();
            faces = detector.detect(frame);
        }

        float smile, eulerY, eulerZ, faceCenterX, faceCenterY;
        //check point for biometric: bitmap dimensions;
        Log.d(TAG, "ready bitmap_W: "+btmWorking.getWidth()+" bitmap_H: "+btmWorking.getHeight());
        if(faces.size()>0) {
            f = faces.valueAt(0);
            smile = f.getIsSmilingProbability();
            eulerY = f.getEulerY();
            eulerZ = f.getEulerZ();
            Log.d(TAG, "eulerY: "+eulerY +" eulerZ: "+eulerZ);
            if(!FaceVisionUtils.isPoseCorrect(eulerY, eulerZ)) {

                detector.release();
                throw new Exception("Incorrec pose!");
            }

            List<Landmark> myList = f.getLandmarks();
            int[] all = FaceVisionUtils.allLandmarks();
            if(myList.size()!=all.length) {
                detector.release();
                throw new Exception("Missing landmark!");
            }else{
                faceCenterX=f.getPosition().x;//+f.getWidth()/2;
                faceCenterY=f.getPosition().y;//+f.getHeight()/2;
                Log.d(TAG, "faceCenter X: "+faceCenterX+" Y: "+faceCenterY);
                //add face center point to hashMap of collected landmarks! (check if granted than zero, just in case);
                for(Landmark l: myList){

                    PointF p = l.getPosition();
                    canvas.drawCircle(p.x, p.y, 2f, landmarkPaint);
                    FaceVisionUtils.createLandmark(l.getType(), p);// create map of faces landmarks for biometric extractor!
                }
                if(faceCenterX > 0 && faceCenterY > 0 ) FaceVisionUtils.addCenterFace(FaceVisionUtils.FACE_CENTER, faceCenterX, faceCenterY);
                else throw new Exception("not valid face center coordinates: X: "+faceCenterX+" Y: "+faceCenterY);
//				canvas.drawPoint(faceCenterX, faceCenterY, landmarkPaint);
            }
        }else {

            detector.release();
            throw new Exception("No face found!");
        }
        detector.release();
        return btmMut;
    }

}

