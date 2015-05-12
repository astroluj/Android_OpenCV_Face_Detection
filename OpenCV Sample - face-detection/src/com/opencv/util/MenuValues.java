package com.opencv.util;

import org.opencv.objdetect.CascadeClassifier;

import com.opencv.face.DetectionBasedTracker;

public class MenuValues {

	// Detector Type
 	private int detectorType ;
	 	
	// Detectors
	private CascadeClassifier javaDetector;
	private DetectionBasedTracker nativeDetector;
	
	private float relativeFaceSize ;
	private int absoluteFaceSize ;
	private int dilateFilter ;
	private int erodeFilter ;
	
	public MenuValues () {
		detectorType = util.JAVA_DETECTOR ;
		
		relativeFaceSize = 0.2f ;
		absoluteFaceSize = 0 ;
		dilateFilter = 0 ;
		erodeFilter = 0 ;
	}
	
	// DetectorType GetSet
	public int getDetectorType () {
		return this.detectorType ;
	}
	synchronized public void setDetectorType (int detectorType) {
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
	
	// SkinColorFilter GetSet
	public int getDilateFilter () {
		return this.dilateFilter ;
	}
	synchronized public void setDilateFilter (int dilateFilter) {
		this.dilateFilter = dilateFilter ;
	}
	
	// erodeFilter GetSet
	public int getErodeFilter () {
		return this.erodeFilter ;
	}
	synchronized public void setErodeFilter (int erodeFilter) {
		this.erodeFilter = erodeFilter ;
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
