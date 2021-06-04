package com.personalchef.mealplan.models;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.personalchef.mealplan.MainActivity;
import com.personalchef.mealplan.StepCounterActivity;

import java.util.Date;


/***
 * StepCounter
 *
 * SensorAdapter updates the StepCount
 */
public class StepCounter extends AppCompatActivity {
    private Date date;
    private int weight = 180; //lbs
    private int height = 72; //inches
    private double stepLength = height*.42/12; //used to get user's stepLength
    private double caloriePerMile = weight*.57; //based on causal walking speed
    private double stepsPerMile = 5280/stepLength;
    private Integer caloriesBurned = 0;



    // Functionality
    public int GetStepCount() {
        MainActivity mainActivity = new MainActivity();
        System.out.println("From GetStepCounter\n_____________________________");
        System.out.println(StepCounterActivity.stepCount);
        return StepCounterActivity.stepCount;
    }

    public int  CalculateCaloriesBurnt() {
        int stepCount = GetStepCount();
        StepCalorieDetails scDetail = new StepCalorieDetails();
        // Get step count
        // Get calories from all day/week meals
        // Calculates calorie details
        caloriesBurned = (int) Math.floor((caloriePerMile / stepsPerMile) * stepCount * 100); // 91.2 / 2138 = cal per step then multiply by stepCount. 100 only used for testing purposes

        return caloriesBurned;
    }
    public String  CalculateCaloriesBurntString() {
        int stepCount = GetStepCount();
        StepCalorieDetails scDetail = new StepCalorieDetails();
        // Get step count
        // Get calories from all day/week meals
        // Calculates calorie details
        caloriesBurned = (int) Math.floor((caloriePerMile / stepsPerMile) * stepCount * 100); // 91.2 / 2138 = cal per step then multiply by stepCount. 100 only used for testing purposes

        return caloriesBurned.toString();
    }
    protected void onPause(int stepCount) {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    protected void onStop(int stepCount) {
        super.onStop();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    protected void onResume(int stepCount) {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }
}
