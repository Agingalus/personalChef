package com.personalchef.mealplan.models;

import android.app.Application;

// 
public class Utilities extends Application {

    private static User user = null;
    private static StepCalorieDetails stepCalorieDetails = null;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        user = user;
    }

    public static void SetStepCalorieDetails(StepCalorieDetails sc) {
        stepCalorieDetails = sc;
    }

    public static StepCalorieDetails getStepCalorieDetails() {
        return stepCalorieDetails;
    }


    public static String GetJoke() {
        return "Joke of the Day :-).";
    }


}
