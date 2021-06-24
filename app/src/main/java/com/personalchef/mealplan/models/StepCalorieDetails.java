package com.personalchef.mealplan.models;

import com.personalchef.mealplan.IOHelper;

import java.io.Serializable;

/**
 * Stores details of Steps and Calories consumed and burned.
 * Specially used to have all details to display on Step counter page
 */
public class StepCalorieDetails implements Serializable {
    public static String EXTRA_STEPCALDETAIL_OBJ = "com.personalchef.mealplan.models.StepCalorieDetails";

    private int totalSteps;
    private int calBurnt;
    private int totalSteps_Week;
    private int totalCal_Intake;
    private int totalCal_Burned;
    private int avgSteps;
    private double milesWalked;
    private int progress;

    public StepCalorieDetails() { }

    public StepCalorieDetails(int tsteps, int cburn, int stepsWeek, int calintake, int calburn, int aSteps) {
        this.totalSteps = tsteps;
        this.calBurnt = cburn;
        this.totalSteps_Week = stepsWeek;
        this.totalCal_Intake = calintake;
        this.totalCal_Burned = calburn;
        this.avgSteps = aSteps;

        Calculate();
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public int getCalBurnt() {
        return calBurnt;
    }

    public int getTotalSteps_Week() {
        return  totalSteps_Week;
    }

    public int getTotalCal_Intake() {
        return totalCal_Intake;
    }

    public int getTotalCal_Burned() {
        return totalCal_Burned;
    }

    public int getAvgSteps() {
        return avgSteps;
    }

    public double getMilesWalked() {
        return this.milesWalked;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setAvgSteps(int avgSteps) {
        this.avgSteps = avgSteps;
    }

    public void setMilesWalked(double milesWalked) {
        this.milesWalked = milesWalked;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setCaloriesBurnt(int calBurnt) {
        this.calBurnt = calBurnt;
    }

    public void setTotalSteps(int steps) {
        this.totalSteps = steps;
    }

    public void Calculate() {
        User user = Utilities.getUser();

        if (user != null) {
            // progress
            double d = (double)this.totalSteps / user.getGoal() * 100;
            this.progress = (int) d;

            // miles walked
            double stepLength = user.getHeight() * 0.42 / 12;
            double stepsPerMile = 5280 / stepLength;
            double miles = this.totalSteps / stepsPerMile;
            this.milesWalked = miles;
        }
    }
}
