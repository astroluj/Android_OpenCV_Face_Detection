package com.opencv.camera;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import com.opencv.face.FaceDetectionArea;
import com.opencv.skincolor.SkinColorDetection;
import com.opencv.util.CVUtil;
import com.opencv.util.MenuValues;
import com.opencv.util.MenuUtil;

import android.content.Context;
import android.util.Log;

public class CvCameraControll implements CvCameraViewListener2 {

	private static final String TAG = "PCA::CameraControll";

	// Custom Class
	private FaceDetectionArea faceDetectionArea ;
	private SkinColorDetection skinColorDetection ;
	private LoaderCallback  loaderCallback ;
	private MenuValues menuValues ;
	
	private Mat searchMat, similarMat ;
	
	private Context context ;
    
	public CvCameraControll(Context context, MenuValues menuValues,
			CameraBridgeViewBase  openCvCameraView) {
		
		this.context = context ;
		this.menuValues = menuValues ;
		
		this.loaderCallback = new LoaderCallback (this.context, menuValues, 
				openCvCameraView) ;
		
		faceDetectionArea = new FaceDetectionArea (this.menuValues) ;
	}
	
	// FaceDetectionArea Get
	synchronized public Rect[] getFacesArray () {
		return faceDetectionArea.getFacesArray() ;
	}
	// FaceDetectionArea SearchFace GetSet
	synchronized public Mat getSearchFaceMat (Rect faceArray) {
		return faceDetectionArea.getMatRGBAlpha().clone().submat(faceArray) ;
	}
	synchronized public void setSearchFaceMat (Mat searchMat) {
		
		try {
			this.searchMat = searchMat ;
			Imgproc.resize(this.searchMat, this.searchMat, CVUtil.ROI_FACE_SIZE) ;
		} catch (NullPointerException e) {}
	}
	
	// FaceDetectionArea SimilarFace GetSet
	synchronized public Mat getSimilarFaceMat () {
		return this.similarMat ;
	}
	synchronized public void setSimilaFaceMat (Mat similarMat) {
		
		try {
			this.similarMat = similarMat ;
			Imgproc.resize(this.similarMat, this.similarMat, CVUtil.ROI_FACE_SIZE) ;
		} catch (NullPointerException e) {}
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
		
		// exist faces
		if (!(facesArray == null || facesArray.length == 0)
				&& menuValues.getSkinColorDetectionState() == MenuUtil.START_DETECTION) {
			// Finding skin colors
			// Init case
			if (skinColorDetection == null)
				skinColorDetection = new SkinColorDetection(faceDetectionArea.getMatRGBAlpha(), this.menuValues) ;
			
			return skinColorDetection.skinColorDetection(facesArray) ;
		}
		// else case and non-exist faces
		else {
			// Rectangle draw near faces 
 			for (Rect faceArray : facesArray) {
 				Core.rectangle(faceDetectionArea.getMatRGBAlpha(),
 						faceArray.tl(), faceArray.br(),
 						CVUtil.FACE_RECT_COLOR, CVUtil.LINE_WIDTH);
 			}
 			// SearchMat shows
 			if (searchMat != null) {
 				Mat roi = faceDetectionArea.getMatRGBAlpha()
 						.submat(0, searchMat.rows(), 0, searchMat.cols()) ;
 				searchMat.copyTo(roi) ;
 				
 				// SimilarMat Shows
 	 			if (similarMat != null) {
 	 				Log.d (TAG, "A") ;
 	 				roi = faceDetectionArea.getMatRGBAlpha()
 	 						.submat(0, similarMat.rows(), similarMat.cols(),
 	 								similarMat.cols() + similarMat.cols()) ;
 	 				similarMat.copyTo(roi) ;
 	 			}
 			}
 			
 			return faceDetectionArea.getMatRGBAlpha() ;
		}
	}
	
	// Return BaseLoaderCallback
	public BaseLoaderCallback getLoaderCallback () {
		return this.loaderCallback.getBaseLoaderCallback () ;
	}
}
