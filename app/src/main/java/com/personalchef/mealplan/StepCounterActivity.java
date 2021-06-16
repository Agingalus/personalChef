package com.personalchef.mealplan;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterActivity extends AppCompatActivity {
    private int stepCount;
    private StepCalorieDetails scDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        DatabaseHelper helper = new DatabaseHelper(this);
        scDetail = helper.getStepDetails();
        scDetail.Calculate();
        helper.close();

        stepCount = scDetail.getTotalSteps();

        populateTextViews();
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTextViews() {
        int avg = 0;

        // Retrieve Views
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTodaysSteps = findViewById(R.id.tvTodaysSteps);
        TextView tvBurnedCals = findViewById(R.id.tvBurnedCals);
        TextView tv_TotalStepsThisWeek = findViewById(R.id.tv_TotalStepsThisWeek);
        TextView tv_TotalCalsIntake = findViewById(R.id.tv_TotalCalsIntake);
        TextView tv_TotalCalsBurned = findViewById(R.id.tv_TotalCalsBurned);
        TextView stepCountV = findViewById(R.id.number_of_calories);
        TextView tv_aSteps = findViewById(R.id.avgSteps);
        TextView tvMiles = findViewById(R.id.tvMiles);
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);

        // Set Values to text views
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // System.out.println(scDetail.getTotalSteps());

        tvTodaysSteps.setText(String.valueOf(scDetail.getTotalSteps()));
        tvBurnedCals.setText(String.valueOf(scDetail.getCalBurnt()));
        tv_TotalStepsThisWeek.setText(String.valueOf(scDetail.getTotalSteps_Week() + stepCount));
        tv_TotalCalsIntake.setText(String.valueOf(scDetail.getTotalCal_Intake()));
        tv_TotalCalsBurned.setText(String.valueOf(scDetail.getTotalCal_Burned() + scDetail.getCalBurnt()));
        if (scDetail.getAvgSteps() != 0){
            avg = (scDetail.getTotalSteps_Week() + scDetail.getTotalSteps()) / scDetail.getAvgSteps();
        }
        tv_aSteps.setText("Avg Steps: "+ avg);

        int progress = scDetail.getProgress();
        pieChart.setProgress(progress);

        tvMiles.setText("Distance Walked (per Mile): " + String.format("%.2f", scDetail.getMilesWalked()));

        stepCountV.setText(stepCount + " / " + Utilities.goal);

        return;
    }

    public void onHomeClicked(View view) {
        onBackPressed();
    }

    protected void onDestroy() {
        System.out.println("app is destroyed and saved data");
        scDetail = null;
        super.onDestroy();
    }
}

