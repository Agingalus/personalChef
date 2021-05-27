package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;
import com.personalchef.mealplan.models.Utilities;

public class MainActivity extends AppCompatActivity {
    private double MagnitudePrevious = 0;
    public static Integer stepCount = 0;
    public  static StepCounter stepCounter= new StepCounter();
    private boolean first = true;
    private final DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ///when activity is  created user gets the text for the joke of the day here
        TextView textViewjoke=findViewById(R.id.tv_textJoke);
        textViewjoke.setText(Utilities.GetJoke());
    }

    // View Meal btn click
    public void onViewMealButton(View view) {

    }

    // Get Meal Plan btn click
    public void onGetMealPlanButton(View view) {

    }

    public void onButtonClick(View view) {
    /*
        User user = new User("john", "ASDFRED", 150, 5.4f, 38);
        StepCalorieDetails sc = new StepCalorieDetails(150, 300, 1000, 2500, 1900);

        Log.i("MP", "About to start activity");

        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        intent.putExtra(User.EXTRA_USEROBJ, user);
        intent.putExtra(StepCalorieDetails.EXTRA_STEPCALDETAIL_OBJ, sc);
        startActivity(intent);

     */

        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);

    }

}