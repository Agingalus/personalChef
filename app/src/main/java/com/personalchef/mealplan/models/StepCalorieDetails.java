package com.personalchef.mealplan.models;

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

    public StepCalorieDetails() { }

    public StepCalorieDetails(int tsteps, int cburn, int stepsWeek, int calintake, int calburn, int aSteps) {
        this.totalSteps = tsteps;
        this.calBurnt = cburn;
        this.totalSteps_Week = stepsWeek;
        this.totalCal_Intake = calintake;
        this.totalCal_Burned = calburn;
        this.avgSteps = aSteps;
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
}
