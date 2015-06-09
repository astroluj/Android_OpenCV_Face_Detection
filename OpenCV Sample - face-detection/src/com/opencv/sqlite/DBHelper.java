package com.opencv.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.opencv.util.DBUtil;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(DBUtil.createTable (
				ImagePathDB.TABLE_NAME, ImagePathDB.createQueryKeys())) ;
		db.execSQL(DBUtil.createTable (
				ImageAnalyzeDB.TABLE_NAME, ImageAnalyzeDB.createQueryKeys())) ;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ImagePathDB.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ImageAnalyzeDB.TABLE_NAME);

		onCreate(db);
	}

}
