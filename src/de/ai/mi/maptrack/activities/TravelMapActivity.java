package de.ai.mi.maptrack.activities;

import com.google.android.maps.MapActivity;

import de.ai.mi.maptrack.R;
import android.app.Activity;
import android.os.Bundle;

public class TravelMapActivity extends MapActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_map_activity);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
