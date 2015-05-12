package com.opencv.camera;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import com.opencv.face.FaceDetectionArea;
import com.opencv.skincolor.SkinColorDetection;
import com.opencv.util.MenuValues;

import android.content.Context;
import android.util.Log;

public class CvCameraControll implements CvCameraViewListener2 {

	private static final String TAG = "OpenCV::CameraControll";

	// Custom Class
	private FaceDetectionArea faceDetectionArea ;
	private SkinColorDetection skinColorDetection ;
	private LoaderCallback  loaderCallback ;
	private MenuValues menuValues ;
	
	private Context context ;
    
	public CvCameraControll(Context context, MenuValues menuValues,
			CameraBridgeViewBase  openCvCameraView) {
		
		this.context = context ;
		this.menuValues = menuValues ;
		
		this.loaderCallback = new LoaderCallback (this.context, menuValues, 
				openCvCameraView) ;
		
		faceDetectionArea = new FaceDetectionArea (this.menuValues) ;
	}
	
	public void onCameraViewStarted(int width, int height) {
		Log.i(TAG, "onCameraViewStarted");
		// Construct Gray Scale
		faceDetectionArea.constructMatGray();
		// Construct RGB Scale
		faceDetectionArea.constructMatRGBAlpha();
	}

	public void onCameraViewStopped() {
		Log.i(TAG, "onCameraViewStopped");
		// play after Call OnPause
		faceDetectionArea.getMatGray().release();
		faceDetectionArea.getMatRGBAlpha().release(); 
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// Set Frame Scales
		faceDetectionArea.setMatGray(inputFrame.gray()) ;
		faceDetectionArea.setMatRGBAlpha(inputFrame.rgba()) ;
		
		// 얼굴 찾기
		Rect[] facesArray = faceDetectionArea.FaceDetection() ;
		// non-exist faces
		if (facesArray == null || facesArray.length == 0)
			return faceDetectionArea.getMatRGBAlpha() ;
		// exist faces
		else {
			// Finding skin colors
			// Init case
			if (skinColorDetection == null)
				skinColorDetection = new SkinColorDetection(faceDetectionArea.getMatRGBAlpha(), this.menuValues) ;
			
			return skinColorDetection.skinColorDetection(facesArray) ;
		}
	}
	
	// Return BaseLoaderCallback
	public BaseLoaderCallback getLoaderCallback () {
		return this.loaderCallback.getBaseLoaderCallback () ;
	}
}