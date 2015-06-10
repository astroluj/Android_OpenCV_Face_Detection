package com.opencv.pca;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.opencv.util.CVUtil;

public class MatControll {

	public Mat reshapreMat (Mat mat) {
		
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
    	Imgproc.resize(mat, mat, CVUtil.IMAGE_SIZE);
    	mat = mat.reshape(0, 1) ;
    	mat.convertTo(mat, CvType.CV_64FC1) ;
    	
    	return mat ;
	}
	
	// 최소거리의 인덱스 반환하는 함수
	public int minimumIndex (double[] dist, int size) {
		double min = CVUtil.DBL_MAX ;
		int minIndex = size ;
		
		for (int i = 0 ; i < size ; i++) {
			if (min > dist[i]) {
				min = dist[i] ;
				minIndex = i ;
			}
		}
		
		return minIndex ;
	}
	
	// 거리구하는 함수
	public double[] distance(Mat distance) {
		double[] dist = new double[distance.cols ()] ;
		
		for (int i = 0, col = distance.cols() ; i <col ; i++) {
			double sum = 0 ;
			for (int j = 0, row = distance.rows() ; j < row ; j++) {
				sum += distance.get(i, j)[0] * distance.get(i, j)[0] ;
			}
			dist[i] = Math.sqrt(sum) ;
		}
		
		return dist ;
	}
}
