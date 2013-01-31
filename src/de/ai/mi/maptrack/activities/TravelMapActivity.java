package de.ai.mi.maptrack.activities;

import android.app.Activity;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import de.ai.mi.maptrack.R;
import de.ai.mi.maptrack.R.layout;
import de.ai.mi.maptrack.src.MapFrameLayout;
import de.ai.mi.maptrack.src.RouteOverlay;

public class TravelMapActivity extends android.support.v4.app.FragmentActivity {

	private MapView mapView;
//	private LocationManager locationManager;
//	private MapController mapController;
//	private Location prevLocation;
//	private RouteOverlay routeOverlay;
//	private long distanceTraveled;
//	private MapFrameLayout mapFrameLayout;
//	private boolean tracking;
//	private long startTime;
//	private PowerManager.WakeLock wakeLock;
//	private boolean gpsFix;
//	private static final double MILLISEC_PER_HOUR = 1000 * 60 * 60;
//	private static final double MILLISEC_PER_KM = 0.621371192;
//	private static final int MAP_ZOOM = 18;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_map_activity);
	}
}
