package com.opencv.dialog;

import org.opencv.core.Mat;

import com.opencv.camera.CvCameraControll;
import com.opencv.pca.PCA;
import com.opencv.util.CVUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.widget.ImageView;
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
	}
	
	// Search Face Set
	public void setFaceArray (Mat faceArray) {
		this.faceArray = faceArray ;
	}
	
	// Search FAce shows
	private void showSearchFace (Bitmap findedFace) {
		
		if (alert != null && alert.isShowing()) alert.dismiss(); 
		
		this.alertBuilder = new AlertDialog.Builder(this.alertContext);
		try {
			Bitmap.createScaledBitmap(findedFace,
					CVUtil.ROI_FACE_LENGTH, CVUtil.ROI_FACE_LENGTH, true) ;
			
			ImageView imageView = new ImageView (context) ;
			imageView.setImageBitmap(findedFace) ;
			
			alertBuilder.setMessage("얼굴이 일치 합니까?(아니오 누를 시 현재 얼굴 저장)") ;
			alertBuilder.setView(imageView) ;
			
			alertBuilder.setNegativeButton("아니요",
					new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	
					    	pca.insertNewImages(faceArray) ;
					    }
					});
		} catch (NullPointerException e) {
			alertBuilder.setMessage("일치하는 얼굴이 없습니다. 현재 얼굴을 저장합니다.") ;
		}
		
		// 공통 사항
 		alertBuilder.setCancelable(true) ;
		
		alertBuilder.setPositiveButton("예",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    }
				}) ;
		
		alert = alertBuilder.create() ;
		alert.show() ;
	}
	
	// Search Face Alert
	public void setSearchAlertBuilder (String msg) {
		
		if (alert != null && alert.isShowing()) alert.dismiss(); 
		
		this.alertBuilder = new AlertDialog.Builder(this.alertContext);
		
		alertBuilder.setMessage(msg) ;
		alertBuilder.setCancelable(true) ;
		
		alertBuilder.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	// Search Face 확정
				    	cvCamera.setSearchFaceMat(faceArray);
				    	// Searching
				    	pca.studyImage () ;
						try {
							showSearchFace(pca.searchPCA(faceArray)) ;
						} catch (Exception e) {
							showSearchFace ( null) ;
						}
				    }
				}) ;
		alertBuilder.setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    }
				});
		
		alert = alertBuilder.create() ;
		alert.show() ;
	}

	// Initial FAce Alert
	public void setInitialAlertBuilder (String msg) {
		
		if (alert != null && alert.isShowing()) alert.dismiss(); 
		
		this.alertBuilder = new AlertDialog.Builder(this.alertContext);
		
		alertBuilder.setMessage(msg) ;
		alertBuilder.setCancelable(true) ;
		
		alertBuilder.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	pca.insertDefaultImages () ;
				    }
				}) ;
		alertBuilder.setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    }
				});
		
		alert = alertBuilder.create() ;
		alert.show() ;
	}
}
