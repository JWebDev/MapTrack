package de.ai.mi.maptrack.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;

public class StartMenuActivity extends Activity {

	public static final String DIR_NAME = "MapTrack";
	private final String LOG_TAG = "START_ACTIVITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		createDirIfNotExist();
		//copyDbToSD();
		removeDb();
	}

	public void showTravelDescriptionActivity(View v) {
		Intent intent = new Intent(this, TravelDescriptionActivity.class);
		startActivity(intent);
	}

	public void showHistoryActivity(View v) {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}

	public void showTravelMapActivity(View v) {
		Intent intent = new Intent(this, TravelMapActivity.class);
		startActivity(intent);
	}

	public void showHelpActivity(View v) {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	private void createDirIfNotExist() {
		File folder = new File(Environment.getExternalStorageDirectory(), DIR_NAME);
		folder.mkdirs();
	}

	private void removeDb() {
		boolean isDeleted = deleteDatabase("MapTrackDB");
		Log.d(LOG_TAG, "--- Remove DB: ---");
		if (isDeleted) {
			Log.i(LOG_TAG, "MapTrackDB is deleted!");
		} else {
			Log.i(LOG_TAG, "MapTrackDB is NOT deleted!");
		}
	}

	private void copyDbToSD() {

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//de.ai.mi.maptrack//databases//MapTrackDB";
				String backupDBPath = "MapTrackBackup";

				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();

					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}
