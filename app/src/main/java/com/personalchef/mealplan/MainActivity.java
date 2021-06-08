package com.personalchef.mealplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.User;

public class MainActivity extends NavigationDrawer {
    private DatabaseHelper helper = null;
    private StepCounterActivity sc = new StepCounterActivity();

    public MainActivity() {
        // With final init, error of SQL not closing was generated. Init in constructor
        helper = new DatabaseHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load user from file
        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;

        ///when activity is  created user gets the text for the joke of the day here
        TextView textViewjoke=findViewById(R.id.tv_textJoke);
        textViewjoke.setText(IOHelper.getJokeOfTheDay(this));
        System.out.println((u != null ? u.toString() : ""));

    }



    public void onButtonClick(View view) {

        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the SQL
        helper.close();
    }



    public void stepCounterDisplay(View view) {
        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        startActivity(intent);
    }

    public void stepGoalSubmitted(View view) {
        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        startActivity(intent);
    }
}