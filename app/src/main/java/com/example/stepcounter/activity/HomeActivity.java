package com.example.stepcounter.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stepcounter.services.ActivityRecognizationService;
import com.example.stepcounter.R;
import com.example.stepcounter.services.ActivityIntentService;
import com.example.stepcounter.workmanager.MyWorker;
import com.example.stepcounter.model.ModelCity;
import com.example.stepcounter.model.ModelConsolidatedWeather;
import com.example.stepcounter.model.ModelSearch;
import com.example.stepcounter.model.ModelWeather;
import com.example.stepcounter.retrofit.WeatherClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener {

    private static final String TAG = "HomeActivity_TAG";
    String baseUrl = "https://www.metaweather.com/api/";
    String lattlong = "";
    WeatherClient weatherClient;
    TextView city;
    TextView count_steps;
    TextView save;
    EditText editText;
    TextView weather_text;
    TextView weather_type;
    Switch notification;
    String currentCity = "";
    long woeid;
    Retrofit retrofit;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    WorkManager workManager;
    PeriodicWorkRequest periodicWork;

    GoogleApiClient mApiClient;
    int steps = 0;
    int distances = 0;
    float[] history = new float[2];
    private ActivityRecognitionClient mActivityRecognitionClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        notification = findViewById(R.id.switch_notification);
        city = findViewById(R.id.location);
        count_steps = findViewById(R.id.count_steps);
        weather_text = findViewById(R.id.weather_text);
        weather_type = findViewById(R.id.weather_type);
        editText = findViewById(R.id.edit);
        save = findViewById(R.id.save);

        mApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(HomeActivity.this)
                .addOnConnectionFailedListener(HomeActivity.this)
                .build();

        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        manager.registerListener(this, accelerometer, SensorManager.AXIS_MINUS_Y);

        setupRetrofitClient();

        setupWorkManager();

        checkNotification();

        initilizGoogleAPIClient();

        getCurrentCity();

    }


    public void requestUpdatesHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                3000,
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("motion_check_", "onSuccess: **********");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("motion_check_", "onFailed: **********");

            }
        });
    }

    //Get a PendingIntent//
    private PendingIntent getActivityDetectionPendingIntent() {
//Send the activity data to our DetectedActivitiesIntentService class//
        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    private void initilizGoogleAPIClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    private void setupWorkManager() {

        workManager = WorkManager.getInstance();
        periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 16, TimeUnit.MINUTES)
                .addTag("NOTIFY")
                .build();

    }

    private void checkNotification() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getBoolean("isEnable", false))
            notification.setChecked(true);

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    workManager.enqueue(periodicWork);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isEnable", true);
                    editor.apply();
                } else {
                    workManager.cancelAllWorkByTag("NOTIFY");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isEnable", false);
                    editor.apply();
                }
            }
        });

    }

    private void setupRetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherClient = retrofit.create(WeatherClient.class);
    }

    private void getCityWeather(long woeid) {

        Log.d(TAG, "getCityWeather: Start" + woeid);

        Call<ModelWeather> weatherCall = weatherClient.getWeather(woeid);
        weatherCall.enqueue(new Callback<ModelWeather>() {
            @Override
            public void onResponse(Call<ModelWeather> call, Response<ModelWeather> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponseFailed: " + response.code());
                    return;
                }
                ModelWeather modelWeather = response.body();

                List<ModelConsolidatedWeather> list = modelWeather.getWeatherDtails();

                Log.d(TAG, "onResponse: Size" + list.size());
                weather_type.setText(list.get(0).getWeather_state_name());
                weather_text.setText("" + currentCity + "'s Weather Condition");
            }

            @Override
            public void onFailure(Call<ModelWeather> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    public long getCurrentCity() {

        final Call<List<ModelCity>> cityList = weatherClient.getCity(lattlong);

        cityList.enqueue(new Callback<List<ModelCity>>() {
            @Override
            public void onResponse(Call<List<ModelCity>> call, Response<List<ModelCity>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                List<ModelCity> list = response.body();
                currentCity = list.get(0).getTitle();
                woeid = list.get(0).getWoeid();

                Log.d(TAG, "onResponse: wID " + woeid);
                getCityWeather(woeid);
                city.setText(currentCity);
                Log.d(TAG, "" + list.get(0).getTitle());

            }

            @Override
            public void onFailure(Call<List<ModelCity>> call, Throwable t) {

            }
        });

        return woeid;
    }

    public void changeCity(View view) {
        String city_name = "";
        if (editText.getText().toString().trim().length() > 0)
            city_name = editText.getText().toString();
        else
            city_name = currentCity;

        Call<List<ModelSearch>> modelCityCall = weatherClient.searchCity(city_name);

        modelCityCall.enqueue(new Callback<List<ModelSearch>>() {
            @Override
            public void onResponse(Call<List<ModelSearch>> call, Response<List<ModelSearch>> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    return;
                }

                List<ModelSearch> list = response.body();
                currentCity = list.get(0).getTitle();
                woeid = list.get(0).getWoeid();
                city.setText(currentCity);
                getCityWeather(woeid);
                Log.d(TAG, "onResponse: Search " + list.get(0).getTitle());
            }

            @Override
            public void onFailure(Call<List<ModelSearch>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });

        editText.setText(null);
        editText.clearFocus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestUpdatesHandler();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "onConnected: no permission###");
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d(TAG, "onConnected: no permission***");

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            lattlong = currentLatitude + "," + currentLongitude;

            getCurrentCity();

            Log.d(TAG, "onConnected: " + currentLatitude + currentLatitude);
            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(HomeActivity.this, ActivityRecognizationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(HomeActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mActivityRecognitionClient.requestActivityUpdates(3000, pendingIntent);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(TAG, "onPointerCaptureChanged:");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        boolean check = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("WALKING", false);

        Log.d("motion_check", "--------------onSensorChanged:  " + check);

        if (check) {

            float xChange = history[0] - event.values[0];

            history[0] = event.values[0];
            history[1] = event.values[1];

            if (xChange > .2) {
                distances++;
            } else if (xChange < -.2) {
                distances++;
            }

            if (distances >= 15) {
                steps++;
                distances = 0;
            }

            count_steps.setText(String.valueOf(steps));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
