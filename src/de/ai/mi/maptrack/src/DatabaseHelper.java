package de.ai.mi.maptrack.src;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final String LOG_TAG = "DB_HELPER";
	private static DatabaseHelper mInstance = null;
	private static String DATABASE_NAME = "MapTrackDB";

	public static DatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return mInstance;
	}

	private DatabaseHelper(Context context) {
//		super(new DatabaseContext(context), DATABASE_NAME, null, 1);
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "--- onCreate database ---");
		db.execSQL("create table travels (" + "id integer primary key autoincrement," + "travelName varchar," + "travelDescription text," + "travelDirName varchar," + "isActive boolean"+ ");");
		db.execSQL("create table routes (" + "id integer primary key autoincrement," + "travelName varchar," + "nummer integer," + "latitude double," + "longitude double" + ");");
		db.execSQL("create table pois (" + "id integer primary key autoincrement," + "travelName varchar," + "latitude double," + "longitude double,"  + "poiName varchar," + "poiDescription text," + "markerTyp character," + "pathToImageOrVideo varchar"+ ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public void clearDb() {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d(LOG_TAG, "--- Clear mytable: ---");
		int clearCount = db.delete("mytable", null, null);
		Log.d(LOG_TAG, "deleted rows count = " + clearCount);
		db.close();
	}
	
}

