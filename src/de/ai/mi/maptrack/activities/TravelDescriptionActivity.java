package de.ai.mi.maptrack.activities;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;

public class TravelDescriptionActivity extends Activity {

	final String LOG_TAG = "TRAVEL_DESCRIPTION: ";

	private String travelDirNameValue;
	private EditText travelName, travelDescription;
	private Button startButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_description_activity);
	}

	public void showTravelMapActivity(View v) {

		startButton = (Button) findViewById(R.id.travelStartButton);

		int isTravelExistsOrEmpty = isTravelExistsOrEmpty();

		if (isTravelExistsOrEmpty == 2) {
			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_exists), Toast.LENGTH_SHORT).show();
			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_exists_again), Toast.LENGTH_SHORT).show();
		} else if (isTravelExistsOrEmpty == 1) {
			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_empty), Toast.LENGTH_SHORT).show();
		} else if (isTravelExistsOrEmpty == 0) {
			createTravelDir();
			Intent intent = new Intent(this, TravelMapActivity.class);
			intent.putExtra("travelDirName", travelDirNameValue);
			intent.putExtra("travelName", travelName.getText().toString());
			intent.putExtra("travelDescription", travelDescription.getText().toString());
			startActivity(intent);
		}
	}

	private int isTravelExistsOrEmpty() {

		travelName = (EditText) findViewById(R.id.travelNameEditText);
		travelDescription = (EditText) findViewById(R.id.travelDescriptionEditText);

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		Cursor cursor = db.rawQuery("SELECT * from travels where travelName=\"" + travelName.getText().toString() + "\"", null);
		if (!travelName.getText().toString().equals("") && !travelDescription.getText().toString().equals("")) {
			if (!cursor.moveToFirst()) {
				chekTravelNameLength(travelName.getText().toString());

				contentValues.put("travelName", travelName.getText().toString());
				contentValues.put("travelDescription", travelDescription.getText().toString());
				contentValues.put("travelDirName", travelDirNameValue);

				db.insert("travels", null, contentValues);
				cursor.close();
				db.close();
				startButton.setEnabled(false);
				return 0;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

	private void createTravelDir() {
		File folder = new File(Environment.getExternalStorageDirectory(), StartMenuActivity.DIR_NAME + "/" + travelDirNameValue);
		folder.mkdirs();
	}

	private void chekTravelNameLength(String travelNameValue) {
		if (travelNameValue.length() >= 20) {
			this.travelDirNameValue = travelNameValue.substring(0, 20);
		} else {
			this.travelDirNameValue = travelNameValue;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (startButton != null) {
			startButton.setEnabled(true);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (travelName != null && travelDescription != null) {
			travelName.setText("");
			travelDescription.setText("");
		}
	}
}
