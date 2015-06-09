package com.opencv.sqlite;

import java.util.ArrayList;

import android.provider.BaseColumns;

public class ImagePathDB implements BaseColumns {
	
	// Table Name
	public static final String TABLE_NAME ="ImagePath" ;

	// Key Name
	public static final String KEY_INDEX ="imageIndex" ;
	public static final String KEY_IMAGE_PATH ="imagePath" ;
	
	// Create querys
	public static final ArrayList<String> createQueryKeys () {
		
		ArrayList<String> list = new ArrayList<String> () ;
		
		list.add (KEY_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT,") ;
		list.add (KEY_IMAGE_PATH + " TEXT NOT NULL") ;
		
		return list ;
	}
}
