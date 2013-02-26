package de.ai.mi.maptrack.src;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTrackerService extends Service implements LocationListener {

	private Context mContext;

	private boolean isGPSEnabled = false;
	private boolean isNetworkEnabled = false;
	private LocationManager locationManager;
	private Location location;
	private double latitude;
	private double longitude;
	private int gpsCounter = 0;
	private String travelName;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000;

	public final static String LOCATION_LISTENER_ACTION = "GPS_ACTION";

	private final String LOG_TAG = "GPS_TRACKER";


	public Location getLocation() {
		mContext = getApplicationContext();
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
			} else {
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTrackerService.this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		latitude = (double) (location.getLatitude());
		longitude = (double) (location.getLongitude());

		Intent intent = new Intent();
		intent.setAction(LOCATION_LISTENER_ACTION);

		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);

		sendBroadcast(intent);
		saveGPSCoordsToDb();

		Log.i(LOG_TAG, "latituteField: " + String.valueOf(latitude) + "; longitudeField: " + String.valueOf(longitude));

	}

	private void saveGPSCoordsToDb() {

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();

		ContentValues routeValues = new ContentValues();

		routeValues.put("travelName", travelName);
		routeValues.put("nummer", gpsCounter++);
		routeValues.put("latitude", latitude);
		routeValues.put("longitude", longitude);

		db.insert("routes", null, routeValues);

		db.close();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public class GPSBinder extends Binder {
		public GPSTrackerService getService() {
			return GPSTrackerService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "GPS_TRACKER onStartCommand");

		travelName = intent.getStringExtra("travelName");
		getLocation();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "GPS_TRACKER onCreate");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "GPS_TRACKER onDestroy");
	}

}
