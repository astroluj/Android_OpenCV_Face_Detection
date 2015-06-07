package com.opencv.skincolor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.opencv.util.MenuValues;


public class ColorBlobDetector {
	
	// Custom Class
	MenuValues menuValues ;
	
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);

    public ColorBlobDetector (MenuValues menuValues) {
    	this.menuValues = menuValues ;
    }
    
    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;
    }

    public void process(Mat rgbaImage) {
    	if (!rgbaImage.empty()) {
	        Imgproc.cvtColor(rgbaImage, rgbaImage, Imgproc.COLOR_BGR2HSV_FULL);
	        Core.inRange(rgbaImage, mLowerBound, mUpperBound, rgbaImage);
	        
	        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)) ;
	        Imgproc.erode(rgbaImage, rgbaImage, kernel);
	        Imgproc.dilate(rgbaImage, rgbaImage, kernel);
	        //Imgproc.cvtColor(rgbaImage, rgbaImage, Imgproc.COLOR_GRAY2BGRA) ;
    	}
    }
}
