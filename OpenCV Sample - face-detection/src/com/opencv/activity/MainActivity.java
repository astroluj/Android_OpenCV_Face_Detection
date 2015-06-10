package com.opencv.activity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Rect;

import com.opencv.camera.CvCameraControll;
import com.opencv.dialog.FaceAlertDialog;
import com.opencv.R;
import com.opencv.sqlite.DB;
import com.opencv.sqlite.ImagePathDB;
import com.opencv.util.MenuValues;
import com.opencv.util.MenuUtil;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

public class MainActivity extends ActionBarActivity {

    private static final String    TAG                 = "PCA::Activity";
    
    // Custom Class
	private DB db ;
	private FaceAlertDialog faceAlertDialog ;
    private CvCameraControll cvCamera ;
    private MenuValues menuValues ;
    
    private CameraBridgeViewBase  openCvCameraView;

 	private String[] detectorTypeNames;
 	private String[] skinColorDetectionStateNames ;
 	private String[] faceDetectionStateNames ;
 	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Call Layout
        setContentView(R.layout.face_detect_surface_view);

        initializes () ;
    }
    
    private void initializes () {
    	// Init
     	// face detector
        detectorTypeNames = new String[2];
        detectorTypeNames[MenuUtil.ENABLED_JAVA] = getString(R.string.enabled_java) ;
        detectorTypeNames[MenuUtil.ENABLED_NATIVE] = getString(R.string.enabled_native) ;

        // face detection state
        faceDetectionStateNames = new String[2];
        faceDetectionStateNames[MenuUtil.START_DETECTION] = getString(R.string.start_face_detection) ;
        faceDetectionStateNames[MenuUtil.STOP_DETECTION] = getString(R.string.stop_face_detection) ;
        
        // skin color detection state
        skinColorDetectionStateNames = new String[2] ;
        skinColorDetectionStateNames[MenuUtil.START_DETECTION] = getString(R.string.start_skin_color_detection) ;
        skinColorDetectionStateNames[MenuUtil.STOP_DETECTION] = getString(R.string.stop_skin_color_detection) ;
        
        db = new DB (getApplicationContext()) ;
        
        // MenuValues Initial
    	menuValues = new MenuValues() ;
    	
    	// OpenCV Camera SurfaceView
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        
    	// Parameter context, MenuValues
        cvCamera = new CvCameraControll (getApplicationContext(), menuValues,
        		openCvCameraView) ;
        
     // FaceAlertDialog Initial
    	faceAlertDialog = new FaceAlertDialog (MainActivity.this, getApplicationContext(), cvCamera) ;
    	
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    

	// View Touch Event
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		// Multi Touch Accept
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // Touching
			
			Rect[] facesArrays = cvCamera.getFacesArray() ;
			if (facesArrays != null) {
				boolean isFaceTouch = false ;
				
				for (Rect faceArray : facesArrays) {
					if ((faceArray.x <= event.getX() && event.getX() <= faceArray.x + faceArray.width)
							|| faceArray.y <= event.getY() && event.getY() <= faceArray.y + faceArray.height) {
						Log.d (TAG, "Touch X : " + event.getX() + " Y : " + event.getY()) ;
						
						// is FAce Touch
						isFaceTouch = true ;
						
						faceAlertDialog.setFaceArray (cvCamera.getSearchFaceMat(faceArray)) ;
						faceAlertDialog.setSearchAlertBuilder("선택한 얼굴을 검색 하시겠습니까?") ;
						
						break ;
					}
				}
				// Face exception touch
				if (isFaceTouch == false) 
					cvCamera.setSearchFaceMat(null);
			}

			break;
		}
		return true;
	}
	
    @Override
    public void onPause() {
        super.onPause();
        
        //Camera stop view
		if (openCvCameraView != null)
			openCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Camera Holder Reload
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, 
        		this, cvCamera.getLoaderCallback ()) ;
    }

    public void onDestroy() {
        super.onDestroy();
        if (openCvCameraView != null)
        	openCvCameraView.disableView();
    }

    @Override
    // Create Menu Item
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
        Log.i(TAG, "called onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu) ;
    	
    	// Face Detector Type
    	MenuItem faceDetectionFilterSubMenu = menu.findItem(R.id.face_dectection_filter_sub_menu) ;
    	
    	if (menuValues.getFaceDetectorType () == MenuUtil.ENABLED_NATIVE)
    		faceDetectionFilterSubMenu.setTitle(getString(R.string.face_detector_native)) ;
    	else faceDetectionFilterSubMenu.setTitle (getString(R.string.face_detector_java)) ;
    	
    	// Skin Color Detection State Visiabled
    	MenuItem skinColorDetectionState = menu.findItem(R.id.skin_color_dection_state) ;
    	
    	if (menuValues.getFaceDetectionState () == MenuUtil.START_DETECTION) {
    		skinColorDetectionState.setVisible(true) ;
    		skinColorDetectionState.setTitle(
    				skinColorDetectionStateNames[
    				                             (menuValues.getSkinColorDetectionState() + 1) 
    				                             % skinColorDetectionStateNames.length]) ;
    	}
    	else skinColorDetectionState.setVisible(false) ;
    	
    	// Face Size
    	MenuItem faceSizeSubMenu = menu.findItem(R.id.face_size_sub_menu) ;
    	
    	float sizeValue = menuValues.getRelativeFaceSize () ;
    	if (sizeValue == 0.5f) 
    		faceSizeSubMenu.setTitle(getString(R.string.fase_size_50)) ;
        else if (sizeValue == 0.4f)
        	faceSizeSubMenu.setTitle(getString(R.string.fase_size_40)) ;
        else if (sizeValue == 0.3f)
        	faceSizeSubMenu.setTitle(getString(R.string.fase_size_30)) ;
        else if (sizeValue == 0.2f)
        	faceSizeSubMenu.setTitle(getString(R.string.fase_size_20)) ;
    	
    	return true ;
    }
    
    @Override
    // Menus
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        int itemId = item.getItemId() ;
        
        // Face Detection State
        if (itemId == R.id.face_detection_state) {
			int faceDetectionState = menuValues.getFaceDetectionState() ;
			
			item.setTitle(faceDetectionStateNames[faceDetectionState]);
			faceDetectionState = (faceDetectionState + 1)
					% faceDetectionStateNames.length;
			
			setFaceDetectionState(faceDetectionState);
		}
        // Face Detector type
        else if (itemId == R.id.filter_java) {
			int faceDetectorType = MenuUtil.ENABLED_JAVA ;

			item.setTitle(detectorTypeNames[faceDetectorType]);
			setFaceDetectorType(faceDetectorType);
		} 
        else if (itemId == R.id.filter_native) {
			int faceDetectorType = MenuUtil.ENABLED_NATIVE ;

			item.setTitle(detectorTypeNames[faceDetectorType]);
			setFaceDetectorType(faceDetectorType);
		}
        // Skin Color Detection
        else if (itemId == R.id.skin_color_dection_state) {
        	int skinColorDetectionState = menuValues.getSkinColorDetectionState() ;
			
			item.setTitle(skinColorDetectionStateNames[skinColorDetectionState]);
			skinColorDetectionState = (skinColorDetectionState + 1)
					% skinColorDetectionStateNames.length;
			
			setSkinColorDetectionState(skinColorDetectionState);
        }
		// FaceSize
        else if (itemId == R.id.face_size_50) 
                setMinFaceSize(0.5f);
        else if (itemId == R.id.face_size_40)
        	setMinFaceSize(0.4f);
        else if (itemId == R.id.face_size_30)
        	setMinFaceSize(0.3f);
        else if (itemId == R.id.face_size_20)
        	setMinFaceSize(0.2f);
		
        return true ;
    }

    // User Select Face Size in View
    public void setMinFaceSize(float faceSize) {
    	// faceSize example 0.5, 0.4, 0.3, 0.2,
    	menuValues.setRelativeFaceSize (faceSize) ;
    	menuValues.setAbsoluteFaceSize (0) ;
    }
    
    // User Select detector Types, java or native
    private void setFaceDetectorType(int type) {
        if (menuValues.getFaceDetectorType () != type) {
        	menuValues.setFaceDetectorType (type) ;

            if (type == MenuUtil.ENABLED_NATIVE) {
                Log.i(TAG, "Detection Based Tracker enabled");
                menuValues.startNativeDetector () ;
            } else {
                Log.i(TAG, "Cascade detector enabled");
                menuValues.stopNativeDetector () ;
            }
        }
    }
    
    // User Change FaceDetection State, Start or Stop
    private void setFaceDetectionState(int type) {
        if (menuValues.getFaceDetectionState() != type) {
        	menuValues.setFaceDetectionState(type) ;
            
            if (type == MenuUtil.START_DETECTION) {
                Log.i(TAG, "Detection starting");
				
            	// Default Image Insert
                db.open() ;
                int count = db.selectAll(ImagePathDB.TABLE_NAME).getCount() ;
                db.close();
                // DB 처음 데이터 입력하기
                if (count == 0) 
                	faceAlertDialog.setInitialAlertBuilder("기본 이미지 정보를 입력 하시겠습니까?") ;
                
                
    			if (openCvCameraView != null) {
    				// register Listener
    				if (cvCamera == null) 
    					cvCamera = new CvCameraControll (getApplicationContext(), menuValues,
    							openCvCameraView) ;
    				openCvCameraView.setCvCameraViewListener(cvCamera);
    			}
            } 
            else { // START_DETECTION
                Log.i(TAG, "Detection stopping");
                
                menuValues.setSkinColorDetectionState(MenuUtil.STOP_DETECTION) ;
                // Release Listener
                if (openCvCameraView != null)
                	cvCamera = null ;
            	openCvCameraView.setCvCameraViewListener(cvCamera) ;
            }
        }
    }
    
    // User Change SkinColorDetection State, Start or Stop
    private void setSkinColorDetectionState(int type) {
        if (menuValues.getSkinColorDetectionState() != type) {
        	menuValues.setSkinColorDetectionState(type) ;
        }
    }
}
