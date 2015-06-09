package com.opencv.skincolor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import com.opencv.util.MenuValues;

public class SkinColorDetection extends RGB {

	private static final String TAG = "PCA::SkinColorDetection";
	
	// Custom Class
	private ColorBlobDetector colorDetector ;
	
	private Mat matRGBAlpha;
	
	public SkinColorDetection (Mat matRGBAlpha, MenuValues menuValues) {
		super (matRGBAlpha.size()) ;
		Log.i (TAG, "Constructor") ;
		
		colorDetector = new ColorBlobDetector(menuValues) ;
		this.matRGBAlpha = matRGBAlpha ;
	}
	
	public Mat skinColorDetection (Rect[] facesArray) {

		try {
			final int SIZE = facesArray.length ;
			
			Scalar[] avgColor = new Scalar[SIZE] ;
			Mat matHsv = new Mat () ;
			Imgproc.cvtColor(this.matRGBAlpha, matHsv, Imgproc.COLOR_BGR2HSV_FULL) ;
			
			// get avg faces color
			for (int i = 0 ; i < SIZE ; i++) {
				
				// ROI (FaceS)
				Mat matRoi = new Mat (matHsv, facesArray[i]) ;
				
				// Average Colors (FACES)
				avgColor[i] = Core.sumElems(matRoi) ;
				
				int pointCount = facesArray[i].width * facesArray[i].height ;
		        for (int j = 0, size = avgColor[i].val.length ; j < size ; j++)
		        	avgColor[i].val[j] /= pointCount ;
		        
		        // Release
		        matRoi.release() ;
			}
			matHsv.release() ;
			
			double[] RGBA =new double[4] ;
			for (Scalar scalar : avgColor) {
				for (int i = 0, size = scalar.val.length ; i < size ; i++) {
					RGBA[i] += scalar.val[i] ;
				}
			}
			
			Scalar HsvColor = new Scalar(RGBA[0] / SIZE, RGBA[1] / SIZE, RGBA[2] / SIZE, RGBA[3] / SIZE) ;

			// Colors detection
	        colorDetector.setHsvColor(HsvColor) ;

	        // Mat Mask
	        colorDetector.process(this.matRGBAlpha) ;
		} catch (Exception e) {}
		
		return this.matRGBAlpha ;
	}
}
