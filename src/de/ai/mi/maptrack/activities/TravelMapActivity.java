package de.ai.mi.maptrack.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;
import de.ai.mi.maptrack.src.GPSTrackerService;
import de.ai.mi.maptrack.src.PoiObject;

public class TravelMapActivity extends Activity implements OnClickListener, OnInfoWindowClickListener {

	private final String TEXT_POI = "TEXT_POI";
	private final String PICTURE_POI = "PICTURE_POI";
	private final String VIDEO_POI = "VIDEO_POI";
	private final String START_END_POI = "START_END_POI";

	private final String LOG_TAG = "TRAVEL_MAP";

	private static final int CAMERA_REQUEST_PICTURE = 110;
	private static final int CAMERA_REQUEST_VIDEO = 111;

	private ToggleButton travelOnOffButton;
	private MenuItem textPoiMenuItem;
	private MenuItem picturePoiMenuItem;
	private MenuItem videoPoiMenuItem;

	private String poiNameValue;
	private String poiDescriptionValue;
	private String travelDirName;
	private String travelName;
	private String travelDescription;

	private boolean isMenuActive = false;
	private boolean isNotificationCreated = false;
	private boolean isIntentFromNotification = false;
	private boolean isIntentFromMenu = false;
	private boolean isTravelEnd = false;
	private boolean isTravelStart = false;

	private String imageOrVideoUri;

	private LatLng currentPosition = null;
	private GoogleMap map;
	private NotificationManager notificationManager;

	private Reciever reciever;
	private Intent serviceIntent;
	private PolylineOptions route = new PolylineOptions().color(Color.RED);

	private ArrayList<Double[]> latitudesAndLongitudesArray = new ArrayList<Double[]>();
	private ArrayList<PoiObject> poisArray = new ArrayList<PoiObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		onNewIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {

		boolean isNewTravel = true;

		Bundle extras = intent.getExtras();

		if (extras != null) {

			setContentView(R.layout.travel_map_activity);

			travelDirName = extras.getString("travelDirName");
			travelName = extras.getString("travelName");
			travelDescription = extras.getString("travelDescription");

			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			map.setOnInfoWindowClickListener(this);

			if (extras.containsKey("isIntentFromNotification") || extras.containsKey("isIntentFromMenu")) {
				if (extras.containsKey("isIntentFromNotification")) {
					isIntentFromNotification = true;
				}
				if (extras.containsKey("isIntentFromMenu")) {
					isIntentFromMenu = true;
				}
				isNewTravel = false;
				resumeTravel(isNewTravel);
				removeNotification();
			}

			initTravelOnOffButton(isNewTravel);
		}
		super.onNewIntent(intent);
	}

	private void initTravelOnOffButton(boolean isNewTravel) {

		travelOnOffButton = (ToggleButton) findViewById(R.id.travel_start_stop);
		travelOnOffButton.setOnClickListener(this);

		if (!isNewTravel) {
			travelOnOffButton.setChecked(true);
		}
	}

