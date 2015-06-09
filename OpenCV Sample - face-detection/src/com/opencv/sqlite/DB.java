package com.opencv.sqlite;

import com.opencv.util.DBUtil;

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
			
			Log.d (TAG, "DB R/W Open") ;
		} catch (Exception e) {
			e.printStackTrace() ;
			sqlDB = new DBHelper(context, DBUtil.DB_NAME, null,
					DBUtil.DB_VERSION).getReadableDatabase() ;
			
			Log.d (TAG, "DB Read Only Open") ;
		}
		
		return this;
	}

	public void close() {
		Log.d (TAG, "DB Close") ;
		sqlDB.close();
	}
	
	// Select All
	public Cursor selectAll (String tableName) {
		return sqlDB.query(tableName, null, null, null, null, null, null) ;
	}
		
	public long insert (String tableName, ContentValues contentValues) {
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
