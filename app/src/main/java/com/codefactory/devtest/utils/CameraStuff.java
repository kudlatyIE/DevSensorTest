package com.codefactory.devtest.utils;

/**
 * Created by kudlaty on 13/10/2016.
 */

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;

import java.util.ArrayList;

public class CameraStuff {

    private Context context;
    private Sensor sensor;
    private Camera camera;
    private CameraInfo info;
    private int camNumber, CameraId, height, width;
    private String cameraName;
    public Xyz xy;

    public CameraStuff(Context context){
        this.context=context;
    }

    CameraStuff(int id, String name, int h, int w){
        this.CameraId=id;
        this.cameraName=name;
        this.width=w;
        this.height=h;
    }

    @SuppressWarnings("deprecation")
    public ArrayList<CameraStuff> getCamerasList(){
        ArrayList<CameraStuff> list = new ArrayList<>();
        camNumber = Camera.getNumberOfCameras();
        System.out.println("Camera num: "+camNumber);
        for (int i=0;i<camNumber;i++){
            camera=null;
            cameraName="unknown";
            camera = Camera.open(i);
            info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if( info.facing==CameraInfo.CAMERA_FACING_BACK) cameraName="BACK";
            if( info.facing==CameraInfo.CAMERA_FACING_FRONT) cameraName="FRONT";
//			list.add(new CameraStuff(i,cameraName, camera));
            try{
                width = camera.getParameters().getPictureSize().width;
                height = camera.getParameters().getPictureSize().height;
            }catch(Exception ex){
                System.out.println("Error get pisture size.....");
            }
            list.add(new CameraStuff(i,cameraName, height, width));
            camera.release(); //camera=null;
        }
        return list;
    }

    //	private Camera getFrontCam(){
    public Xyz getFrontCamXy(){
        Camera c=null;
        CameraInfo ci=null;
        int w=0,h=0;
        try{
            c=Camera.open(1);
            ci= new CameraInfo();
            if(ci.facing==CameraInfo.CAMERA_FACING_FRONT){
                w=c.getParameters().getPictureSize().width;
                h=c.getParameters().getPictureSize().height;
                this.xy = new Xyz(w,h);
                c.release();
                c=null;
                return xy;
//				return c;
            }
        }catch(Exception ex){
            System.out.println("No front camera!");
            ex.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static int getFrontCameraId() throws Exception{
        Camera camera;
        CameraInfo info;
        int id=0;
        ArrayList<Integer> list = new ArrayList<Integer>();
        int camNumber = Camera.getNumberOfCameras();
        System.out.println("Camera num: "+camNumber);
        for (int i=0;i<camNumber;i++){
//			camera=null;
//			camera = Camera.open(i);
            info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if( info.facing==CameraInfo.CAMERA_FACING_FRONT) id=i;
        }
        if (id!=0) return id;
        else throw new Exception("No front camera detected!");
    }

    public Camera getCamera() {
        return camera;
    }
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    public int getCameraId() {
        return CameraId;
    }
    public void setCameraId(int cameraId) {
        CameraId = cameraId;
    }
    public String getCameraName() {
        return cameraName;
    }
    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }


}
