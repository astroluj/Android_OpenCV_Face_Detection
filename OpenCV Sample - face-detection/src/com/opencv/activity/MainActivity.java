package com.opencv.activity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;

import com.opencv.R;
import com.opencv.camera.CvCameraControll;
import com.opencv.util.MenuValues;
import com.opencv.util.util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private static final String    TAG                 = "OpenCV::Activity";
    
    // Custom Class
    private CvCameraControll cvCamera ;
    private MenuValues menuValues ;
    
    private CameraBridgeViewBase  openCvCameraView;

     // Detection Start or Stop
 	private int detectionState ;
 	
 	private String[] detectorTypeNames;
 	private String[] detectionStateNames ;
 	private String[] dilateFilterNames ;
 	private String[] erodeFilterNames ;
 	
    public MainActivity() {

    	// Init
     	detectionState = util.STOP_DETECTION ;
     	
        detectorTypeNames = new String[2];
        detectorTypeNames[util.JAVA_DETECTOR] = "Java";
        detectorTypeNames[util.NATIVE_DETECTOR] = "Native (tracking)";

        detectionStateNames = new String[2];
        detectionStateNames[util.START_DETECTION] = "Start Detection";
        detectionStateNames[util.STOP_DETECTION] = "Stop Detection";
        
        dilateFilterNames = new String[2] ;
        dilateFilterNames[util.ENABLED_DILATE] = "Enabled Dilate" ;
        dilateFilterNames[util.DISABLED_DILATE] = "Disabled Dilate" ;
        
        erodeFilterNames = new String[2] ;
        erodeFilterNames[util.ENABLED_ERODE] = "Enabled Erode" ;
        erodeFilterNames[util.DISABLED_ERODE] = "Disabled Erode" ;
        
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Call Layout
        setContentView(R.layout.face_detect_surface_view);

        // MenuValues Initial
    	menuValues = new MenuValues() ;
    	
    	// OpenCV Camera SurfaceView
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        
    	// Parameter context, MenuValues
        cvCamera = new CvCameraControll (getApplicationContext(), menuValues,
        		openCvCameraView) ;
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
    	
    	// Face Size
    	MenuItem faceSizeSubMenu = menu.findItem(R.id.face_size_sub_menu), selectItem = null ;
    	
    	float sizeValue = menuValues.getRelativeFaceSize () ;
    	if (sizeValue == 0.5f) 
    		selectItem = menu.findItem(R.id.face_size_50) ;
        else if (sizeValue == 0.4f)
        	selectItem = menu.findItem(R.id.face_size_40) ;
        else if (sizeValue == 0.3f)
        	selectItem = menu.findItem(R.id.face_size_30) ;
        else if (sizeValue == 0.2f)
        	selectItem = menu.findItem(R.id.face_size_20) ;
    	
    	faceSizeSubMenu.setTitle(selectItem.getTitle()) ;
    	
    	
    	// SkinColor Filter
    	MenuItem skinColorFilterSubMenu = menu.findItem(R.id.filter_sub_menu) ;
    	
    	int dilateValue = menuValues.getDilateFilter(),
    			erodeValue = menuValues.getErodeFilter() ;
    	if (dilateValue == util.ENABLED_DILATE
    			&& erodeValue == util.ENABLED_ERODE) 
    		skinColorFilterSubMenu.setTitle("Enabled Erode, Dilate") ;
    	else if (dilateValue == util.ENABLED_DILATE)
    		skinColorFilterSubMenu.setTitle("Enabled Dilate") ;
    	else if (erodeValue == util.ENABLED_ERODE)
    		skinColorFilterSubMenu.setTitle("Enabled Erode") ;
    	else skinColorFilterSubMenu.setTitle("No Filter") ;
    	
    	return true ;
    }
    
    @Override
    // Menus
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        int itemId = item.getItemId() ;
        
        // Detector type
		if (itemId == R.id.dectect_case) {
			int tmpDetectorType = (menuValues.getDetectorType () + 1)
					% detectorTypeNames.length;
			item.setTitle(detectorTypeNames[tmpDetectorType]);
			setDetectorType(tmpDetectorType);
		} 
		// Detection State
		else if (itemId == R.id.start_fase_detection) {
			item.setTitle(detectionStateNames[detectionState]);
			int tmpDetectionType = (detectionState + 1)
					% detectionStateNames.length;
			
			setDetectionState(tmpDetectionType);
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
        // Filter
        else if (itemId == R.id.filter_dilate) {
        	item.setTitle(dilateFilterNames[menuValues.getDilateFilter()]);
        	int tmpDilateFilter = (menuValues.getDilateFilter() + 1)
        			%  dilateFilterNames.length ;
			
        	setDilateFilter (tmpDilateFilter) ;
        }
        else if (itemId == R.id.filter_erode) {
        	item.setTitle(erodeFilterNames[menuValues.getErodeFilter()]);
        	int tmpErodeFilter = (menuValues.getErodeFilter() + 1)
        			%  erodeFilterNames.length ;
        	
        	setErodeFilter (tmpErodeFilter) ;
        }
		
        return true ;
    }

    // User Select Face Size in View
    public void setMinFaceSize(float faceSize) {
    	// faceSize example 0.5, 0.4, 0.3, 0.2,
    	menuValues.setRelativeFaceSize (faceSize) ;
    	menuValues.setAbsoluteFaceSize (0) ;
    }
    
    // User Select detector Types, java or native
    private void setDetectorType(int type) {
        if (menuValues.getDetectorType () != type) {
        	menuValues.setDetectorType (type) ;

            if (type == util.NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                menuValues.startNativeDetector () ;
            } else {
                Log.i(TAG, "Cascade detector enabled");
                menuValues.stopNativeDetector () ;
            }
        }
    }
    
    // User Change Detection State, Start or Stop
    private void setDetectionState(int type) {
        if (detectionState != type) {
            detectionState = type;
            
            if (type == util.START_DETECTION) {
                Log.i(TAG, "Detection starting");
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
                // Release Listener
                if (openCvCameraView != null)
                	cvCamera = null ;
            	openCvCameraView.setCvCameraViewListener(cvCamera) ;
            }
        }
    }
    
    // User Change Dilate Filter  Enabled or Disabled
    private void setDilateFilter (int type) {
    	if (menuValues.getDilateFilter () != type) {
        	menuValues.setDilateFilter(type) ;

            if (type == util.ENABLED_DILATE) {
                Log.i(TAG, "Dilate Filter  Enabled");
            } else 
                Log.i(TAG, "Dilate Filter  Disabled");
        }
    }
    
    // User Change Dilate Filter  Enabled or Disabled
    private void setErodeFilter (int type) {
    	if (menuValues.getErodeFilter () != type) {
        	menuValues.setErodeFilter(type) ;

            if (type == util.ENABLED_ERODE) {
                Log.i(TAG, "Erode Filter  Enabled");
            } else 
                Log.i(TAG, "Erode Filter  Disabled");
        }
    }
}
