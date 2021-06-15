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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.personalchef.mealplan.Constants;
import com.personalchef.mealplan.IOHelper;
import com.personalchef.mealplan.R;
import com.personalchef.mealplan.StepCounterActivity;
import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class StepsCalculatorService extends Service implements SensorEventListener  {
    private final int ONGOING_NOTIFICATION_ID = 2;
    private final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";

    private StepCalorieDetails scDetails = null;
    private User user = null;

    private double MagnitudePrevious = 0;
    public static Integer stepCount;
    private boolean saved = false;
    private double goal = 30;
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
        helper.close();

        user = Utilities.getUser();

        Log.i("TAG_FOREGROUND_SERVICE", "StepsCalculatorService onCreate(). Did we get scDetails from DB? " + (scDetails != null ? "YES, hooray" : "NO, Oops"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("TAG_FOREGROUND_SERVICE", "onStartCommand foreground service.");

        if (intent != null) {
            //inputText = intent.getStringExtra(Constants.EXTRA_TEXT);
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
                case ACTION_PLAY:   // Resume
                    running = true;
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    running = false;
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void startForegroundService()
    {
        Log.i("TAG_FOREGROUND_SERVICE", "Start foreground service.");

        // Set Notification
        Notification notification = getMyNotification();

        // Notification ID cannot be 0.
        // Start foreground service with notification
        startForeground(ONGOING_NOTIFICATION_ID, notification);

        // set Sensors, Listener, & Register manager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor == null) {
                Toast.makeText(this, "No Step Counter Detected", Toast.LENGTH_LONG).show();
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
                    stepCount++;
                    update();
                    Utilities.NotificationString = "Steps: " + stepCount;
                    updateNotification();
                    PrintStepCount();
                    //add an update to a graph or other visual progress function as a stretch goal
                    Log.i("TAG_FOREGROUND_SERVICE", stepCount + " I am called even when not stepping");
                }
                if (stepCount % 5 == 0 && !saved) {
                    //saves data every 10 steps. 10 used for testing purposes, will be much higher for actual app
                    //*********** helper.addToDb(LocalDate.now().getDayOfWeek().getValue(), stepCount, stepCounter.CalculateCaloriesBurnt(), scDetail.getTotalCal_Intake());
                    saveToDB();

                    Log.i("TAG_FOREGROUND_SERVICE", "mutiple of 10 saved data");
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

        int caloriesBurnt = CalculateCaloriesBurnt();

        DatabaseHelper helper = new DatabaseHelper(this);
        helper.addToDb(dayOfWeek, stepCount, caloriesBurnt, scDetails.getTotalCal_Intake());
        helper.close();

        Log.i("TAG_FOREGROUND_SERVICE","Added to DB: " + dayOfWeek + ", " + stepCount + ", " + caloriesBurnt + ", " +  scDetails.getTotalCal_Intake());
    }

    private void update() {
        Log.i("TAG_FOREGROUND_SERVICE", "Into update");

        // Calculate Percentage of steps with goal
        double d = (double)stepCount / goal * 100; //2000
        while (d >= 100){ // this WHILE statement is for testing purposes so that the diagram is never stuck being full
            goal += 10;
            d = (double)stepCount / goal * 100;
        }
        int progress = (int) d ;
        if (progress == 25 || progress == 50 || progress == 75 || progress == 100) {
            Utilities.NotificationString = "Congratulations! You have completed " + progress + "% of the goal.";
        }

        // Calcualte Miles
        double miles = stepCount / stepsPerMile;

        // Calculate Average steps
        int avg = (scDetails.getTotalSteps_Week() + stepCount) / scDetails.getAvgSteps();

        // Set values to StepCalorieDetails
        scDetails.setProgress(progress);
        scDetails.setMilesWalked(miles);
        scDetails.setAvgSteps(avg);
        Log.i("TAG_FOREGROUND_SERVICE", "Progress: " + progress + ", Miles: " + miles + ", Avg: " + avg);
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

    /*
    private void setStepSensor() {
        Log.i("TAG_FOREGROUND_SERVICE", "Inside setStepSensor()");

        try {
            //sensor instances used to get accelerometer to read steps
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sensorManager != null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (sensor == null) {
                    Toast.makeText(this, "No Step Counter Detected", Toast.LENGTH_LONG).show();
                }
                else {
                    StepCounterSensorListener stepDetector = new StepCounterSensorListener(getApplicationContext(), scDetails, user.getWeight(), user.getHeight());

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
        };*
                    sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        } catch (Exception ex) {
            Log.i("TAG_FOREGROUND_SERVICE", "ERROR: " + ex.getMessage());
        }

    }*/

    private void stopForegroundService()
    {
        Log.d("TAG_FOREGROUND_SERVICE", "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    private Notification getMyNotification() {
        Intent notificationIntent = new Intent(this, StepCounterActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(Utilities.NotificationString);
        bigText.setBigContentTitle(getString(R.string.appTitle));
        bigText.setSummaryText("Steps Progress");

        // Create Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notifChannelId))
                .setSmallIcon(R.drawable.ic_stat_run)
                .setContentTitle(getString(R.string.appTitle))
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
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);

        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, StepsCalculatorService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Resume", pendingPlayIntent);
        builder.addAction(playAction);

        return builder.build();
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification() {
        String text = "Some text that will update the notification";

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
        super.onDestroy();
    }
}

/*
class StepCounterSensorListener implements SensorEventListener {

    private double MagnitudePrevious = 0;
    public static Integer stepCount;
    //public StepCounter stepCounter;
    private boolean saved = false;
    private double goal = 30;
    private StepCalorieDetails scDetail;
    private double stepLength;
    private double caloriePerMile;
    private double stepsPerMile;
    private Context context;

    public StepCounterSensorListener(Context context, StepCalorieDetails scDetails, int userWeight, float userHeight) {
        this.context = context;
        this.scDetail = scDetails;
        //stepCounter = new StepCounter();
        if (scDetails != null) {
            stepCount = scDetail.getTotalSteps();
        }
        stepLength = userHeight * 0.42/12;
        caloriePerMile = userWeight * 0.57; //based on causal walking speed
        stepsPerMile = 5280/stepLength;

        Log.i("TAG_FOREGROUND_SERVICE", "In constructor Got scDetails: " + (scDetail != null ? "YES" : "NO") );
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent != null && scDetail != null) {
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
                Log.i("TAG_FOREGROUND_SERVICE",PrintStepCount() + " I am called even when not stepping");
            }
            if (stepCount%5 == 0 && !saved){
                //saves data every 10 steps. 10 used for testing purposes, will be much higher for actual app
                //*********** helper.addToDb(LocalDate.now().getDayOfWeek().getValue(), stepCount, stepCounter.CalculateCaloriesBurnt(), scDetail.getTotalCal_Intake());
                saveToDB();

                Log.i("TAG_FOREGROUND_SERVICE","mutiple of 10 saved data");
                saved = true;
            }
            if (stepCount%5 == 1 || stepCount%5 == 2){
                saved = false;
            }
        }
    }

    private void saveToDB() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date date = calendar.getTime();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int caloriesBurnt = CalculateCaloriesBurnt();

        DatabaseHelper helper = new DatabaseHelper(context);
        helper.addToDb(dayOfWeek, stepCount, caloriesBurnt, scDetail.getTotalCal_Intake());
        helper.close();

        Log.i("TAG_FOREGROUND_SERVICE","Added to DB: " + dayOfWeek + ", " + stepCount + ", " + caloriesBurnt + ", " +  scDetail.getTotalCal_Intake());
    }

    private void update() {
        Log.i("TAG_FOREGROUND_SERVICE", "Into update");

        // Calculate Percentage of steps with goal
        double d = (double)stepCount / goal * 100; //2000
        while (d >= 100){ // this WHILE statement is for testing purposes so that the diagram is never stuck being full
            goal += 10;
            d = (double)stepCount / goal * 100;
        }
        int progress = (int) d ;
        if (progress == 25 || progress == 50 || progress == 75 || progress == 100) {
            Utilities.NotificationString = "Congratulations! You have completed " + progress + "% of the goal.";
        }

        // Calcualte Miles
        double miles = stepCount / stepsPerMile;

        // Calculate Average steps
        int avg = (scDetail.getTotalSteps_Week() + stepCount) / scDetail.getAvgSteps();

        // Set values to StepCalorieDetails
        scDetail.setProgress(progress);
        scDetail.setMilesWalked(miles);
        scDetail.setAvgSteps(avg);
        Log.i("TAG_FOREGROUND_SERVICE", "Progress: " + progress + ", Miles: " + miles + ", Avg: " + avg);
    }

    public int PrintStepCount() {
        System.out.println("From PrintStepCount\n_____________________________");
        System.out.println(stepCount);
        return stepCount;
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
}
*/