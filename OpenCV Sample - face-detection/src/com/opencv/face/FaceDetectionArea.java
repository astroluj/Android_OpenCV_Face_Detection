package com.opencv.face;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import com.opencv.pca_face_detection.util.MenuValues;
import com.opencv.pca_face_detection.util.util;

import android.util.Log;

public class FaceDetectionArea {

	private static final String TAG = "OpenCV::FaceDetectionArea";
	
	// Custom Class
	private MenuValues menuValues ;
	
	private Mat matRGBAlpha;
	private Mat matGray;
	
	public FaceDetectionArea (MenuValues menuValues) {
		
		this.menuValues = menuValues ;
	}

	// Constructor matRGBAlpha
	synchronized public void constructMatRGBAlpha () {
		this.matRGBAlpha =  new Mat () ;
	}
	
	// matRGBAlpha GetSet
	synchronized public Mat getMatRGBAlpha () {
		return this.matRGBAlpha ;
	}
	synchronized public void setMatRGBAlpha (Mat matRGBAlpha) {
		this.matRGBAlpha = matRGBAlpha ;
	}
	
	// Constructor matGray
	synchronized public void constructMatGray () {
		this.matGray =  new Mat () ;
	}
		
	// matGray GetSet
	synchronized public Mat getMatGray () {
		return this.matGray ;
	}
	synchronized public void setMatGray (Mat matGray) {
		this.matGray = matGray ;
	}
	
	// Face Detection
	public Rect[] FaceDetection () {
		
		try {
			if (menuValues.getAbsoluteFaceSize() == 0) {
				int height = matGray.rows();
				
				if (Math.round(height * menuValues.getRelativeFaceSize()) > 0) {
					
					menuValues.setAbsoluteFaceSize(Math.round(height *
							menuValues.getRelativeFaceSize())) ;
				}
				menuValues.getNativeDetector().setMinFaceSize(menuValues.getAbsoluteFaceSize());
			}
	
			// Rectangles
			MatOfRect faces = new MatOfRect();
	
			int detectorType = menuValues.getFaceDetectorType() ;
			// isJavaDetector
			if (detectorType == util.ENABLED_JAVA) {
				if (menuValues.getJavaDetector () != null)
					menuValues.getJavaDetector ().detectMultiScale(matGray,
							faces,
							1.1,
							2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
							new Size(menuValues.getAbsoluteFaceSize (),
									menuValues.getAbsoluteFaceSize ()),
							new Size());
			} 
			// isNativeDetector
			else if (detectorType == util.ENABLED_NATIVE) {
				if (menuValues.getNativeDetector () != null)
					menuValues.getNativeDetector ().detect(matGray, faces) ;
			} 
			// Exception
			else {
				Log.e(TAG, "Detection method is not selected!");
			}
	
			// faces list
			Rect[] facesArray = faces.toArray() ;
			
			// Rectangle draw near faces 
 			/*for (Rect faceArray : facesArray) {
 				Core.rectangle(this.matRGBAlpha,
 						faceArray.tl(), faceArray.br(),
 						util.FACE_RECT_COLOR, 3);
 			}*/
 			
			return facesArray ;
		} catch (Exception e) {}
		
		return null ;
	}
}
