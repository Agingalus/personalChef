package com.personalchef.mealplan.services;

import android.app.Notification;
import android.app.NotificationManager;
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
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.personalchef.mealplan.R;
import com.personalchef.mealplan.StepCounterActivity;
import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * StepsCalculatorService
 *
 * Starts a foreground service with a notification. Notification text is updated on special events - like reaching % of the goal.
 * Keeps track of steps, calculates the calories burnt, miles, etc.
 *
 * Notification has a "Pause" and "Resume" button that is useful to pause or continue tracking steps
 *
 * PENDING To-Do:
 * 1. Have only 1 button in Notification instead of 2 and change the text on event clicks.
 */
public class StepsCalculatorService extends Service implements SensorEventListener  {
    private final int ONGOING_NOTIFICATION_ID = 2;
    private final String TAG_FOREGROUND_SERVICE = "STEPS_CALC_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_CONTINUE = "ACTION_CONTINUE";

    private StepCalorieDetails scDetails = null;
    private User user = null;

    private double MagnitudePrevious = 0;
    public int stepCount;
    private boolean saved = false;
    private double goal = Utilities.goal;
    private double stepLength;
    private double caloriePerMile;
    private double stepsPerMile;
    private boolean running;

    public StepsCalculatorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        scDetails = helper.getStepDetails();
        scDetails.Calculate();
        helper.close();

        user = Utilities.getUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    break;
                case ACTION_CONTINUE:
                    running = true;
                    Toast.makeText(getApplicationContext(), "Cotinuing StepCounter", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    running = false;
                    Toast.makeText(getApplicationContext(), "Paused StepCounter", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    // Sets notification, sensors, and starts fg service
    private void startForegroundService()
    {
        if (!running) {
            // Set Notification
            Notification notification = getMyNotification();

            // Start foreground service with notification
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }

        // set Sensors, Listener, & Register manager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor == null) {
                Toast.makeText(this, "No Step Counter Detected", Toast.LENGTH_LONG).show();
                stopForegroundService();
            }
            else {
                //StepCounterSensorListener stepDetector = new StepCounterSensorListener(this, scDetails, user.getWeight(), user.getHeight());
                running = true;
                stepCount = scDetails.getTotalSteps();
                stepLength = user.getHeight() * 0.42/12;
                caloriePerMile = user.getWeight() * 0.57; //based on causal walking speed
                stepsPerMile = 5280/stepLength;
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
            if (sensorEvent != null && scDetails != null) {
                float x_acceleration = sensorEvent.values[0];
                float y_acceleration = sensorEvent.values[1];
                float z_acceleration = sensorEvent.values[2];
                double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                double MagnitudeDelta = Magnitude - MagnitudePrevious;
                MagnitudePrevious = Magnitude;

                if (MagnitudeDelta > 2) {
                    scDetails.setTotalSteps(stepCount++);
                    update();

                    //PrintStepCount();
                }
                if (stepCount % 5 == 0 && !saved) {
                    //saves data every 10 steps. 10 used for testing purposes, will be much higher for actual app
                    saveToDB();
                    saved = true;
                }
                if (stepCount % 5 == 1 || stepCount % 5 == 2) {
                    saved = false;
                }
            }
        }
    }

    private void saveToDB() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date date = calendar.getTime();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        DatabaseHelper helper = new DatabaseHelper(this);
        helper.addToDb(dayOfWeek, stepCount, scDetails.getCalBurnt(), scDetails.getTotalCal_Intake());
        helper.close();

        Log.i(TAG_FOREGROUND_SERVICE,"Added to DB: " + dayOfWeek + ", " + stepCount + ", " + scDetails.getCalBurnt() + ", " +  scDetails.getTotalCal_Intake());
    }

    private void update() {
        //Log.i("TAG_FOREGROUND_SERVICE", "Into update");

        // Calculate Percentage of steps with goal
        double d = (double)stepCount / goal * 100; //2000  40/160 == 25
        while (d >= 100){ // this WHILE statement is for testing purposes so that the diagram is never stuck being full
            goal += 10;
            d = (double)stepCount / goal * 100;
        }
        int progress = (int) d;
        if (progress == 25 || progress == 50 || progress == 75 || progress == 100) {
            Utilities.NotificationString = "Congratulations! You have completed " + progress + "% of the goal.";
            updateNotification();
        }

        // Calculate Miles
        double miles = stepCount / stepsPerMile;

        // Calculate Average steps
        int avg = (scDetails.getTotalSteps_Week() + stepCount) / scDetails.getAvgSteps();

        // Set values to StepCalorieDetails
        scDetails.setProgress(progress);
        scDetails.setMilesWalked(miles);
        scDetails.setAvgSteps(avg);
        scDetails.setCaloriesBurnt(CalculateCaloriesBurnt());
        Log.i(TAG_FOREGROUND_SERVICE, "Progress: " + progress + ", Miles: " + miles + ", Avg: " + avg);
    }

    public void PrintStepCount() {
        System.out.println("From PrintStepCount\n_____________________________");
        System.out.println(stepCount);
        return;
    }

    private int CalculateCaloriesBurnt() {
        // Get calories from all day/week meals
        // Calculates calorie details
        int caloriesBurned = (int) Math.floor((caloriePerMile / stepsPerMile) * stepCount * 100); // 91.2 / 2138 = cal per step then multiply by stepCount. 100 only used for testing purposes

        return caloriesBurned;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    /**
     * Create a Notification object with all properties and values
     * @return Notification
     */
    private Notification getMyNotification() {
        Intent notificationIntent = new Intent(this, StepCounterActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(Utilities.NotificationString);
        bigText.setBigContentTitle(getString(R.string.app_name));
        bigText.setSummaryText("Steps Progress");

        // Create Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notifChannelId))
                .setSmallIcon(R.drawable.ic_stat_run)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(Utilities.NotificationString)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setStyle(bigText)
                .setTicker("Steps Progress");

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_logo);
        builder.setLargeIcon(largeIconBitmap);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, StepsCalculatorService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(R.drawable.ic_pause_filled, getString(R.string.pause), pendingPrevIntent);
        builder.addAction(prevAction);

        // Add Resume button intent in notification.
        Intent playIntent = new Intent(this, StepsCalculatorService.class);
        playIntent.setAction(ACTION_CONTINUE);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(R.drawable.ic_play_circle_filled, getString(R.string.cont), pendingPlayIntent);
        builder.addAction(playAction);

        return builder.build();
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification() {
        Notification notification = getMyNotification();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        scDetails = null;
        user = null;
        super.onDestroy();
    }
}
