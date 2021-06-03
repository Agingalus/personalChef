package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.personalchef.mealplan.models.User;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Load user from file
        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        Log.i("PC", "User = " + u.toString());
        if (u == null) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // remove this activity from stack
        finish();
    }
}