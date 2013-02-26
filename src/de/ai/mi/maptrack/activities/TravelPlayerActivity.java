package de.ai.mi.maptrack.activities;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TravelPlayerActivity extends Activity {

	private GoogleMap map;
	private ArrayList<LatLng> route = new ArrayList<LatLng>();
	private int currentPosition = 0;
	private int bearing_old = 0;
	private boolean play;
	private final Handler handler = new Handler();
	private Button backwards;
	private Button forwards;
	private Button playPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_player_activity);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// initDummyRoute("test");
		Bundle routeName = getIntent().getExtras();
		String name = routeName.getString("name");
		loadTravel(name);
		moveToFirstPosition();

		forwards = (Button) findViewById(R.id.forwards);
		forwards.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				moveToNextPosition();
			}
		});

		backwards = (Button) findViewById(R.id.backwards);
		backwards.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				moveToPreviewPosition();
			}
		});

		playPause = (Button) findViewById(R.id.play_pause);
		playPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (play == false) {
					play = true;
					forwards.setClickable(false);
					backwards.setClickable(false);
					playPause.setText(getResources().getString(
							R.string.button_player_pause));
					player();
				} else {
					play = false;
					forwards.setClickable(true);
					backwards.setClickable(true);
					playPause.setText(getResources().getString(
							R.string.button_player_start));
				}
			}
		});

		Button stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				moveToFirstPosition();
			}
		});
	}

	/**
	 * Startet die Autoplay-Funktion des Players.
	 */
	public void player() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				if (play) {
					moveToNextPosition();
					handler.postDelayed(this, 1000);
				}
			}
		};
		runnable.run();
	}

	/**
	 * Lässt die Cameraansicht zur gewünschten Position wechseln.
	 * 
	 * @param pos
	 *            Die gewünschte Position.
	 * @param bearing
	 *            Die Ausrichtung der Karte
	 */
	public void moveToPosition(LatLng pos, int bearing) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(pos) // sets the position
				.zoom(18) // Sets the zoom
				.bearing(bearing) // Sets the orientation of the camera to east
				.tilt(70) // Sets the tilt of the camera to 30 degrees
				.build(); // Creates a CameraPosition from the builder
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	/**
	 * Lädt die Reise anhand ihres Names in den Speicher.
	 * 
	 * @param travelName
	 *            Der Reisename.
	 */
	public void loadTravel(String travelName) {
		SQLiteDatabase db = DatabaseHelper.getInstance(this)
				.getReadableDatabase();

		Cursor cursor = db.query(true, "routes", null, "travelName like" + "'"
				+ travelName + "'", null, null, null, null, null);

		int indexLat = cursor.getColumnIndex("latitude");
		int indexLon = cursor.getColumnIndex("longitude");
		if (cursor.moveToFirst())
			do {
				route.add(new LatLng(cursor.getDouble(indexLat), cursor
						.getDouble(indexLon)));
			} while (cursor.moveToNext());
		cursor.close();
		db.close();
	}

	/**
	 * Lässt die Kamera zur ersten Position der Reise springen.
	 */
	public void moveToFirstPosition() {
		{
			int bearing = (int) bearing(route.get(0).latitude,
					route.get(0).longitude, route.get(1).latitude,
					route.get(1).longitude);
			currentPosition = 0;
			moveToPosition(route.get(0), bearing);
		}
	}

	/**
	 * Lässt die Kamera zur nächsten Position der Reise springen.
	 */
	public void moveToNextPosition() {
		if (route.size() - 1 > (currentPosition + 1)) {
			currentPosition++;
			int bearing = (int) bearing(route.get(currentPosition).latitude,
					route.get(currentPosition).longitude,
					route.get(currentPosition + 1).latitude,
					route.get(currentPosition + 1).longitude);
			moveToPosition(route.get(currentPosition), bearing);
		} else if (route.size() - 1 == (currentPosition + 1)) {
			currentPosition++;
			moveToPosition(route.get(currentPosition), bearing_old);
			play = false;
			forwards.setClickable(true);
			backwards.setClickable(true);
			playPause.setText(getResources().getString(
					R.string.button_player_start));
		} else {
			// Toast.makeText(this, "Ende der Route erreicht",
			// Toast.LENGTH_LONG);
			play = false;
			forwards.setClickable(true);
			backwards.setClickable(true);
			playPause.setText(getResources().getString(
					R.string.button_player_start));
		}
	}

	/**
	 * Lässt die Kamera zur vorrigen Position der Reise springen.
	 */
	public void moveToPreviewPosition() {
		if (0 == (currentPosition - 1)) {
			currentPosition = 0;
			moveToPosition(route.get(0), bearing_old);
		} else if (0 < (currentPosition - 1)) {
			currentPosition--;
			int bearing = (int) bearing(route.get(currentPosition).latitude,
					route.get(currentPosition).longitude,
					route.get(currentPosition + 1).latitude,
					route.get(currentPosition + 1).longitude);
			moveToPosition(route.get(currentPosition), bearing);

		} else {
			// Toast.makeText(this, "Anfang der Route erreicht",
			// Toast.LENGTH_LONG);
		}
	}

	/**
	 * Berechnet die Ausrichtung der Karte von Punkt A zu Punkt B.
	 * 
	 * @param lat1
	 *            Latitude von Punkt A
	 * @param lon1
	 *            Longitude von Punkt A
	 * @param lat2
	 *            Latitude von Punkt B
	 * @param lon2
	 *            Longitude von Punkt B
	 * @return Wert fürdie Ausrichtung der Karte.
	 */
	protected static double bearing(double lat1, double lon1, double lat2,
			double lon2) {
		double longitude1 = lon1;
		double longitude2 = lon2;
		double latitude1 = Math.toRadians(lat1);
		double latitude2 = Math.toRadians(lat2);
		double longDiff = Math.toRadians(longitude2 - longitude1);
		double y = Math.sin(longDiff) * Math.cos(latitude2);
		double x = Math.cos(latitude1) * Math.sin(latitude2)
				- Math.sin(latitude1) * Math.cos(latitude2)
				* Math.cos(longDiff);

		return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
	}
}
