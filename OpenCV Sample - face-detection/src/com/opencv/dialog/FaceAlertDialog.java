package com.opencv.dialog;

import org.opencv.core.Mat;

import com.opencv.camera.CvCameraControll;
import com.opencv.pca.PCA;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;

public class FaceAlertDialog  {

	// Custom Class
	private PCA pca ;
	private CvCameraControll cvCamera ;
	
	private Mat faceArray ;
	private Context alertContext, context ;
	private AlertDialog  alert ;
	private AlertDialog.Builder alertBuilder ;
	
	public FaceAlertDialog (Context alertContext, Context context, CvCameraControll cvCamera) {
		
		this.alertContext = alertContext ;
		this.context = context ;
		this.cvCamera = cvCamera ;
		
		// PCA Initial
    	this.pca = new PCA (this.context) ;
		this.alertBuilder = new AlertDialog.Builder(this.alertContext);
	}
	
	// AlertDialog Get
	public AlertDialog getAlertDialog () {
		return this.alert ;
	}
	
	// Search Face Set
	public void setFaceArray (Mat faceArray) {
		this.faceArray = faceArray ;
	}
	
	// Search Face Alert
	public void setSearchAlertBuilder (String msg) {
		
		alertBuilder.setMessage(msg) ;
		alertBuilder.setCancelable(true) ;
		
		alertBuilder.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	// Search Face 확정
				    	cvCamera.setSearchFaceMat(faceArray);
				    	// Searching
				    	pca.insertNewImages(faceArray);
						cvCamera.setSimilaFaceMat(pca.searchPCA(faceArray)) ;
				    }
				}) ;
		alertBuilder.setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	
				    	return;
				    }
				});
		
		alert = alertBuilder.create() ;
	}

	// Initial FAce Alert
	public void setInitialAlertBuilder (String msg) {
		
		alertBuilder.setMessage(msg) ;
		alertBuilder.setCancelable(true) ;
		
		alertBuilder.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	pca.insertDefaultImages () ;
	                	pca.studyDefaultImage () ;
				    }
				}) ;
		alertBuilder.setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	
				    	return;
				    }
				});
		
		alert = alertBuilder.create() ;
	}
}
