package de.ai.mi.maptrack.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;

public class TravelDescriptionActivity extends Activity {

	final String LOG_TAG = "myLogs: ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_description_activity);
		//clearDb();
	}

	public void showTravelMapActivity(View v) {
		
		Intent intent = new Intent(this, TravelMapActivity.class);
		startActivity(intent);
		
		//Работает
//		int isTravelExistsOrEmpty = isTravelExistsOrEmpty();
//
//		if (isTravelExistsOrEmpty == 2) {
//			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_exists), Toast.LENGTH_SHORT).show();
//			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_exists_again), Toast.LENGTH_SHORT).show();
//		} else if (isTravelExistsOrEmpty == 1) {
//			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_empty), Toast.LENGTH_SHORT).show();
//		} else if (isTravelExistsOrEmpty == 0) {
//			Intent intent = new Intent(this, TravelMapActivity.class);
//			startActivity(intent);
//		}
	}

	private int isTravelExistsOrEmpty() {
		EditText travelName, travelDescription;
		travelName = (EditText) findViewById(R.id.travelNameEditText);
		travelDescription = (EditText) findViewById(R.id.travelDescriptionEditText);

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		Cursor cursor = db.rawQuery("SELECT * from mytable where name=\"" + travelName.getText().toString() + "\"", null);
		if (!travelName.getText().toString().equals("") && !travelDescription.getText().toString().equals("")) {
			if (!cursor.moveToFirst()) {
				contentValues.put("name", travelName.getText().toString());
				contentValues.put("description", travelDescription.getText().toString());
				db.insert("mytable", null, contentValues);
				cursor.close();
				db.close();
				return 0;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

	private void clearDb() {
		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		Log.d(LOG_TAG, "--- Clear mytable: ---");
		int clearCount = db.delete("mytable", null, null);
		Log.d(LOG_TAG, "deleted rows count = " + clearCount);
		db.close();
	}

}