	private void resumeTravel(boolean isNewTravel) {
		Log.i(LOG_TAG, "RESUMMMEEEEE TRAVELLL");

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		Cursor cursor;
		latitudesAndLongitudesArray.clear();
		poisArray.clear();
		cursor = db.rawQuery("SELECT * from routes where travelName=\"" + travelName + "\" ORDER BY nummer", null);

		if (cursor.moveToFirst()) {

			do {
				double latitude = Double.parseDouble(cursor.getString(3));
				double longitude = Double.parseDouble(cursor.getString(4));
				Double[] dValues = { latitude, longitude };
				latitudesAndLongitudesArray.add(dValues);

			} while (cursor.moveToNext());

		}

		cursor = db.rawQuery("SELECT * from pois where travelName=\"" + travelName + "\"", null);

		if (cursor.moveToFirst()) {

			do {

				PoiObject poiObject = new PoiObject();
				double latitude = Double.parseDouble(cursor.getString(2));
				double longitude = Double.parseDouble(cursor.getString(3));
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(new LatLng(latitude, longitude));
				markerOptions.title(cursor.getString(4));
				markerOptions.snippet(cursor.getString(5));

				poiObject.setMarkerOptions(markerOptions);
				poiObject.setMarkerTyp(cursor.getString(6));
				poiObject.setPathToImageOrVideo(cursor.getString(7));
				poisArray.add(poiObject);

			} while (cursor.moveToNext());

		}

		cursor.close();
		db.close();

		for (PoiObject poiObject : poisArray) {
			LatLng currentPosition = poiObject.getMarkerOptions().getPosition();
			String poiNameValue = poiObject.getMarkerOptions().getTitle();
			String poiDescriptionValue = poiObject.getMarkerOptions().getSnippet();
			String imageOrVideoUri;
			if (poiObject.getPathToImageOrVideo() != null) {
				imageOrVideoUri = poiObject.getPathToImageOrVideo();
			} else {
				imageOrVideoUri = null;
			}
			addPoi(poiObject.getMarkerTyp(), currentPosition, poiNameValue, poiDescriptionValue, imageOrVideoUri, "fromDb");
		}

		for (Double[] elem : latitudesAndLongitudesArray) {
			drawRoute(elem[0], elem[1]);
			currentPosition = new LatLng(elem[0], elem[1]);
		}

		startTravel(isNewTravel);

		cameraZoom(currentPosition);

	}

	private void addStartAndEndPois(String flag) {

		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition);

		if (flag.equals("start")) {
			markerOptions.title(getResources().getString(R.string.poi_travel_start) + travelName);
		} else if (flag.equals("end")) {
			markerOptions.title(getResources().getString(R.string.poi_travel_end) + travelName);
		}

		markerOptions.snippet(travelDescription);
		markerOptions.icon(bitmapDescriptor);

		map.addMarker(markerOptions);

		addPoiToDb(new PoiObject(markerOptions, START_END_POI, null));

