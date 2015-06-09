package com.opencv.pca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.opencv.sqlite.DB;
import com.opencv.sqlite.ImageAnalyzeDB;
import com.opencv.sqlite.ImagePathDB;
import com.opencv.util.CVUtil;
import com.opencv.util.DBUtil;

public class PCA {

	private static final String TAG = "PCA::PCA";
	
	// Custom Class
	private DB db ;
	
	private Mat mean ;
	private Context context ;
	
	public PCA (Context context) {
	
		this.context =context ;
		
		db = new DB (this.context) ;
	}
	
	// 찾기
	public void searchPCA () {
		// 1) DB읽어오기
		db.open () ;
		
		// 기존 DB에서 값 가져옴
		Mat eigenFace = new Mat (), omega = new Mat () ;
		Cursor cursor = db.selectAll(ImageAnalyzeDB.TABLE_NAME) ;
		
		cursor.moveToFirst() ;
		Mat tempMat = new Mat (1, 1024, 6) ;
		for (int i = 0 ; i < cursor.getCount() ; i++) {
			
			try {
				Log.d (TAG, Double.parseDouble(cursor.getString(1)) +"") ;
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
				Log.d (TAG, "A") ;
			}
			try {
				omega.put(i, 0, cursor.getString(2).getBytes("UTF-8")) ;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			cursor.moveToNext() ;
		}
		
		cursor.close () ;
		db.close () ;
		eigenFace.convertTo(eigenFace, CvType.CV_64FC1) ;
		omega.convertTo(omega, CvType.CV_64FC1) ;
		
		// 2) 테스트영상 불러오기
		Mat testMat = new Mat () ;
		Imgproc.cvtColor(testMat, testMat, Imgproc.COLOR_RGB2GRAY);
    	//Imgproc.resize(testMat, testMat, CVUtil.IMAGE_SIZE);
    	testMat = testMat.reshape(0, 1) ;
    	testMat.convertTo(testMat, CvType.CV_64FC1) ;
    	
		// 3) 테스트영상 정규화
		Core.subtract(testMat, mean, testMat) ;
		
		// 4) 벡터공간에 정규화된 테스트영상 투영
		Core.gemm(testMat, eigenFace, 1, new Mat (), 0, testMat) ;
		
		// 5) weight값 계산
		Mat weightMat = new Mat (), subtract = new Mat () ;
		for (int i = 0 ; i < omega.rows() ; i++) {
			Core.subtract(testMat, omega.row(i), subtract) ;
			weightMat.push_back(subtract) ;
		}
			
		// 6) 가장 근접한 weight값 찾기
		double[][] dist = new double[weightMat.rows()][weightMat.cols()] ;
		
		for (int i = 0 ; i < dist.length ; i++) {
			for (int j = 0 ; j < dist[i].length ; j++) {
				
			}
		}
	}
	
	// 새로운 이미지 학습
	public void studyDefaultImage () {
    	try {
	    	//1.학습
	    	// 1) 학습데이터 불러오기 
	        Mat studyMat = new Mat () ;
	        // push gray scale
	        db.open() ;
	        Cursor cursor = db.selectAll(ImagePathDB.TABLE_NAME) ;
	
	        cursor.moveToFirst() ;
	        for (int i = 0 ; i < cursor.getCount() ; i++) {
	        	String fileName = cursor.getString(1) ;
	        	Log.d (TAG, "Image Path : " + fileName) ;
	        	
	        	Mat grayMat = Highgui.imread(fileName,
	        			Highgui.IMREAD_ANYDEPTH | Highgui.IMREAD_ANYCOLOR) ;
	        	Imgproc.cvtColor(grayMat, grayMat, Imgproc.COLOR_RGB2GRAY);
	        	//Imgproc.resize(grayMat, grayMat, CVUtil.IMAGE_SIZE);
	        	
	        	grayMat = grayMat.reshape(0, 1) ;
	        	grayMat.convertTo(grayMat, CvType.CV_64FC1) ;
	        	studyMat.push_back(grayMat) ;
	        	
	        	cursor.moveToNext() ;
	        }
	        cursor.close() ;
	        db.close() ;
	        
	        // 2) 평균영상계산
	        mean = studyMat.row(0).clone() ;
	        for (int i = 1 ; i < studyMat.rows() ; i++)
	        	Core.add(mean, studyMat.row(i), mean) ;
	        Core.divide(mean, new Scalar(20), mean, 1) ;
	    
	        // 3) 벡터공간 만들기 및 벡터공간 정규화
	        Mat vecSPC = new Mat (), subtract = new Mat () ;
	        for (int i = 0 ; i < studyMat.rows() ; i++) {
	        	Core.subtract(studyMat.row(i), mean, subtract) ;
	        	vecSPC.push_back(subtract) ;
	        }
	        // 4) 공분산계산 정사각형 만들기
	        Mat cov = new Mat () ;
	        Core.gemm(vecSPC, vecSPC.t(), 1, new Mat (), 0, cov) ;
	       
	        // 5) eidgen_face계산
	        Mat eigenVectors = new Mat (), eigenValues = new Mat () ;
	        Core.eigen(cov, true, eigenValues, eigenVectors) ;
	    
	        // 6) 벡터공간에 eigen_face투영
	        Mat eigenFace = new Mat (), omega = new Mat () ;
	        Core.gemm(eigenVectors, vecSPC, 1, new Mat (), 0, eigenFace) ;
	        Core.gemm(vecSPC, eigenFace.t(), 1, new Mat (), 0, omega) ;
	    
	        // 7) DB저장
	        db.open () ;
	        for (int i = 0 ; i < omega.rows() ; i++) {
		        ContentValues  contentValues = new ContentValues () ;
		        contentValues.put(ImageAnalyzeDB.KEY_EIGEN_DATA, eigenFace.row(i).dump ()) ;
		        contentValues.put(ImageAnalyzeDB.KEY_OMEGA_DATA, omega.row(i).dump ()) ;
		        
		        db.insert(ImageAnalyzeDB.TABLE_NAME, contentValues) ;
	        }
	        db.close () ;
	        
	        subtract.release() ;
	        eigenVectors.release() ;
	        eigenValues.release() ;
	        cov.release() ;
	        vecSPC.release() ;
	        eigenFace.release() ; 
	        omega.release() ;
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		db.close() ;
    	}
    }
    
	// New Image insert
	public void insertNewImages (Mat newMat) {
		
		// Bitmap 틀
		Bitmap image = Bitmap.createBitmap(newMat.cols(), newMat.rows(),
				Bitmap.Config.ARGB_8888) ;
		// Convert 
		Utils.matToBitmap(newMat, image);
		
		// Directory Create
		File file = new File (DBUtil.PATH) ;
		if (!file.isDirectory()) file.mkdirs() ;
		
		// exist Images count
		db.open() ;
		int count = db.selectAll(ImagePathDB.TABLE_NAME).getCount() + 1;
		db.close () ;
		
		OutputStream outputStream = null ;
		try {
			// file create
			outputStream = new FileOutputStream (DBUtil.PATH + "default_image_" + count + ".png") ;
			// image resizing
			Bitmap.createScaledBitmap(image,
					CVUtil.IMAGE_LENGTH, CVUtil.IMAGE_LENGTH, true) ;
			// Image Compress
			image.compress(Bitmap.CompressFormat.PNG, 100, outputStream) ;
			outputStream.flush(); 
			
			// DB Insert Path
			ContentValues contentValues = new ContentValues() ;
			contentValues.put(ImagePathDB.KEY_IMAGE_PATH,
					DBUtil.PATH + "default_image_" + count + ".png");
			db.open() ;
			db.insert(ImagePathDB.TABLE_NAME, contentValues) ;
			db.close() ;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e (TAG, "FileNotFound") ;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e (TAG, "IOException") ;
		} finally {
			db.close() ; 
			if (outputStream != null) {
				try {
					outputStream.close () ;
					outputStream = null ;
				} catch (IOException e) {
					outputStream = null ;
				}
    		}
    	}
	}
	
    // Default Image insert
    public void insertDefaultImages () {
    	
    	for (int i = 1 ; i <= 20 ; i++) {
    		
    		// Directory Create
    		File file = new File (DBUtil.PATH) ;
			if (!file.isDirectory()) file.mkdirs() ;
			
    		OutputStream outputStream = null ;
    		try {
    			// file create
    			outputStream = new FileOutputStream (DBUtil.PATH + "default_image_" + i + ".png") ;
    			// Image Compress
    			Bitmap image = BitmapFactory.decodeResource(context.getResources(),
    					context.getResources().getIdentifier("default_image_" + i, "drawable", "com.opencv")) ;
    			// resizing
    			Bitmap.createScaledBitmap(image,
    					CVUtil.IMAGE_LENGTH, CVUtil.IMAGE_LENGTH, true) ;
    			image.compress(Bitmap.CompressFormat.PNG, 100, outputStream) ;
    			outputStream.flush(); 
    			
    			// DB Insert Path
    			ContentValues contentValues = new ContentValues() ;
    			contentValues.put(ImagePathDB.KEY_IMAGE_PATH,
    					DBUtil.PATH + "default_image_" + i + ".png");
    			db.open() ;
    			db.insert(ImagePathDB.TABLE_NAME, contentValues) ;
    			db.close() ;
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    			Log.e (TAG, "FileNotFound") ;
    		} catch (IOException e) {
    			e.printStackTrace();
    			Log.e (TAG, "IOException") ;
    		} finally {
    			db.close() ; 
    			if (outputStream != null) {
    				try {
    					outputStream.close () ;
    					outputStream = null ;
    				} catch (IOException e) {
    					outputStream = null ;
    				}
    			}
    		}
    	}
    }
}
