package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

public class StepCounterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        Log.i("MP", "Into StepCount activity");

        // GET PARAMETERS PASSEd
        Intent intent = getIntent();
        User user = Utilities.getUser(); // (User) intent.getSerializableExtra(User.EXTRA_USEROBJ);
        StepCalorieDetails scDetail = (StepCalorieDetails) intent.getSerializableExtra(StepCalorieDetails.EXTRA_STEPCALDETAIL_OBJ);

        populateTextViews(user, scDetail);
    }

    private void populateTextViews(User user, StepCalorieDetails scDetail) {
        // Retrieve Views
        // User
        TextView tvHeight = findViewById(R.id.tvHeight);
        TextView tvWeight = findViewById(R.id.tvWeight);

        // Step-Calorie
        TextView tvTodaysSteps = findViewById(R.id.tvTodaysSteps);
        TextView tvBurnedCals = findViewById(R.id.tvBurnedCals);
        TextView tv_TotalStepsThisWeek = findViewById(R.id.tv_TotalStepsThisWeek);
        TextView tv_TotalCalsIntake = findViewById(R.id.tv_TotalCalsIntake);
        TextView tv_TotalCalsBurned = findViewById(R.id.tv_TotalCalsBurned);

        // Set Values to text views
        tvHeight.setText(Float.toString(user.getHeight()));
        tvWeight.setText(String.valueOf(user.getWeight()));

        tvTodaysSteps.setText(String.valueOf(scDetail.getTotalSteps()));
        tvBurnedCals.setText(String.valueOf(scDetail.getCalBurnt()));
        tv_TotalStepsThisWeek.setText(String.valueOf(scDetail.getTotalSteps_Week()));
        tv_TotalCalsIntake.setText(String.valueOf(scDetail.getTotalCal_Intake()));
        tv_TotalCalsBurned.setText(String.valueOf(scDetail.getTotalCal_Burned()));

        return;
    }

    public void onHomeClicked(View view) {
        onBackPressed();
    }
}

