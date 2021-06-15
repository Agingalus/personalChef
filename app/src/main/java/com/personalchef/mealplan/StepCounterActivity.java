package com.personalchef.mealplan;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;
import com.personalchef.mealplan.models.User;

public class StepCounterActivity extends AppCompatActivity {
    private DatabaseHelper helper = null;
    private double MagnitudePrevious = 0;
    public static Integer stepCount = 0;
    public  static StepCounter stepCounter;
    private boolean first = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stepCounter = new StepCounter();
        setContentView(R.layout.activity_step_counter);

        //Log.i("MP", "Into StepCount activity");

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
                        System.out.println(stepCounter.GetStepCount());
                    }

                }
                if(stepCount ==3 && first){
                    //StepCounter stepCounter= new StepCounter();
                    first = false;
                    helper.insert(41, 51, 61, 71, 81);
                }
                if (stepCount == 5 && !first){
                    first = true;
                    StepCalorieDetails stepCalorieDetails = helper.getStepDetails();
                    System.out.println(stepCalorieDetails.getCalBurnt() + " This is from the DB. YAY!!!!!");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);




        // GET PARAMETERS PASSEd
        Intent intent = getIntent();

        //User user = Utilities.getUser(); // (User) intent.getSerializableExtra(User.EXTRA_USEROBJ);
        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        helper = new DatabaseHelper(this);
        //StepCalorieDetails scDetail = helper.getStepDetails(); // (StepCalorieDetails) intent.getSerializableExtra(StepCalorieDetails.EXTRA_STEPCALDETAIL_OBJ);

        //populateTextViews(u, scDetail);
    }

    private void populateTextViews(User u, StepCalorieDetails scDetail) {
        // Retrieve Views
        // User
//        TextView tvHeight = findViewById(R.id.tvHeight);
//        TextView tvWeight = findViewById(R.id.tvWeight);

        // Step-Calorie
        TextView tvTodaysSteps = findViewById(R.id.tvTodaysSteps);
        TextView tvBurnedCals = findViewById(R.id.tvBurnedCals);
        TextView tv_TotalStepsThisWeek = findViewById(R.id.tv_TotalStepsThisWeek);
        TextView tv_TotalCalsIntake = findViewById(R.id.tv_TotalCalsIntake);
        TextView tv_TotalCalsBurned = findViewById(R.id.tv_TotalCalsBurned);
        TextView stepCountV = findViewById(R.id.number_of_calories);


        stepCountV.setText(stepCount.toString());
        // Set Values to text views
//        tvHeight.setText(Float.toString(user.getHeight()));
//        tvWeight.setText(String.valueOf(user.getWeight()));

        tvTodaysSteps.setText(String.valueOf(scDetail.getTotalSteps()));
        tvBurnedCals.setText(String.valueOf(scDetail.getCalBurnt()));
        tv_TotalStepsThisWeek.setText(String.valueOf(scDetail.getTotalSteps_Week()));
        tv_TotalCalsIntake.setText(String.valueOf(scDetail.getTotalCal_Intake()));
        tv_TotalCalsBurned.setText(String.valueOf(scDetail.getTotalCal_Burned()));

        return;
    }

    public void update(){
        TextView stepCountV = findViewById(R.id.number_of_calories);
        stepCountV.setText(stepCount.toString() + " / 20" );//goal.toString());
        // Calculate the slice size and update the pie chart:
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double d = (double)stepCount / 20 * 100; //2000
        int progress = (int) d ;
        pieChart.setProgress(progress);
        TextView tvTodaysSteps = findViewById(R.id.tvTodaysSteps);
        tvTodaysSteps.setText(stepCount.toString());

        TextView tvBurnedCals = findViewById(R.id.tvBurnedCals);
        tvBurnedCals.setText(stepCounter.CalculateCaloriesBurntString());

        TextView tv_TotalStepsThisWeek = findViewById(R.id.tv_TotalStepsThisWeek);
        StepCalorieDetails scDetail = helper.getStepDetails();
        tv_TotalStepsThisWeek.setText(String.valueOf(scDetail.getTotalSteps_Week() + stepCount));


        TextView tv_TotalCalsBurned = findViewById(R.id.tv_TotalCalsBurned);
        tv_TotalCalsBurned.setText(String.valueOf(scDetail.getTotalCal_Burned() +stepCounter.CalculateCaloriesBurnt()));
    }
    /*public void onHomeClicked(View view) {
        onBackPressed();
    }*/

}

