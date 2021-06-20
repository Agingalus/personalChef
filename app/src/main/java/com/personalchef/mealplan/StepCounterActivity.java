package com.personalchef.mealplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int stepCount;
    private StepCalorieDetails scDetail;

    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        DatabaseHelper helper = new DatabaseHelper(this);
        scDetail = helper.getStepDetails();
        scDetail.Calculate();
        helper.close();

        //Toolbar and Nav Drawer set up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close);

        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        navView.setNavigationItemSelectedListener(this);
        toggle.syncState();


        stepCount = scDetail.getTotalSteps();

        populateTextViews();
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTextViews() {
        int avg = 0;
        User user = Utilities.getUser();

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
        TextView percentV = findViewById(R.id.percent);

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


        int goal = user.getGoal();
        double percent;

        stepCountV.setText(stepCount + " / " + goal);
        percent = (double)stepCount/(double)goal;
        percentV.setText(percent + "%");

        return;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch (id)
        {
            case R.id.homeMenu:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.userProfile:
                intent = new Intent(getApplicationContext(), DisplayProfile.class);
                break;
            case R.id.setStepGoal:
                intent = new Intent(getApplicationContext(), SetStepGoal.class);
                break;
            /*case R.id.stepCounter:
                //intent = new Intent(getApplicationContext(), StepCounterActivity.class);
                break;*/
            case R.id.aboutUs:
                intent = new Intent(getApplicationContext(), AboutUsActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }

        //Close drawer when user selects option
        drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
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