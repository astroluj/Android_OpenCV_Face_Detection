package com.opencv.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.opencv.face.FaceDetectionArea;
import com.opencv.skincolor.SkinColorDetection;
import com.opencv.sqlite.DB;
import com.opencv.sqlite.ImagePathDB;
import com.opencv.sqlite.util.DBUtil;
import com.opencv.util.CVUtil;
import com.opencv.util.MenuValues;
import com.opencv.util.MenuUtil;
import com.opencv.xml.FileStorageXML;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CvCameraControll implements CvCameraViewListener2 {

	private static final String TAG = "PCA::CameraControll";

	// Custom Class
	private DB db ;
	private FaceDetectionArea faceDetectionArea ;
	private SkinColorDetection skinColorDetection ;
	private LoaderCallback  loaderCallback ;
	private MenuValues menuValues ;
	
	private Mat mean ;
	
	private Context context ;
    
	public CvCameraControll(Context context, MenuValues menuValues,
			CameraBridgeViewBase  openCvCameraView) {
		
		this.context = context ;
		this.menuValues = menuValues ;
		
		this.loaderCallback = new LoaderCallback (this.context, menuValues, 
				openCvCameraView) ;
		
		faceDetectionArea = new FaceDetectionArea (this.menuValues) ;
		
		// Default Image Insert
		db = new DB (this.context) ;
        db.open() ;
        int count = db.selectAll(ImagePathDB.TABLE_NAME).getCount() ;
        db.close();
        // DB 처음 데이터 입력하기
        if (count == 0) {
        	insertDefaultImages () ;
        	studyDefaultImage () ;
        }
	}
	
	public void onCameraViewStarted(int width, int height) {
		Log.i(TAG, "onCameraViewStarted");
		// Construct Gray Scale
		faceDetectionArea.constructMatGray();
		// Construct RGB Scale
		faceDetectionArea.constructMatRGBAlpha();
	}

	public void onCameraViewStopped() {
		Log.i(TAG, "onCameraViewStopped");
		// play after Call OnPause
		faceDetectionArea.getMatGray().release();
		faceDetectionArea.getMatRGBAlpha().release(); 
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// Set Frame Scales
		faceDetectionArea.setMatGray(inputFrame.gray()) ;
		faceDetectionArea.setMatRGBAlpha(inputFrame.rgba()) ;
		
		// 얼굴 찾기
		Rect[] facesArray = faceDetectionArea.FaceDetection() ;
		
		// exist faces
		if (!(facesArray == null || facesArray.length == 0)
				&& menuValues.getSkinColorDetectionState() == MenuUtil.START_DETECTION) {
			// Finding skin colors
			// Init case
			if (skinColorDetection == null)
				skinColorDetection = new SkinColorDetection(faceDetectionArea.getMatRGBAlpha(), this.menuValues) ;
			
			return skinColorDetection.skinColorDetection(facesArray) ;
		}
		// else case and non-exist faces
		else {
			// Rectangle draw near faces 
 			for (Rect faceArray : facesArray) {
 				Core.rectangle(faceDetectionArea.getMatRGBAlpha(),
 						faceArray.tl(), faceArray.br(),
 						CVUtil.FACE_RECT_COLOR, 3);
 			}
 			
 			return faceDetectionArea.getMatRGBAlpha() ;
		}
	}
	
	// Return BaseLoaderCallback
	public BaseLoaderCallback getLoaderCallback () {
		return this.loaderCallback.getBaseLoaderCallback () ;
	}
	
    private void studyDefaultImage () {
    	
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
        	Imgproc.resize(grayMat, grayMat, CVUtil.IMAGE_SIZE);
        	
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
        FileStorageXML fileXML = new FileStorageXML() ;
        fileXML.create(DBUtil.PATH +"db.xml", eigenFace, omega) ;
        
        eigenFace.release() ; 
        omega.release() ;
    }
    
    // Default Image insert
    private void insertDefaultImages () {
    	
    	for (int i = 1 ; i <= 20 ; i++) {
    		
    		// Directory Create
    		File file = new File (DBUtil.PATH) ;
			if (!file.isDirectory()) file.mkdirs() ;
			
    		OutputStream outputStream = null ;
    		try {
    			// file create
    			outputStream = new FileOutputStream (DBUtil.PATH + "default_image_" + i + ".png") ;
    			// Image Compress
    			BitmapFactory.decodeResource(context.getResources(),
    					context.getResources().getIdentifier("default_image_" + i, "drawable", "com.opencv"))
    					.compress(Bitmap.CompressFormat.PNG, 100, outputStream) ;
    			outputStream.flush(); 
    			
    			// DB Insert Path
    			ContentValues contentValues = new ContentValues() ;
    			contentValues.put(ImagePathDB.KEY_IMAGE_PATH,
    					DBUtil.PATH + "default_image_" + i + ".png");
    			db.insert(ImagePathDB.TABLE_NAME, contentValues) ;
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    			Log.e (TAG, "FileNotFound") ;
    		} catch (IOException e) {
    			e.printStackTrace();
    			Log.e (TAG, "IOException") ;
    		} finally {
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
