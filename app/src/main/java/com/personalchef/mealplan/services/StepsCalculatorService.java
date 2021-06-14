package com.personalchef.mealplan.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.personalchef.mealplan.Constants;
import com.personalchef.mealplan.R;
import com.personalchef.mealplan.StepCounterActivity;
import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;

import java.time.LocalDate;

public class StepsCalculatorService extends Service {
    private final int ONGOING_NOTIFICATION_ID = 2;
    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";

    private String inputText;

    public StepsCalculatorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG_FOREGROUND_SERVICE", "StepsCalculatorService onCreate().");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("TAG_FOREGROUND_SERVICE", "onStartCommand foreground service.");

        if (intent != null) {
            inputText = intent.getStringExtra(Constants.EXTRA_TEXT);
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PLAY:
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForegroundService()
    {
        Log.i("TAG_FOREGROUND_SERVICE", "Start foreground service.");

        Intent notificationIntent = new Intent(this, StepCounterActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(inputText);
        bigText.setBigContentTitle(getString(R.string.appTitle));
        bigText.setSummaryText("Steps Progress");

        // Create Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notifChannelId))
                .setSmallIcon(R.drawable.ic_stat_run)
                .setContentTitle(getString(R.string.appTitle))
                .setContentText(inputText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setStyle(bigText)
                .setTicker("Steps Progress");

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_logo);
        builder.setLargeIcon(largeIconBitmap);

        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, StepsCalculatorService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        builder.addAction(playAction);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, StepsCalculatorService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);

        Notification notification = builder.build();
        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification);

        //do heavy work on a background thread

        setStepSensor();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setStepSensor() {
        Log.i("TAG_FOREGROUND_SERVICE", "Inside setStepSensor()");

        try {
            final StepCalorieDetails[] scDetails = {null};
            new Runnable() {
                @Override
                public void run() {
                    DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                    scDetails[0] = helper.getStepDetails();
                    helper.close();
                }
            };
            // scDetails is null - app stops

            //sensor instances used to get accelerometer to read steps
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            StepCounterSensorListener stepDetector = new StepCounterSensorListener(getApplicationContext(), scDetails[0]);

        /*SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];
                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 2) {
                        stepCount++;
                        update();
                        //add an update to a graph or other visual progress function as a stretch goal
                        System.out.println(stepCounter.GetStepCount() + " I am called even when not stepping");
                    }
                    if (stepCount%5 == 0 && !saved){
                        //saves data every 10 steps. 10 used for testing purposes, will be much higher for actual app
                        helper.addToDb(LocalDate.now().getDayOfWeek().getValue(), stepCount, stepCounter.CalculateCaloriesBurnt(), scDetail.getTotalCal_Intake());
                        //helper.addToDb(2,68,24,25,26,27);

                        System.out.println("mutiple of 10 saved data");
                        saved = true;
                    }
                    if (stepCount%5 == 1 || stepCount%5 == 2){
                        saved = false;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };*/
            sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception ex) {
            Log.i("TAG_FOREGROUND_SERVICE", "ERROR: " + ex.getMessage());
        }

    }

    private void stopForegroundService()
    {
        Log.d("TAG_FOREGROUND_SERVICE", "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

class StepCounterSensorListener implements SensorEventListener {

    private double MagnitudePrevious = 0;
    private Integer stepCount;
    private StepCounter stepCounter;
    private boolean saved = false;
    private double goal = 20;
    private StepCalorieDetails scDetail;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public StepCounterSensorListener(Context context, StepCalorieDetails scDetails) {
        Log.i("TAG_FOREGROUND_SERVICE", "Inside StepcounterSesnsorListener constrictor");
        this.scDetail = scDetails;
        stepCounter = new StepCounter();
        stepCount = scDetail.getTotalSteps();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i("TAG_FOREGROUND_SERVICE", "In onSensorChanged");
        if (sensorEvent != null) {
            float x_acceleration = sensorEvent.values[0];
            float y_acceleration = sensorEvent.values[1];
            float z_acceleration = sensorEvent.values[2];
            double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
            double MagnitudeDelta = Magnitude - MagnitudePrevious;
            MagnitudePrevious = Magnitude;

            if (MagnitudeDelta > 2) {
                stepCount++;
                update();
                //add an update to a graph or other visual progress function as a stretch goal
                Log.i("TAG_FOREGROUND_SERVICE",stepCounter.GetStepCount() + " I am called even when not stepping");
            }
            if (stepCount%5 == 0 && !saved){
                //saves data every 10 steps. 10 used for testing purposes, will be much higher for actual app
                //*********** helper.addToDb(LocalDate.now().getDayOfWeek().getValue(), stepCount, stepCounter.CalculateCaloriesBurnt(), scDetail.getTotalCal_Intake());
                Log.i("TAG_FOREGROUND_SERVICE","To Add to DB: " + LocalDate.now().getDayOfWeek().getValue() + ", " + stepCount + ", " + stepCounter.CalculateCaloriesBurnt() + ", " +  scDetail.getTotalCal_Intake());
                //helper.addToDb(2,68,24,25,26,27);

                Log.i("TAG_FOREGROUND_SERVICE","mutiple of 10 saved data");
                saved = true;
            }
            if (stepCount%5 == 1 || stepCount%5 == 2){
                saved = false;
            }
        }
    }

    private void update() {
        // Calculate Percentage of steps with goal
        double d = (double)stepCount / goal * 100; //2000
        while (d >= 100){ // this WHILE statement is for testing purposes so that the diagram is never stuck being full
            goal += 10;
            d = (double)stepCount / goal * 100;
        }
        int progress = (int) d ;

        // Calcualte Miles
        double miles = stepCount / stepCounter.stepsPerMile;

        // Calculate Average steps
        int avg = (scDetail.getTotalSteps_Week() + stepCount) / scDetail.getAvgSteps();

        // Set values to StepCalorieDetails
        scDetail.setProgress(progress);
        scDetail.setMilesWalked(miles);
        scDetail.setAvgSteps(avg);
        Log.i("TAG_FOREGROUND_SERVICE", "Progress: " + progress + " Miles: " + miles + " Avg: " + avg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}