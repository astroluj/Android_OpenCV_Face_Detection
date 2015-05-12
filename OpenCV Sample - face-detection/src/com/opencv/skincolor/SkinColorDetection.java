package com.opencv.skincolor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import com.opencv.util.MenuValues;
import com.opencv.util.util;

public class SkinColorDetection extends RGB {

	private static final String TAG = "OpenCV::SkinColorDetection";
	
	// Custom Class
	private ColorBlobDetector colorDetector ;
	private MenuValues mevalues ;
	
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
	     			
	     	
			/*Core.inRange(this.matRGBAlpha,
					new Scalar (0, 133, 77), new Scalar (255, 173, 127),
					this.matRGBAlpha);
			// SkinColor Maps
			byte[] gaussianR = new byte[util.SKIN_MAP_SIZE],
					gaussianG = new byte[util.SKIN_MAP_SIZE] ;
				
			gaussianProcess (gaussianR,
					avgColor.val[0] -5, avgColor.val[0] + 5,
					util.SKIN_DEV_R * util.SKIN_DEV_R) ;
			gaussianProcess (gaussianG,
					avgColor.val[1] -5, avgColor.val[1] + 5,
					util.SKIN_DEV_G * util.SKIN_DEV_G) ;
			// NormalizationRGB
			normalizationRGB (matSkinColor) ;
			// ConvertToGrayScale
			convertToGrayScale (gaussianR, gaussianG) ;*/
		} catch (Exception e) {}
		
		return this.matRGBAlpha ;
	}
	
	// ConvertToGrasyScale
	private boolean convertToGrayScale (byte[] gaussianR, byte[] gaussianG) {
		try {
			//skin color 지역을 gray image로 바꿔주는 작업 
			for (int i = 0, rows = this.matRGBAlpha.rows() ; i < rows ; i++) {
				for (int j = 0, cols = this.matRGBAlpha.cols() ; j < cols ; j++) {
					int[] data = new int[1] ;
					data[0] = (gaussianR[(int) this.getNormalizeR(i,  j)] & 0xff)
							* (gaussianG[(int) this.getNormalizeG(i,  j)] & 0xff) / 255 ;

					//this.matGray.put(i, j, data) ;
				}
			}
		} catch (Exception e) {
			return false ;
		}
		
		return true ;
	}
	
	// Normalization RGB
	private void normalizationRGB (Mat matRoi) {
		
		// RGB -> norR, norG, norB
		
		for (int i = 0, height = matRoi.height() ; i < height ; i++) {
			for (int j = 0, width = matRoi.width() ; j < width ; j++) {
				
				// R + G + B
				Scalar scalarColors = Core.sumElems (this.matRGBAlpha.submat(new Rect (j, i, 1, 1))) ;
				double sumRGB = scalarColors.val[0] +  scalarColors.val[1] + scalarColors.val[2] ;
				
				if  (sumRGB > util.SUM_RGB) 
					this.setNormalizeRGB(i, j,
							// R
							(util.MAX_SCALAR * scalarColors.val[0] / sumRGB),
							// G
							(util.MAX_SCALAR * scalarColors.val[1] / sumRGB),
							// B
							(util.MAX_SCALAR * scalarColors.val[2] / sumRGB)) ;
					
				else // sumRGB <= util.SUM_RGB
					this.setNormalizeRGB(i, j, 0, 0, 0) ;
			}
		}
	}
	
	// Gaussian Processing 
	private void gaussianProcess (byte[]gaussianColor,
			double skinLow, double skinHigh, double dev) {

		double[] temp = new double[util.SKIN_MAP_SIZE] ;

		for (int i = 0 ; i < util.SKIN_MAP_SIZE ;  i++) {
			if (i < skinLow)
				temp[i] = Math.exp(-1. * ((double)i - skinLow) * ((double)i - skinLow) / dev) ;
			else if (i >= skinLow && i <= skinHigh)
				temp[i] =  1.0 ;
			else
				temp[i] = Math.exp(-1. * ((double)i - skinHigh) * ((double)i - skinHigh) / dev) ;
		}

		double min = 1.0 ;
		double max = 0.0 ;	

		for (int i = 0 ; i < util.SKIN_MAP_SIZE ; i++) {
			if ( temp[i] < min ) min = temp[i] ;
			if ( temp[i] > max )	max = temp[i] ;
		}

		double mag = max - min;

		for(int i = 0 ; i < util.SKIN_MAP_SIZE ; i++) 
			gaussianColor[i] = (byte)((temp[i] - min) / mag * 255) ;
	}
}
