package com.opencv.sqlite;

import com.opencv.sqlite.util.DBUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// DB interface
public final class DB {

	private final String TAG = "PCA::sqlite:DB" ;
	
	private SQLiteDatabase sqlDB ;
	private Context context ;
	
	public DB(Context context) {
		this.context = context;
	}

	public DB open() throws SQLException {
		try {
			sqlDB = new DBHelper(context, DBUtil.DB_NAME, null,
					DBUtil.DB_VERSION).getWritableDatabase() ;
		} catch (Exception e) {
			e.printStackTrace() ;
			sqlDB = new DBHelper(context, DBUtil.DB_NAME, null,
					DBUtil.DB_VERSION).getReadableDatabase() ;
		}
		
		return this;
	}

	public void close() {
		sqlDB.close();
	}
	
	// Select All
	public Cursor selectAll (String tableName) {
		return sqlDB.query(tableName, null, null, null, null, null, null) ;
	}
		
	public long insert (String tableName, ContentValues contentValues) {
		Log.d (TAG, "Insert "+ contentValues.getAsString(ImagePathDB.KEY_IMAGE_PATH)) ;
		
		return sqlDB.insert(tableName, null, contentValues) ;
	}
	
	public long update (String tableName, ContentValues contentValues,
			String whereClause, String[] whereArgs) {
		return sqlDB.update(tableName, contentValues, whereClause, whereArgs) ;
	}
	
	public long delete (String tableName, String whereClause, String[] whereArgs) {
		return sqlDB.delete(tableName, whereClause, whereArgs) ;
	}
}
