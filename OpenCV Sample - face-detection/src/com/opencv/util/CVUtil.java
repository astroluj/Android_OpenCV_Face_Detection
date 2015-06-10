package com.opencv.util;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class CVUtil {

	// Line width
	public static final int LINE_WIDTH = 3 ;
	// ImageSize
	public static final int IMAGE_LENGTH = 32 ;
	public static final Size IMAGE_SIZE = new Size (32, 32) ;
	public static final int ROI_FACE_LENGTH = 200 ;
	public static final Size ROI_FACE_SIZE = new Size (200, 200) ;
	
	// DBL_MAX
	public static final double DBL_MAX = 1.7976931348623158e+308 ;
	
	// Face Ractangle Color
	public static final Scalar FACE_RECT_COLOR = new Scalar(255, 255, 0, 255); // is Yellow
}
