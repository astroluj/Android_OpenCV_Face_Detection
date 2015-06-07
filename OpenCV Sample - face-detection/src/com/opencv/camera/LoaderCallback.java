package com.opencv.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;

import com.opencv.R;
import com.opencv.pca_face_detection.util.MenuValues;

import android.content.Context;
import android.util.Log;

public class LoaderCallback extends BaseLoaderCallback {

	private static final String TAG = "OpenCV::LoaderCallback";
	
	// Custom Class
	private MenuValues menuValues ;
	
	private CameraBridgeViewBase  openCvCameraView;
	
	private File cascadeFile;
	private BaseLoaderCallback loaderCallback ;
	
	private Context context ;
	
	public LoaderCallback(Context context, MenuValues menuValues,
			CameraBridgeViewBase  openCvCameraView) {
		super(context);
		
		this.context = context ;
		this.menuValues = menuValues ;
		this.openCvCameraView = openCvCameraView ;
		
		loaderCallback = new BaseLoaderCallback (this.context) {
			
			@Override
		    public void onManagerConnected(int status) {
		        switch (status) {
		            case LoaderCallbackInterface.SUCCESS:
		                Log.i(TAG, "OpenCV loaded successfully");

		                // call SUCCESS Case Fucation
		                loaderCallbackInterfaceSuccess () ;
		               
		                break;
		            default:
		                super.onManagerConnected(status);
		        }
		    }
		} ;
	}
	
	// Return loaderCallback
	public BaseLoaderCallback getBaseLoaderCallback () {
		return this.loaderCallback ;
	}
	
	// case LoaderCallbackInterface.SUCCESS in onManagerConnected
	private void loaderCallbackInterfaceSuccess () {
		// Load native library after(!) OpenCV initialization
        System.loadLibrary("detection_based_tracker");

        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();

            // JavaDetector Create
            menuValues.constructJavaDetector (cascadeFile.getAbsolutePath()) ;
            
            // isEmpty
            if (menuValues.getJavaDetector ().empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                menuValues.setJavaDetector (null) ;
            } 
            else Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());

            // NativeDtector Create
            menuValues.constructNativeDetector(cascadeFile.getAbsolutePath(), 0);
            cascadeDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }

        openCvCameraView.enableView();
	}
}
