package com.codefactory.devtest.visionface;

/**
 * Created by kudlaty on 13/10/2016.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {

    private static final String TAG = CameraSourcePreview.class.getSimpleName();
    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested, mSurfaceAvailable;
    private CameraSource mCameraSource;
    private GraphicOverlay mOverlay;
    //	private SavePicture save;
    private FaceInterfaces.PicDone picDone;


    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext=context;
        this.mStartRequested=false;
        this.mSurfaceAvailable=false;
        this.mSurfaceView = new SurfaceView(context);
        this.mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(this.mSurfaceView);
    }

    //working shit, just try do the same in FaceMarker
    public void setSave(final FaceInterfaces.PicDone done) {
        this.picDone=done;
//		this.save = save;
        if(mCameraSource!=null){
            Log.w(TAG, "try to save in camerasource preview");
            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] arg0) {
                    Log.d(TAG, "picture callback!");
                    if(arg0!=null) Log.d(TAG, "picture callback - byte[] size: "+arg0.length);
                    else Log.d(TAG, "picture callback - byte[] is NULL!");
                    FaceVisionUtils.setByteFace(arg0);
                    done.isSaved(true);

                }} );

        }else Log.w(TAG, "takePicture(): camera source is null");
    }

    public int getSurfaceWidth(){
        return mSurfaceView.getWidth();
    }
    public int getSurfaceHeight(){
        return mSurfaceView.getHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 240, height = 320;

        if (mCameraSource != null)
        {
            Size size = mCameraSource.getPreviewSize();
            if (size != null)
            {
                width = size.getWidth();
                height = size.getHeight();
                Log.d(TAG, "onLauout camera source W: "+width+" H: "+height);
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode())
        {
            int tmp = width;

            //noinspection SuspiciousNameCombination
            width = height;
            height = tmp;
            Log.d(TAG, "onLauout BUM! PORTRAIT MODE camera source W: "+width+" H: "+height);
        }
        // original size from screen lauout,
        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;


        Log.d(TAG, "onLayout 1 LAUOUT W: "+layoutWidth+ "H: "+layoutHeight);
        // Computes height and width for potentially doing fit width.
//	    int childWidth = layoutWidth;
//	    int childHeight = (int) (((float) layoutWidth / (float) width) * height);

// let's swich it to match camera to screen/lauout height - we will crop weight (outside screen) late
        int childHeight = layoutHeight;// (int) (((float) layoutWidth / (float) width) * height);
        int childWidth = layoutWidth;


        for (int i = 0; i < getChildCount(); ++i)
        {
            getChildAt(i).layout(0, 0, layoutWidth, childHeight);// temporary switch childHeight with layoutHeight, but WORKS!
        }
        try {
            startIfReady();
            Log.d(TAG, "onLayout 2 LAYOUT W: "+layoutWidth+ " H: "+layoutHeight);
            Log.d(TAG, "onLayout LAYOUT CHILD W: "+childHeight+ " H: "+childHeight);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source: "+e.getMessage());
        }



    }// END onLayout()------------------

    private boolean isPortraitMode(){

        int orientation = this.mContext.getResources().getConfiguration().orientation;
        if(orientation==Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG, "detected Portrait orientation");
            return true;
        }
        if(orientation==Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "detected Landscape orientation");
            return false;
        }

        Log.i(TAG, "cano't detecte screen orientation o_O");
        return false;
    }

    public void start(CameraSource source) throws IOException{
        if(source==null) stop();
        mCameraSource = source;
        if(mCameraSource!=null){
            this.mStartRequested=true;
            startIfReady();
        }
    }

    public void start(CameraSource source, GraphicOverlay overlay) throws IOException{
        this.mOverlay=overlay;
        start(source);
    }

    public void stop(){
        if(this.mCameraSource!=null) this.mCameraSource.stop();
    }

    public void release(){
        if(this.mCameraSource!=null){
            this.mCameraSource.release();
            this.mCameraSource=null;
        }
    }

    public void startIfReady() throws IOException{
        if(this.mStartRequested && this.mSurfaceAvailable){
            this.mCameraSource.start(this.mSurfaceView.getHolder());
            if(mOverlay!=null){

                int mini, max;
                Size size = mCameraSource.getPreviewSize();

                mini = Math.min(size.getWidth(), size.getHeight());
                max = Math.max(size.getWidth(), size.getHeight());


                Log.d(TAG, "startIfReady Mini: "+mini+" max: "+max);
                if(isPortraitMode()) mOverlay.setCameraInfo(mini, max, mCameraSource.getCameraFacing());
                else mOverlay.setCameraInfo(max, mini, mCameraSource.getCameraFacing());
                Log.d(TAG, "startIfReady Overlay View H: "+mOverlay.getHeight()+" W: "+mOverlay.getWidth());
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }



    private class SurfaceCallback implements SurfaceHolder.Callback{ //TODO implement call to save surface view here!

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceAvailable = true;

            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source: "+e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceAvailable = false;
        }

    }


//	private PictureCallback pictureCallback = new CameraSource.PictureCallback() {
//
//		@Override
//		public void onPictureTaken(byte[] arg0) {
//			if(arg0==null){
//				Log.d(TAG, "picture callback - byte[] is NULL: "+(arg0==null));
//			}else{
//				Log.d(TAG, "picture callback!");
//				Log.d(TAG, "picture callback - byte[] size: "+arg0.length);
////				new SavePic().execute(arg0);
//				FaceVisionUtils.setByteFace(arg0);
//				savePic.isSaved(true);
//			}
//
//		}
//	};

//	@Override
//	public void isSaved(boolean status) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void saveIt(boolean readyToDo) {
//		if(readyToDo){
//			if(mCameraSource!=null){
//				try{
//					mCameraSource.start(mSurfaceView.getHolder());
//					mCameraSource.takePicture(null,pictureCallback );
//				}catch(RuntimeException | IOException ex){
//					Log.e(TAG, "take pic: "+ex.getMessage());
//					ex.printStackTrace();
//				}finally{
//					Log.d(TAG, "bye, bye from saveIt");
//				}
//			}else Log.w(TAG, "takePicture(): camera source is null");
//		}
//
//	}



//	@Override
//	public void smiling(boolean isSmile) {
//		// TODO Auto-generated method stub
//				if(isSmile) {  //create bitmap from camera source, release camera source and finish activity
////					Toast.makeText(getApplicationContext(), "it is smiling!", Toast.LENGTH_SHORT).show();
//					Log.d(TAG, "is smiling!");
//					try {
//						mCameraSource.start(mSurfaceView.getHolder());
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if(mCameraSource!=null){
//						mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
//
//							@Override
//							public void onPictureTaken(byte[] arg0) {
//								Log.d(TAG, "picture callback!");
//								if(arg0!=null) Log.d(TAG, "picture callback - byte[] size: "+arg0.length);
//								else Log.d(TAG, "picture callback - byte[] is NULL!");
//								FaceVisionUtils.setByteFace(arg0);
//								savePic.isSaved(true);
//
//							}} );
//					}else Log.w(TAG, "takePicture(): camera source is null");
//					mCameraSource.release();
//					mCameraSource=null;
//
//				}
//
//	}

//	@Override
//	public void saveIt(boolean readyToDo) {
//
//		if(readyToDo) {  //create bitmap from camera source, release camera source and finish activity
////			Toast.makeText(getApplicationContext(), "it is smiling!", Toast.LENGTH_SHORT).show();
//			Log.d(TAG, "is smiling!");
//			try {
//				mCameraSource.start();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if(mCameraSource!=null){
//				mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
//
//					@Override
//					public void onPictureTaken(byte[] arg0) {
//						Log.d(TAG, "picture callback!");
//						if(arg0!=null) Log.d(TAG, "picture callback - byte[] size: "+arg0.length);
//						else Log.d(TAG, "picture callback - byte[] is NULL!");
//						FaceVisionUtils.setByteFace(arg0);
//						done.isSaved(true);
//
//					}} );
//
//			}else Log.w(TAG, "takePicture(): camera source is null");
//			mCameraSource.release();
//			mCameraSource=null;
//
//		}
//
//	}





}

