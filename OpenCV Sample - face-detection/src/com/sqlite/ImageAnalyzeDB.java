package com.sqlite;

import java.util.ArrayList;

import android.provider.BaseColumns;

public class ImageAnalyzeDB implements BaseColumns {

	// Table Name
	public static final String TABLE_NAME ="ImageAnalyze" ;

	// Key Name
	public static final String KEY_INDEX ="imageIndex" ;
	public static final String KEY_EIGEN_DATA = "eigenData" ;
	public static final String KEY_OMEGA_DATA ="omegaData" ;
	
	// Create querys
	public static final ArrayList<String> createQueryKeys () {
		
		ArrayList<String> list = new ArrayList<String> () ;
		
		list.add (KEY_INDEX + " INTEGER PRIMARY KEY,") ;
		list.add (KEY_EIGEN_DATA + " TEXT NOT NULL,") ;
		list.add (KEY_OMEGA_DATA + " TEXT NOT NULL,") ;
		list.add ("FOREIGN KEY (" + KEY_INDEX + ") "
				+ "REFERENCES " + ImagePathDB.TABLE_NAME + "(" + ImagePathDB.KEY_INDEX +")"
				+ " ON UPDATE CASCADE ON DELETE CASCADE") ;
		
		return list ;
	}
}
