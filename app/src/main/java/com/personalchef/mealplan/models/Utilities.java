package com.personalchef.mealplan.models;

import android.app.Application;

// 
public class Utilities extends Application {

    // Keep a single copy of User on start of the program.
    // So don't have to read User obj often from the disk.
    private static User user = null;

    // A string to update the notification text
    public static String NotificationString = "";

    //public static int goal = 102;

    // Var to pass text within activities (in the intent.putExtra()
    public static final String EXTRA_TEXT = "Text";


    public static User getUser() {
        return user;
    }

    public static void setUser(User u) {
        user = u;
    }

}