		cameraZoom(currentPosition);
	}

	private void addPoi(String poiTyp, LatLng currentPosition, String poiNameValue, String poiDescriptionValue,
			String imageOrVideoUri, String fromOrToDbFlag) {

		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition);
		markerOptions.title(poiNameValue);
		markerOptions.snippet(poiDescriptionValue);

		if (poiTyp.equals(TEXT_POI)) {
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
		} else if (poiTyp.equals(PICTURE_POI)) {
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
		} else if (poiTyp.equals(VIDEO_POI)) {
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
		} else if (poiTyp.equals(START_END_POI)) {
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
		}

		if (fromOrToDbFlag.equals("toDb")) {
			addPoiToDb(new PoiObject(markerOptions, poiTyp, imageOrVideoUri));
		}

		markerOptions.icon(bitmapDescriptor);

		map.addMarker(markerOptions);

		cameraZoom(currentPosition);
	}

	private void addPoiInfo(final String poiTyp) {

		LayoutInflater inflater = getLayoutInflater();
		final View view = inflater.inflate(R.layout.add_poi_info_box, null);

		final AlertDialog.Builder addInfo = new AlertDialog.Builder(this);

		addInfo.setView(view);

		addInfo.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				poiNameValue = ((EditText) view.findViewById(R.id.poiName)).getText().toString().trim();
				poiDescriptionValue = ((EditText) view.findViewById(R.id.poiDescription)).getText().toString().trim();

				boolean check = checkFieldsToNull(poiNameValue, poiDescriptionValue);

				if (check) {

					if (poiTyp.equals(TEXT_POI)) {
						addPoi(TEXT_POI, currentPosition, poiNameValue, poiDescriptionValue, imageOrVideoUri, "toDb");
					}
					if (poiTyp.equals(PICTURE_POI)) {
						takePicture();
					}
					if (poiTyp.equals(VIDEO_POI)) {
						takeVideo();
					}
				}
			}
		});

		addInfo.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		addInfo.show();
	}

	private void drawRoute(double latitude, double longitude) {
		
		Log.i(LOG_TAG, "Draw ROUTE:> " + "latitude: " + latitude + ", longitude" + longitude);
		route.add(new LatLng(latitude, longitude));
		map.addPolyline(route);
	}

	private void startTravel(boolean isNewTravel) {

		isGPSOrNetworkNotVisible();

		if (isNewTravel) {
			serviceIntent = new Intent(TravelMapActivity.this, GPSTrackerService.class);
			serviceIntent.putExtra("travelName", travelName);
			startService(serviceIntent);
			isTravelStart = isNewTravel;
			setAllTravelsAsInactive();
			setThisTravelAsActive();
		}
	}

	private void stopTravel() {
		addStartAndEndPois("end");
		if (serviceIntent == null) {
			serviceIntent = new Intent(TravelMapActivity.this, GPSTrackerService.class);
		}
		unregisterReceiver(reciever);
		stopService(serviceIntent);
		setAllTravelsAsInactive();
		isTravelEnd = true;
	}

	private void setAllTravelsAsInactive() {
		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * from travels", null);

		if (cursor.moveToFirst()) {
			do {
				Log.i(LOG_TAG, cursor.getString(1));
				ContentValues contentValues = new ContentValues();
				contentValues.put("isActive", false);
				db.update("travels", contentValues, null, null);

			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();

	}

	private void setThisTravelAsActive() {
		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * from travels where travelName=\"" + travelName + "\"", null);

		if (cursor.moveToFirst()) {
			Log.i(LOG_TAG, cursor.getString(1));
			ContentValues contentValues = new ContentValues();
			contentValues.put("isActive", true);
			db.update("travels", contentValues, "travelName='" + travelName + "'", null);
		}

		cursor.close();
		db.close();
	}

	private void takePicture() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = new File(Environment.getExternalStorageDirectory(), StartMenuActivity.DIR_NAME + "/" + travelDirName
				+ "/" + travelDirName + "_" + poisArray.size() + ".jpg");

		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

		imageOrVideoUri = Uri.fromFile(photoFile).getPath();

		startActivityForResult(cameraIntent, CAMERA_REQUEST_PICTURE);
	}

	private void takeVideo() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		File videoFile = new File(Environment.getExternalStorageDirectory(), StartMenuActivity.DIR_NAME + "/" + travelDirName
				+ "/" + travelDirName + "_" + poisArray.size() + ".mp4");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));

		imageOrVideoUri = Uri.fromFile(videoFile).getPath();

		startActivityForResult(cameraIntent, CAMERA_REQUEST_VIDEO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((resultCode == RESULT_OK)) {
			if (requestCode == CAMERA_REQUEST_PICTURE) {
				// createThumbnail();
				addPoi(PICTURE_POI, currentPosition, poiNameValue, poiDescriptionValue, imageOrVideoUri, "toDb");
			}
			if (requestCode == CAMERA_REQUEST_VIDEO) {
				addPoi(VIDEO_POI, currentPosition, poiNameValue, poiDescriptionValue, imageOrVideoUri, "toDb");
			}
		} else {
			Toast.makeText(TravelMapActivity.this, R.string.poi_add_error, Toast.LENGTH_LONG).show();
		}
	}

	private void createThumbnail() {

		byte[] imageData = null;

		try {

			final int THUMBNAIL_SIZE = 64;

			// FileInputStream fis = new FileInputStream(imageOrVideoUri);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[16 * 1024];

			Bitmap imageBitmap = BitmapFactory.decodeFile(imageOrVideoUri, options);
			imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 40, 40, true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			imageData = baos.toByteArray();
			//
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/"
					+ StartMenuActivity.DIR_NAME + "/" + travelDirName + "/" + "thumbnails/" + travelDirName + "_"
					+ poisArray.size() + ".jpg");
			fos.write(imageData);
			fos.close();
			//
		} catch (Exception ex) {

		}
	}

	private boolean checkFieldsToNull(String poiName, String poiDescription) {
		if (!poiName.equals("") && !poiDescription.equals("")) {
			return true;
		} else {
			Toast.makeText(this, getResources().getString(R.string.toast_signal_travel_empty), Toast.LENGTH_LONG).show();
		}
		return false;
	}

	private void addPoiToDb(PoiObject poiObject) {

		poisArray.add(poiObject);

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();

		ContentValues poiValues = new ContentValues();
		poiValues.put("travelName", travelName);
		poiValues.put("latitude", poiObject.getMarkerOptions().getPosition().latitude);
		poiValues.put("longitude", poiObject.getMarkerOptions().getPosition().longitude);
		poiValues.put("poiName", poiObject.getMarkerOptions().getTitle());
		poiValues.put("poiDescription", poiObject.getMarkerOptions().getSnippet());
		poiValues.put("markerTyp", poiObject.getMarkerTyp());
		poiValues.put("pathToImageOrVideo", poiObject.getPathToImageOrVideo());

		db.insert("pois", null, poiValues);

		db.close();
	}

	private void addGPSCoordsToDb(Double[] dValues) {
		latitudesAndLongitudesArray.add(dValues);

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();

		ContentValues routeValues = new ContentValues();

		routeValues.put("travelName", travelName);
		routeValues.put("nummer", latitudesAndLongitudesArray.size());
		routeValues.put("latitude", dValues[0]);
		routeValues.put("longitude", dValues[1]);

		db.insert("routes", null, routeValues);

		db.close();
	}

	@SuppressLint("NewApi")
	private void createNotification() {

		Intent notiIntent = new Intent(this, TravelMapActivity.class);
		notiIntent.putExtra("isIntentFromNotification", "fromNotification");

		notiIntent.putExtra("travelDirName", travelDirName);
		notiIntent.putExtra("travelName", travelName);
		notiIntent.putExtra("travelDescription", travelDescription);

		notiIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, notiIntent, 0);

		Notification notification = new Notification.Builder(this)
				.setContentTitle(getResources().getString(R.string.travelDescrTravelNameTV) + " " + travelName)
				.setContentText(getResources().getString(R.string.travelDescrTravelDescrTV) + " " + travelDescription)
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).build();

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(13, notification);

		isNotificationCreated = true;
	}

	private void removeNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(ns);
		notificationManager.cancel(13);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.travel_map_menu_add_textPoi:
			addPoiInfo(TEXT_POI);
			return true;
		case R.id.travel_map_menu_add_picturePoi:
			addPoiInfo(PICTURE_POI);
			return true;
		case R.id.travel_map_menu_add_videoPoi:
			addPoiInfo(VIDEO_POI);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.travel_map_menu, menu);

		textPoiMenuItem = menu.getItem(0);
		picturePoiMenuItem = menu.getItem(1);
		videoPoiMenuItem = menu.getItem(2);

		isMenuActive = true;

		if (!travelOnOffButton.isChecked()) {
			enableDisableMenu(false);
		}

		return super.onCreateOptionsMenu(menu);
	}

	private void enableDisableMenu(boolean onOF) {
		textPoiMenuItem.setEnabled(onOF);
		picturePoiMenuItem.setEnabled(onOF);
		videoPoiMenuItem.setEnabled(onOF);
	}

	@Override
	public void onClick(View v) {

		if (travelOnOffButton.isChecked()) {
			Toast.makeText(getApplicationContext(), "travelStarted", Toast.LENGTH_SHORT).show();
			startTravel(true);
			createNotification();
		} else {
			Toast.makeText(getApplicationContext(), "travelStopped", Toast.LENGTH_SHORT).show();
			stopTravel();
			removeNotification();
			travelOnOffButton.setEnabled(false);
			if (isMenuActive) {
				enableDisableMenu(false);
			}
		}
	}

	private void cameraZoom(LatLng currentPosition) {
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.i(LOG_TAG, "onRestart");
	}

	@Override
	protected void onStart() {
		super.onStart();
		restoreRecieverState();
		Log.i(LOG_TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(LOG_TAG, "OnResume");
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.i(LOG_TAG, "OnPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isTravelEnd) {
			unregisterReceiver(reciever);
			createNotification();
		}
		Log.i(LOG_TAG, "OnSTOP");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i(LOG_TAG, "OnDestroy");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (!isNotificationCreated && travelOnOffButton.isChecked()) {
			createNotification();
		}

		Log.i(LOG_TAG, "onBackPressed");
	}

	@Override
	public void onAttachedToWindow() {
		if (isTravelStart || isTravelEnd) {
			openOptionsMenu();
			closeOptionsMenu();
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putString("poiNameValue", poiNameValue);
		outState.putString("poiDescriptionValue", poiDescriptionValue);
		outState.putString("travelDirName", travelDirName);
		outState.putString("travelName", travelName);
		outState.putString("travelDescription", travelDescription);
		outState.putString("imageOrVideoUri", imageOrVideoUri);

		outState.putBoolean("isMenuActive", isMenuActive);
		outState.putBoolean("isNotificationCreated", isNotificationCreated);
		outState.putBoolean("isIntentFromNotification", isIntentFromNotification);
		outState.putBoolean("isIntentFromMenu", isIntentFromMenu);
		outState.putBoolean("isTravelEnd", isTravelEnd);
		outState.putBoolean("isTravelStart", isTravelStart);

		Log.i(LOG_TAG, "onSaveInstanceState");

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		poiNameValue = savedInstanceState.getString("poiNameValue");
		poiDescriptionValue = savedInstanceState.getString("poiDescriptionValue");
		travelDirName = savedInstanceState.getString("travelDirName");
		travelName = savedInstanceState.getString("travelName");
		travelDescription = savedInstanceState.getString("travelDescription");
		imageOrVideoUri = savedInstanceState.getString("imageOrVideoUri");

		isMenuActive = savedInstanceState.getBoolean("isMenuActive");
		isNotificationCreated = savedInstanceState.getBoolean("isNotificationCreated");
		isIntentFromNotification = savedInstanceState.getBoolean("isIntentFromNotification");
		isIntentFromMenu = savedInstanceState.getBoolean("isIntentFromMenu");
		isTravelStart = savedInstanceState.getBoolean("isTravelStart");
		isTravelEnd = savedInstanceState.getBoolean("isTravelEnd");

		resumeTravel(false);
		Log.i(LOG_TAG, "onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void restoreRecieverState() {

		reciever = new Reciever();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GPSTrackerService.LOCATION_LISTENER_ACTION);
		registerReceiver(reciever, intentFilter);
	}

	private void isGPSOrNetworkNotVisible() {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (currentPosition == null) {
					showSettingsAlert();
				}
				Log.i(LOG_TAG, "Handler in 30 sec");
			}
		}, 30000);
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle(R.string.alert_gps_settings_title);

		alertDialog.setMessage(R.string.alert_gps_settings_message);

		alertDialog.setPositiveButton(R.string.button_gps_settings_positive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});

		alertDialog.setNegativeButton(R.string.button_gps_settings_negative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	private class Reciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			double latitude = intent.getDoubleExtra("latitude", 0.0);
			double longitude = intent.getDoubleExtra("longitude", 0.0);

			if (currentPosition == null) {
				currentPosition = new LatLng(latitude, longitude);
				drawRoute(latitude, longitude);
				if (!isIntentFromNotification || !isIntentFromMenu) {
					addStartAndEndPois("start");
					Log.i(LOG_TAG, "addStartAndEndPois");
				}
			} else {
				if (currentPosition.latitude != latitude || currentPosition.longitude != longitude) {
					drawRoute(latitude, longitude);
					currentPosition = new LatLng(latitude, longitude);
				}
			}
			Log.i(LOG_TAG, "TravelMapLatitude: " + latitude + " , TravelMapLongitude: " + longitude);

		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Log.i(LOG_TAG, "@InfoWindow Clicked" + marker.getId() + " , " + marker.getTitle());
	}
}
