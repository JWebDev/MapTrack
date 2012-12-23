package de.ai.mi.maptrack.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.ai.mi.maptrack.R;

public class StartMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}
	
    public void showTravelDescriptionActivity(View v){
        Intent intent = new Intent(this, TravelDescriptionActivity.class);
        startActivity(intent);
    }
}
