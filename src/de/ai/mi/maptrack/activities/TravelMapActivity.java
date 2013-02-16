package de.ai.mi.maptrack.activities;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.DatabaseHelper;
import de.ai.mi.maptrack.src.GPSTrackerService;
import de.ai.mi.maptrack.src.PoiObject;

public class TravelMapActivity extends Activity implements OnClickListener {

	private ToggleButton travelOnOffButton;
	private MenuItem textPoiMenuItem;
	private MenuItem picturePoiMenuItem;
	private MenuItem videoPoiMenuItem;

	private final String TEXT_POI = "TEXT_POI";
	private final String PICTURE_POI = "PICTURE_POI";
	private final String VIDEO_POI = "VIDEO_POI";
	private final String LOG_TAG = "TRAVEL_MAP";

	private static final int CAMERA_REQUEST_PICTURE = 110;
	private static final int CAMERA_REQUEST_VIDEO = 111;

	private String poiNameValue;
	private String poiDescriptionValue;
	private String travelDirName;
	private String travelName;
	private String travelDescription;

	private Uri imageOrVideoUri;
	
	private LatLng currentPosition = null;
	private GoogleMap map;

	private Reciever reciever;
	private Intent serviceIntent;
	private PolylineOptions route;

	private ArrayList<Double[]> latitudesAndLongitudesArray = new ArrayList<Double[]>();
	private ArrayList<PoiObject> poisArray = new ArrayList<PoiObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_map_activity);

		Intent intent = getIntent();
		travelDirName = intent.getStringExtra("travelDirName");
		travelName = intent.getStringExtra("travelName");
		travelDescription = intent.getStringExtra("travelDescription");

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		init();
	}

	private void init() {

		travelOnOffButton = (ToggleButton) findViewById(R.id.travel_start_stop);
		travelOnOffButton.setOnClickListener(this);

		// gpsService = new GPSTrackerService(TravelMapActivity.this);
		//
		// if (gpsService.canGetLocation()) {
		//
		// latitude = gpsService.getLatitude();
		// longitude = gpsService.getLongitude();
		//
		// Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
		// + latitude + "\nLong: " + longitude,
		// Toast.LENGTH_LONG).show();
		// System.out.println("Your Location is - \nLat: " + latitude +
		// "\nLong: " + longitude);
		// } else {
		// gpsService.showSettingsAlert();
		// }
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

		poisArray.add(new PoiObject(markerOptions, TEXT_POI, null));

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
	}

	private void addPoi(String poiTyp) {
		// Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
		// + currentPosition.latitude + "\nLong: " + currentPosition.longitude,
		// Toast.LENGTH_SHORT).show();

		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(currentPosition);
		markerOptions.title(poiNameValue);
		markerOptions.snippet(poiDescriptionValue);

		// map.setInfoWindowAdapter(new InfoWindowAdapter() {
		//
		// private final View contens =
		// getLayoutInflater().inflate(R.layout.picture_poi_box, null);
		//
		// @Override
		// public View getInfoWindow(Marker marker) {
		// return null;
		// }
		//
		// @Override
		// public View getInfoContents(Marker marker) {
		//
		// return contens;
		// }
		// });

		if (poiTyp.equals(TEXT_POI)) {
			// markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.text_poi));
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
		} else if (poiTyp.equals(PICTURE_POI)) {
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
			poisArray.add(new PoiObject(markerOptions, PICTURE_POI, imageOrVideoUri.getPath()));
			// markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picture_poi));
		} else if (poiTyp.equals(VIDEO_POI)) {
			bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			// markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.video_poi));
		}
		markerOptions.icon(bitmapDescriptor);

		map.addMarker(markerOptions);

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
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
						addPoi(TEXT_POI);
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
		route.add(new LatLng(latitude, longitude));
		map.addPolyline(route);
	}

	private void startTravel() {

		reciever = new Reciever();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GPSTrackerService.LOCATION_LISTENER_ACTION);
		registerReceiver(reciever, intentFilter);

		serviceIntent = new Intent(TravelMapActivity.this, GPSTrackerService.class);
		startService(serviceIntent);

		route = new PolylineOptions();
		route.color(Color.RED);

		// currentPosition = new LatLng(latitude, longitude);
		//
		// Toast.makeText(getApplicationContext(),
		// "Your Location is - \nLat: " + currentPosition.latitude + "\nLong: "
		// + currentPosition.longitude,
		// Toast.LENGTH_LONG).show();
		// Marker marker = map.addMarker(new
		// MarkerOptions().position(currentPosition).title("currentPosition")
		// .snippet("currentPosition is cool").draggable(true));
		//
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,
		// 15));
		// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}

	private void stopTravel() {
		unregisterReceiver(reciever);
		stopService(serviceIntent);
		addStartAndEndPois("end");
		saveTravel();
	}

	private void saveLatitudeAndLongitude(double latitude, double longitude) {
		Double[] dValues = { latitude, longitude };
		latitudesAndLongitudesArray.add(dValues);
	}

	private void takePicture() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = new File(Environment.getExternalStorageDirectory(), StartMenuActivity.DIR_NAME + "/" + travelDirName
				+ "/" + travelDirName + "_" + poisArray.size() + ".jpg");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

		imageOrVideoUri = Uri.fromFile(photoFile);

		startActivityForResult(cameraIntent, CAMERA_REQUEST_PICTURE);
	}

	private void takeVideo() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		File videoFile = new File(Environment.getExternalStorageDirectory(), StartMenuActivity.DIR_NAME + "/" + travelDirName
				+ "/" + travelDirName + "_" + poisArray.size() + ".mp4");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));

		imageOrVideoUri = Uri.fromFile(videoFile);

		startActivityForResult(cameraIntent, CAMERA_REQUEST_VIDEO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_REQUEST_PICTURE) {
				addPoi(PICTURE_POI);
			}
			if (requestCode == CAMERA_REQUEST_VIDEO) {
				addPoi(VIDEO_POI);
			}
			// Uri selectedUri = imageOrVideoUri;
			// Bitmap photo = (Bitmap) data.getExtras().get("data");
			// imageView.setImageBitmap(photo);
		} else {
			Toast.makeText(TravelMapActivity.this, R.string.poi_add_error, Toast.LENGTH_LONG).show();
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

	private void saveTravel() {

		SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
		ContentValues routeValues = new ContentValues();
		ContentValues poisValues = new ContentValues();

		int i=0;
		for (Double[] elem : latitudesAndLongitudesArray) {
			
			routeValues.put("travelName", travelName);
			routeValues.put("nummer", i++);
			routeValues.put("latitude", elem[0]);
			routeValues.put("longitude", elem[1]);
		}

		for (PoiObject poiObject : poisArray) {
			poisValues.put("travelName", travelName);
			poisValues.put("latitude", poiObject.getMarkerOptions().getPosition().latitude);
			poisValues.put("longitude", poiObject.getMarkerOptions().getPosition().longitude);
			poisValues.put("poiName", poiObject.getMarkerOptions().getTitle());
			poisValues.put("poiDescription", poiObject.getMarkerOptions().getSnippet());
			poisValues.put("markerTyp", poiObject.getMarkerTyp());
			poisValues.put("pathToImageOrVideo", poiObject.getMarkerLink());	
		}

		db.insert("routes", null, routeValues);
		db.insert("pois", null, poisValues);
		db.close();
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
		
		enableDisableMenu(false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private void enableDisableMenu(boolean onOF){
		textPoiMenuItem.setEnabled(onOF);
		picturePoiMenuItem.setEnabled(onOF);
		videoPoiMenuItem.setEnabled(onOF);
	}

	@Override
	public void onClick(View v) {

		if (travelOnOffButton.isChecked()) {
			Toast.makeText(getApplicationContext(), "travelStarted", Toast.LENGTH_SHORT).show();
			enableDisableMenu(true);
			startTravel();
		} else {
			Toast.makeText(getApplicationContext(), "travelStopped", Toast.LENGTH_SHORT).show();
			enableDisableMenu(false);
			stopTravel();
			travelOnOffButton.setEnabled(false);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.i(LOG_TAG, "onRestart");
		
		//Vielleicht brauche ich nicht
		 IntentFilter intentFilter = new IntentFilter();
		 intentFilter.addAction(GPSTrackerService.LOCATION_LISTENER_ACTION);
		 registerReceiver(reciever, intentFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();

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

		Log.i(LOG_TAG, "onBackPressed");
	}

	private class Reciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			double latitude = intent.getDoubleExtra("latitude", 0.0);
			double longitude = intent.getDoubleExtra("longitude", 0.0);

			if (currentPosition == null) {
				currentPosition = new LatLng(latitude, longitude);
				drawRoute(latitude, longitude);
				addStartAndEndPois("start");
				saveLatitudeAndLongitude(latitude, longitude);
			} else {
				if (currentPosition.latitude != latitude || currentPosition.longitude != longitude) {
					drawRoute(latitude, longitude);
					currentPosition = new LatLng(latitude, longitude);
					saveLatitudeAndLongitude(latitude, longitude);
				}
			}

			Toast.makeText(TravelMapActivity.this,
					"TravelMapLatitudeOnRecive: " + latitude + " , TravelMapLongitudeOnRecive: " + longitude, Toast.LENGTH_SHORT)
					.show();
			Log.i(LOG_TAG, "TravelMapLatitude: " + latitude + " , TravelMapLongitude: " + longitude);

		}
	}
}
