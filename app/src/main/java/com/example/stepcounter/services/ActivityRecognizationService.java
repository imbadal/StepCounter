package com.example.stepcounter.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityRecognizationService extends IntentService {

    Timer timer;
    TimerTask task;
    private Handler handler;

    private static final String TAG = "ActivityRecognizationSe";

    public ActivityRecognizationService() {
        super("ActivityRecognizationService");
        handler = new Handler();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            final ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            handledetectedActivity(result.getMostProbableActivity());
                            Log.d("motion_check", "run: ...");
                        }
                    });
                }
            };
            timer.schedule(task, 0, 3000);

        }
    }

    private void handledetectedActivity(DetectedActivity probableActivities) {


        switch (probableActivities.getType()) {

            case DetectedActivity.IN_VEHICLE:
                Log.d("motion_check", "IN_VEHICLE");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", false)
                        .apply();
                break;

            case DetectedActivity.ON_BICYCLE:
                Log.d("motion_check", "ON_BICYCLE");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", true)
                        .apply();
                break;

            case DetectedActivity.ON_FOOT:
                Log.d("motion_check", "ON_FOOT");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", true)
                        .apply();
                break;

            case DetectedActivity.RUNNING:
                Log.d("motion_check", "RUNNING");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", false)
                        .apply();
                break;

            case DetectedActivity.STILL:
                Log.d("motion_check", "STILL");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", false)
                        .apply();
                break;

            case DetectedActivity.WALKING:
                Log.d("motion_check", "WALKING");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", true)
                        .apply();
                break;

            case DetectedActivity.TILTING:
                Log.d("motion_check", "TILTING");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", false)
                        .apply();
                break;

            case DetectedActivity.UNKNOWN:
                Log.d("motion_check", "UNKNOWN");
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", false)
                        .apply();
                break;


        }

    }


}
