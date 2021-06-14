package com.personalchef.mealplan;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;
import com.personalchef.mealplan.models.User;

import java.time.LocalDate;

public class StepCounterActivity extends AppCompatActivity {
    private DatabaseHelper helper = null;
    private double MagnitudePrevious = 0;
    public static Integer stepCount;
    public static StepCounter stepCounter;
    public static StepCalorieDetails scDetail;
    public static boolean saved = false;
    public static int avg = 0;
    public static double goal = 20;
    public static double miles = 0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stepCounter = new StepCounter();
        setContentView(R.layout.activity_step_counter);


        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        helper = new DatabaseHelper(this);
        scDetail = helper.getStepDetails(); // (StepCalorieDetails) intent.getSerializableExtra(StepCalorieDetails.EXTRA_STEPCALDETAIL_OBJ);

        populateTextViews(u, scDetail);


        stepCount = scDetail.getTotalSteps();
        //Log.i("MP", "Into StepCount activity");

/*
        //sensor instances used to get accelerometer to read steps
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener stepDetector = new SensorEventListener() {
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
        };
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTextViews(User u, StepCalorieDetails scDetail) {
        // Retrieve Views
        // User
        TextView tvDate = findViewById(R.id.tvDate);

        // Step-Calorie
        TextView tvTodaysSteps = findViewById(R.id.tvTodaysSteps);
        TextView tvBurnedCals = findViewById(R.id.tvBurnedCals);
        TextView tv_TotalStepsThisWeek = findViewById(R.id.tv_TotalStepsThisWeek);
        TextView tv_TotalCalsIntake = findViewById(R.id.tv_TotalCalsIntake);
        TextView tv_TotalCalsBurned = findViewById(R.id.tv_TotalCalsBurned);
        TextView stepCountV = findViewById(R.id.number_of_calories);
        TextView tv_aSteps = findViewById(R.id.avgSteps);


        // Set Values to text views
        tvDate.setText(LocalDate.now().toString());

        System.out.println(scDetail.getTotalSteps());

        tvTodaysSteps.setText(String.valueOf(scDetail.getTotalSteps()));
        tvBurnedCals.setText(String.valueOf(scDetail.getCalBurnt()));
        tv_TotalStepsThisWeek.setText(String.valueOf(scDetail.getTotalSteps_Week()));
        tv_TotalCalsIntake.setText(String.valueOf(scDetail.getTotalCal_Intake()));
        tv_TotalCalsBurned.setText(String.valueOf(scDetail.getTotalCal_Burned()));
        if (scDetail.getAvgSteps() != 0){
            avg = (scDetail.getTotalSteps_Week() + scDetail.getTotalSteps()) / scDetail.getAvgSteps();
        }
        tv_aSteps.setText("Avg Steps: "+ avg);

        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(){
        TextView stepCountV = findViewById(R.id.number_of_calories);
        // Calculate the slice size and update the pie chart:
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double d = (double)stepCount / goal * 100; //2000
        while (d >= 100){ // this WHILE statement is for testing purposes so that the diagram is never stuck being full
            goal += 10;
            d = (double)stepCount / goal * 100;
        }
        int progress = (int) d ;
        TextView tvMiles = findViewById(R.id.tvMiles);

        miles = stepCount / stepCounter.stepsPerMile;

        tvMiles.setText("Distance Walked (per Mile): " + String.format("%.2f", miles));

        stepCountV.setText(stepCount.toString() + " / " + (int)goal );//goal.toString());

        pieChart.setProgress(progress);
        //User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        //populateTextViews(u, scDetail);

        TextView tvTodaysSteps = findViewById(R.id.tvTodaysSteps);
        tvTodaysSteps.setText(stepCount.toString());

        TextView tvBurnedCals = findViewById(R.id.tvBurnedCals);
        tvBurnedCals.setText(stepCounter.CalculateCaloriesBurntString());

        TextView tv_TotalStepsThisWeek = findViewById(R.id.tv_TotalStepsThisWeek);
        StepCalorieDetails scDetail = helper.getStepDetails();
        tv_TotalStepsThisWeek.setText(String.valueOf(scDetail.getTotalSteps_Week() + stepCount));


        TextView tv_TotalCalsBurned = findViewById(R.id.tv_TotalCalsBurned);
        tv_TotalCalsBurned.setText(String.valueOf(scDetail.getTotalCal_Burned() +stepCounter.CalculateCaloriesBurnt()));

        TextView tv_aSteps = findViewById(R.id.avgSteps);
        avg = (scDetail.getTotalSteps_Week() + stepCount) / scDetail.getAvgSteps();

        tv_aSteps.setText("Avg Steps: "+ avg);

    }
    public void onHomeClicked(View view) {
        onBackPressed();
    }
    protected void onDestroy() {
        System.out.println("app is destroyed and saved data");
        System.out.println("im a stepCounter " + stepCounter.GetStepCount());

       //helper.insert(stepCount, stepCounter.CalculateCaloriesBurnt(), scDetail.getTotalSteps_Week(), scDetail.getTotalCal_Intake(), scDetail.getTotalCal_Burned());

        //helper.close();
        super.onDestroy();
    }
}

