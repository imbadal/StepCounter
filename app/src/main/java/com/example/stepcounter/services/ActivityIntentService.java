package com.example.stepcounter.services;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityIntentService extends IntentService {
    protected static final String TAG = "Activity";

    String motions = "";

    public ActivityIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleMostProbableActivity(result.getMostProbableActivity());
        }
    }



    private void handleMostProbableActivity(DetectedActivity probableActivities) {


        switch (probableActivities.getType()) {

            case DetectedActivity.STILL:
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", false)
                        .apply();
                motions += "STILL......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "STILL" + false);
                break;

            case DetectedActivity.IN_VEHICLE:
                motions += "IN_VEHICLE......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "IN_VEHICLE");
                break;

            case DetectedActivity.ON_BICYCLE:
                motions += "ON_BICYCLE......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "ON_BICYCLE");
                break;


            case DetectedActivity.RUNNING:

                motions += "RUNNING......";
                Log.d("motion_check", "RUNNING");
//                    MainActivity.textView.setText(motions);

                break;


            case DetectedActivity.TILTING:
                motions += "TILTING......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "TILTING");
                break;

            case DetectedActivity.UNKNOWN:

                motions += "UNKNOWN......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "UNKNOWN");
                break;

            case DetectedActivity.ON_FOOT:
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", true)
                        .apply();
                motions += "ON_FOOT......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "ON_FOOT");
                break;

            case DetectedActivity.WALKING:
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean("WALKING", true)
                        .apply();
                motions += "WALKING......";
//                    MainActivity.textView.setText(motions);
                Log.d("motion_check", "Walking" + true);
                break;

        }

    }

}
