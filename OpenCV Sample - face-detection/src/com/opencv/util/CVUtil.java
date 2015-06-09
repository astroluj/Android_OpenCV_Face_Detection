package com.opencv.util;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class CVUtil {

	// ImageSize
	public static final int IMAGE_LENGTH = 20 ;
	public static final Size IMAGE_SIZE = new Size (20, 20) ;
	public static final Size ROI_FACE_SIZE = new Size (200, 200) ;
	
	// Face Ractangle Color
	public static final Scalar FACE_RECT_COLOR = new Scalar(255, 255, 0, 255); // is Yellow
}
