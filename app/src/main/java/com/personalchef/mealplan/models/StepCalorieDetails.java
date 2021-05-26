package com.personalchef.mealplan.models;

import android.content.Context;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * Stores details of Steps and Calories consumed and burned.
 * Specially used to have all details to display on Step counter page
 */
public class StepCalorieDetails implements Serializable {
    public static String EXTRA_STEPCALDETAIL_OBJ = "com.personalchef.mealplan.models.StepCalorieDetails";

    private final String filenameInternal = "StepCalorieDetail";
    private int totalSteps;
    private int calBurnt;
    private int totalSteps_Week;
    private int totalCal_Intake;
    private int totalCal_Burned;

    public StepCalorieDetails() { }

    public StepCalorieDetails(int tsteps, int cburn, int stepsWeek, int calintake, int calburn) {
        this.totalSteps = tsteps;
        this.calBurnt = cburn;
        this.totalSteps_Week = stepsWeek;
        this.totalCal_Intake = calintake;
        this.totalCal_Burned = calburn;
    }
    public void SaveCalorieDetailsToInternalStorage(){
        StepCounter stepCounter = new StepCounter();
        StepCalorieDetails infoToWrite = new StepCalorieDetails(stepCounter.GetStepCount(),stepCounter.CalculateCaloriesBurnt(),
                0,1,2);
    }
    private void createUpdateFile(String fileName, String content) {
        FileOutputStream outputStream;

        try {

            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
