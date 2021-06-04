package com.personalchef.mealplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

public class MainActivity extends AppCompatActivity {
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
        // If at all user is null, we don't want to crash the app right now
        textViewjoke.setText(Utilities.GetJoke() + "\n" + (u != null ? u.toString() : ""));
    }

    // View Meal btn click
    public void onViewMealButton(View view) {

    }

    // Get Meal Plan btn click
    public void onGetMealPlanButton(View view) {

    }

    public void onButtonClick(View view) {
    /*
        User user = new User("john", "ASDFRED", 150, 5.4f, 38);
        StepCalorieDetails sc = new StepCalorieDetails(150, 300, 1000, 2500, 1900);

        Log.i("MP", "About to start activity");

        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        intent.putExtra(User.EXTRA_USEROBJ, user);
        intent.putExtra(StepCalorieDetails.EXTRA_STEPCALDETAIL_OBJ, sc);
        startActivity(intent);

     */

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
}