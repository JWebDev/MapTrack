package de.ai.mi.maptrack.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import de.ai.mi.maptrack.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		animate();

		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				// make sure we close the splash screen so the user won't come
				// back when it presses back key

				finish();
				// start the home screen

				Intent intent = new Intent(SplashActivity.this, StartMenuActivity.class);
				SplashActivity.this.startActivity(intent);
			}

		}, 8000); // time in milliseconds (1 second = 1000 milliseconds) until
					// the run() method will be called

	}

	private void animate() {
		ImageView mImage;
		Animation animation;

		mImage = (ImageView) findViewById(R.id.splashScreenImage);
		
//		mImage.setImageResource(R.drawable.splash_screen_map_background);
//		animation = AnimationUtils.loadAnimation(this, R.anim.splash_map_background_scale);
//		mImage.startAnimation(animation);

		animation = AnimationUtils.loadAnimation(this, R.anim.splash_map_fade_in);
		mImage.startAnimation(animation);
		
		mImage = (ImageView) findViewById(R.id.splashScreenPointBlue);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_blue_fade_in);
		mImage.startAnimation(animation);
		mImage = (ImageView) findViewById(R.id.splashScreenPointGreen);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_green_fade_in);
		mImage.startAnimation(animation);
		mImage = (ImageView) findViewById(R.id.splashScreenPointMagenta);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_magenta_fade_in);
		mImage.startAnimation(animation);
		mImage = (ImageView) findViewById(R.id.splashScreenPointRed);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_red_fade_in);
		mImage.startAnimation(animation);
		mImage = (ImageView) findViewById(R.id.splashScreenPointYellow);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_yellow_fade_in);
		mImage.startAnimation(animation);
		mImage = (ImageView) findViewById(R.id.splashScreenTextMap);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_texts_fade_in);
		mImage.startAnimation(animation);
		mImage = (ImageView) findViewById(R.id.splashScreenTextTrack);
		animation = AnimationUtils.loadAnimation(this, R.anim.splash_point_texts_fade_in);
		mImage.startAnimation(animation);
	}

}
