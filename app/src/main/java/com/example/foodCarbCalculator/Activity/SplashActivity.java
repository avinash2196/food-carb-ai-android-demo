package com.example.foodCarbCalculator.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodCarbCalculator.R;

/**
 * The app's entry point, showing a branded launch screen before navigating to
 * {@link MainActivity}.
 *
 * <p><b>Purpose:</b> Provides a short visual pause while the app initializes, then
 * transitions to the main screen.</p>
 *
 * <p><b>Learning Note:</b> {@link Handler} is constructed with
 * {@link Looper#getMainLooper()} to avoid the deprecation warning introduced in
 * API 30, where the no-arg {@code new Handler()} constructor was deprecated in favour
 * of explicitly specifying which {@link Looper} the handler should run on.</p>
 *
 * <p><b>Design Note:</b> For production apps targeting API 31+, the Android
 * SplashScreen API (or the {@code androidx.core:core-splashscreen} compatibility
 * library) is the recommended approach. This simple Handler-based pattern is used
 * here for broad compatibility and clarity.</p>
 */
public class SplashActivity extends AppCompatActivity {

    /** Duration of the splash screen in milliseconds. */
    private static final long SPLASH_DURATION_MS = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}
