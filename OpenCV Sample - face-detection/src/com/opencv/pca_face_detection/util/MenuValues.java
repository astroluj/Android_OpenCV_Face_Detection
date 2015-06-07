package com.opencv.pca_face_detection.util;

import org.opencv.objdetect.CascadeClassifier;

import com.opencv.face.DetectionBasedTracker;

public class MenuValues {

	// Detection Start or Stop
 	private int faceDetectionState, skinColorDetectionState ;
	// Detector Type
 	private int detectorType ;
	 	
	// Detectors
	private CascadeClassifier javaDetector;
	private DetectionBasedTracker nativeDetector;
	
	private float relativeFaceSize ;
	private int absoluteFaceSize ;
	
	public MenuValues () {
		faceDetectionState = util.STOP_DETECTION ;
    	skinColorDetectionState = util.STOP_DETECTION ;
		detectorType = util.ENABLED_NATIVE ;
		
		relativeFaceSize = 0.2f ;
		absoluteFaceSize = 0 ;
	}
	
	// Face Detection state GetSet
	public int getFaceDetectionState () {
		return this.faceDetectionState ;
	}
	synchronized public void setFaceDetectionState (int faceDetectionState) {
		this.faceDetectionState =  faceDetectionState ;
	}
	
	// Skin Color Detection State GetSet
	public int getSkinColorDetectionState () {
		return this.skinColorDetectionState ;
	}
	synchronized public void setSkinColorDetectionState (int skinColorDetectionState) {
		this.skinColorDetectionState = skinColorDetectionState ;
	}
	
	// DetectorType GetSet
	public int getFaceDetectorType () {
		return this.detectorType ;
	}
	synchronized public void setFaceDetectorType (int detectorType) {
		this.detectorType = detectorType ;
	}
	
	// JavaDetector Constructor
	public void constructJavaDetector (String fileName) {
		this.javaDetector = new CascadeClassifier(fileName) ;
	}
	// JavaDetector GetSet
	public CascadeClassifier getJavaDetector () {
		return this.javaDetector ;
	}
	synchronized public void setJavaDetector (CascadeClassifier javaDetector) {
		this.javaDetector = javaDetector ;
	}
	
	// NativeDetector Constructor
	public void constructNativeDetector (String cascadeName, int minFaceSize) {
		this.nativeDetector = new DetectionBasedTracker(cascadeName, minFaceSize) ;
	}
	// NativeDetector GetSet
	public DetectionBasedTracker getNativeDetector () {
		return this.nativeDetector ;
	}
	synchronized public void setNativeDetector (DetectionBasedTracker nativeDetector) {
		this.nativeDetector = nativeDetector ;
	}
	
	// nativeDetector Start or Stop
	synchronized public void startNativeDetector () {
		try {
			this.nativeDetector.start () ;
		} catch (Exception e) {}
	}
	synchronized public void stopNativeDetector () {
		try {
			this.nativeDetector.stop () ;
		} catch (Exception e) {}
	}
	
	// relativeFaceSize GetSet
	public float getRelativeFaceSize () {
		return this.relativeFaceSize ;
	}
	synchronized public void setRelativeFaceSize (float relativeFaceSize) {
		this.relativeFaceSize = relativeFaceSize ;
	}
	
	// absoluteFaceSize GetSet
	public int getAbsoluteFaceSize () {
		return this.absoluteFaceSize ;
	}
	synchronized public void setAbsoluteFaceSize (int absoluteFaceSize) {
		this.absoluteFaceSize = absoluteFaceSize ;
	}
}
