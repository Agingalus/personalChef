package com.personalchef.mealplan.models;

public class Utilities {

    private static User user;
    private static StepCalorieDetails stepCalorieDetails;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Utilities.user = user;
    }

    public static void ReadUserFromFS() {
        //
    }

    public  static void SaveUserToFS() {
        // Save obj to file sys
    }

    public static String GetJoke() {
        return "Joke of the Day :-).";
    }


}
