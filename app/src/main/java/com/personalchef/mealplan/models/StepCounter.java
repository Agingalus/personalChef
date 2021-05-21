package com.personalchef.mealplan.models;

import java.util.Date;

/***
 * StepCounter
 *
 * SensorAdapter updates the StepCount
 */
public class StepCounter {
    private int stepCounter;
    private Date date;

    // Functionality
    public int GetStepCount() {
        int steps = 0;
        // Get step count from sensor, &/or Db
        // Return sum of all
        return steps;
    }

    public StepCalorieDetails CalculateCaloriesBurnt() {
        StepCalorieDetails scDetail = new StepCalorieDetails();
        // Get step count
        // Get calories from all day/week meals
        // Calculates calorie details

        return scDetail;
    }
}
