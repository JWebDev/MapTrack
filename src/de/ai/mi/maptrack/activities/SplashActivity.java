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

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		animate();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				finish();

				Intent intent = new Intent(SplashActivity.this, StartMenuActivity.class);
				SplashActivity.this.startActivity(intent);
			}

		}, 4500); 
	}

	private void animate() {
		ImageView mImage;
		Animation animation;

		mImage = (ImageView) findViewById(R.id.splashScreenImage);

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
