package de.ai.mi.maptrack.src;

import de.ai.mi.maptrack.R;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSTrackerService extends Service implements LocationListener {

	private Context mContext;

	private boolean isGPSEnabled = false;
	private boolean isNetworkEnabled = false;
	private boolean canGetLocation = false;
	private LocationManager locationManager;
	private Location location;
	private double latitude;
	private double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000;

	public final static String LOCATION_LISTENER_ACTION = "GPS_ACTION";

	private final String GPS_TRACKER = "GPS_TRACKER";

	// public GPSTrackerServiceOld(Context context) {
	// mContext = context;
	// getLocation();
	// }

	private void getLoca() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		/*
		 * Es kann sein, dass der System-Service nicht verfügbar ist!
		 */
		if (locationManager == null) {
			Toast.makeText(this, "Could get location manager!", Toast.LENGTH_SHORT).show();
			return;
		}
		/*
		 * Es kann sein, dass der Location Provider nicht verfügbar ist!
		 */
		try {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, this);
		} catch (Exception e) {
			// TODO handle exception
		}
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	}

	public Location getLocation() {
		mContext = getApplicationContext();
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
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

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		return longitude;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		alertDialog.setTitle(R.string.alert_gps_settings_title);

		alertDialog.setMessage(R.string.alert_gps_settings_message);

		alertDialog.setPositiveButton(R.string.button_gps_settings_positive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton(R.string.button_gps_settings_negative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		double latitude = (double) (location.getLatitude());
		double longitude = (double) (location.getLongitude());

		Intent intent = new Intent();
		intent.setAction(LOCATION_LISTENER_ACTION);

		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);

		sendBroadcast(intent);

		Log.i(GPS_TRACKER, "latituteField: " + String.valueOf(latitude) + "; longitudeField: " + String.valueOf(longitude));

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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(GPS_TRACKER, "GPS_TRACKER onStartCommand");
		// TODO Auto-generated method stub
		getLocation();
		//getLoca();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onCreate() {
		super.onCreate();
		Log.d(GPS_TRACKER, "GPS_TRACKER onCreate");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(GPS_TRACKER, "GPS_TRACKER onDestroy");
	}

}
