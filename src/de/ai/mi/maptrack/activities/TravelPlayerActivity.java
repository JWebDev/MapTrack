package de.ai.mi.maptrack.activities;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.src.MapFrameLayout;
import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;

public class TravelPlayerActivity extends Activity {
	
	private MapView mapView;
	private LocationManager locationManager;
	private MapController mapController;
	private Location prevLocation;
	private long distanceTraveled;
	private MapFrameLayout mapFrameLayout;
	private boolean tracking;
	private long startTime;
	private PowerManager.WakeLock wakeLock;
	private boolean gpsFix;
	private static final double MILLISEC_PER_HOUR = 1000 * 60 * 60;
	private static final double MILLISEC_PER_KM = 0.621371192;
	private static final int MAP_ZOOM = 18;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_map_activity);
		//mapView = (MapView) findViewById(R.id.travelMapView);

		mapView.setClickable(true); // обеспечение взаимодействия
		// пользователя с картой
		mapView.setEnabled(true); // активизация генерирования событий
		// компонентом MapView
		mapView.setSatellite(false); // отображение традиционной карты
		mapView.setBuiltInZoomControls(true); // активизация элементов

		// // создание нового MapView с помощью ключа Google Maps API
		// mapFrameLayout = new MapFrameLayout(this,
		// getResources().getString(R.string.google_maps_api_key));
		//
		// // добавление bearingFrameLayout в основную разметку mainLayout
		// FrameLayout mainLayout = (FrameLayout)
		// findViewById(R.id.mapFrameLayout);
		// mainLayout.addView(mapFrameLayout, 0);
		// // получение MapView и MapController
		// mapView = mapFrameLayout.getMapView();
		// mapController = mapView.getController(); // получение MapController
		// mapController.setZoom(MAP_ZOOM); // увеличение масштаба карты
		// // создание слоя карты
		// routeOverlay = new RouteOverlay();
		// // добавление слоя RouteOverlay
		// mapView.getOverlays().add(routeOverlay);
		// distanceTraveled = 0; // инициализация distanceTraveled значением 0

	}
}
