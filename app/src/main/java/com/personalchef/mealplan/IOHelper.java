package com.personalchef.mealplan;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.personalchef.mealplan.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOHelper {
    private static String userFileName = "pc-user.json";
    private static String stepsFileName = "pc-steps.json";

    // Reads string from stream
    private static String stringFromStream(InputStream is) throws IOException {
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }

    // Load User from file
    public static User loadUserFromFile(Context context) {
        String json;
        User user = null;

        try {
            InputStream is = context.openFileInput(userFileName) ;
            json = stringFromStream(is);
            JSONObject obj = new JSONObject(json);

            if (obj.length() > 0) {
                user = new Gson().fromJson(obj.toString(), User.class);
            }
        } catch (IOException | JSONException e) {
            Log.e("PC", "ERror while loading user - " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    // Save User
    public static void SaveUserToFile(Context context, User user) {
        try {
            String jsonStr = new Gson().toJson(user);

            // Save to File
            FileOutputStream fos = context.openFileOutput(userFileName, Context.MODE_PRIVATE);
            fos.write(jsonStr.getBytes(), 0, jsonStr.length());
            fos.close();
            //Log.i("PC", "Saved to file ");
        } catch (IOException e) {
            Log.e("PC", "ERror while saving - " + e.getMessage());
            e.printStackTrace();
        }

        return;
    }

    /** StepCalorieDetails JSON read/write
    // Load StepCalorieDetails from file
    public static StepCalorieDetails loadStepDetails(Context context) {
        String json;
        StepCalorieDetails stepCalorieDetails = null;

        try {
            InputStream is = context.openFileInput(stepsFileName) ;
            json = stringFromStream(is);
            JSONObject obj = new JSONObject(json);

            if (obj.length() > 0) {
                stepCalorieDetails = new Gson().fromJson(obj.toString(), StepCalorieDetails.class);
            }
        } catch (IOException | JSONException e) {
            Log.e("PC", "ERror while loading user - " + e.getMessage());
            e.printStackTrace();
        }

        return stepCalorieDetails;
    }

    // Save StepCalorieDetails to file
    public static void SaveStepCalorieToFile(Context context, StepCalorieDetails stepCalorieDetails) {
        try {
            String jsonStr = new Gson().toJson(stepCalorieDetails);

            // Save to File
            FileOutputStream fos = context.openFileOutput(stepsFileName, Context.MODE_PRIVATE);
            fos.write(jsonStr.getBytes(), 0, jsonStr.length());
            fos.close();
            //Log.i("PC", "Saved to file ");
        } catch (IOException e) {
            Log.e("PC", "ERror while saving - " + e.getMessage());
            e.printStackTrace();
        }

        return;
    }
*/

}
