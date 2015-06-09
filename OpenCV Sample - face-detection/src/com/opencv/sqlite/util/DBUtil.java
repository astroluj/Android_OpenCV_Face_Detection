package com.opencv.sqlite.util;

import java.util.ArrayList;

import android.os.Environment;

public class DBUtil {

	// Default Image Path
	public static final String PATH = Environment.getExternalStorageDirectory() +"/PCA_Image/" ;
	
	// DB Name
	public static final String DB_NAME = "PCA.db" ;
	
	// DB Version
	public static final int DB_VERSION = 1 ;
	
	// DDL Commend
	public static final String createTable (String tableName, ArrayList<String> commendList) {
		// name
		String createQuery = "CREATE TABLE " + tableName + " (" ;
		
		for (String key : commendList) 
			createQuery += key ;
		
		createQuery += ")" ;
		
		return createQuery ;
	}
}
