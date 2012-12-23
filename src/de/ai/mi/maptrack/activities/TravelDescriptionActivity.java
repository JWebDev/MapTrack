package de.ai.mi.maptrack.activities;

import de.ai.mi.maptrack.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TravelDescriptionActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_description_activity);
	}
	
    public void showTravelMapActivity(View v){
        Intent intent = new Intent(this, TravelMapActivity.class);
        startActivity(intent);
    }

}
