package com.example.taskmanagerpro;

import static android.os.Build.VERSION_CODES.R;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

	private static final int AUTO_SKIP_DELAY = 3000; // 3 seconds
	private MediaPlayer mediaPlayer;
	private boolean isSkipped = false;
	private ProgressBar progressBar;
	private Handler progressHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit);
		}

		TextView welcomeText = findViewById(R.id.welcomeText);
		Button enterBtn = findViewById(R.id.enterButton);
		progressBar = findViewById(R.id.progressBar);

		Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
		welcomeText.startAnimation(slideUp);
		enterBtn.startAnimation(slideUp);

		mediaPlayer = MediaPlayer.create(this, R.raw.win7startup);
		mediaPlayer.start();

		enterBtn.setOnClickListener(v -> goToMainActivity());

		startProgressBarAnimation();

		new Handler().postDelayed(() -> {
			if (!isSkipped) goToMainActivity();
		}, AUTO_SKIP_DELAY);
	}

	private void startProgressBarAnimation() {
		final int duration = AUTO_SKIP_DELAY;
		final int updateInterval = 30;
		final int maxProgress = 100;

		progressHandler.postDelayed(new Runnable() {
			long startTime = System.currentTimeMillis();
			@Override
			public void run() {
				if (isSkipped) return;

				long elapsed = System.currentTimeMillis() - startTime;
				int progress = (int) (elapsed * maxProgress / duration);
				progressBar.setProgress(progress);

				if (elapsed < duration) {
					progressHandler.postDelayed(this, updateInterval);
				}
			}
		}, 0);
	}

	private void goToMainActivity() {
		isSkipped = true;
		progressHandler.removeCallbacksAndMessages(null); // stop progress updates

		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}

		startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
		overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit);
		finish();
	}
}
