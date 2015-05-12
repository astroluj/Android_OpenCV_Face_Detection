package com.opencv.util;

import org.opencv.core.Scalar;

public class util {

	public static final Scalar FACE_RECT_COLOR = new Scalar(255, 255, 0, 255); // is Yellow
	
	public static final int        JAVA_DETECTOR       = 0;
	public static final int        NATIVE_DETECTOR     = 1;
	public static final int			STOP_DETECTION = 0 ;
	public static final int			START_DETECTION = 1 ;
	public static final int			DISABLED_ERODE = 0 ;
	public static final int			ENABLED_ERODE = 1 ;
	public static final int			DISABLED_DILATE = 0 ;
	public static final int			ENABLED_DILATE = 1 ;
	
	
	//skin color에 대한 평균(범위 ) 및 standard deviation
	//여려분은 얼굴 검출 영역에서 자동으로 평균값 추출할 것 
	public static final int SKIN_MAP_SIZE = 256 ;
	public static final int MAX_SCALAR = 255 ;
	
	public static final double SKIN_LOW_R = 103 ;//100
	public static final double SKIN_HIGH_R = 109 ;//102

	public static final double SKIN_LOW_G = 80 ;//94 
	public static final double SKIN_HIGH_G = 86 ;//96

	public static final double SKIN_DEV_R = 10 ;
	public static final double SKIN_DEV_G = 10 ;
	
	public static final double SUM_RGB = 10 ;
}
