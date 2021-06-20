package com.personalchef.mealplan;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;
import com.personalchef.mealplan.services.NotificationService;
import com.personalchef.mealplan.services.StepsCalculatorService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;
    private StepCalorieDetails scDetail;
    private int stepCount;


    @Override
    protected void onStart() {
        super.onStart();

        // Register Notification Channel
        createNotificationChannel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper helper = new DatabaseHelper(this);
        scDetail = helper.getStepDetails();
        scDetail.Calculate();
        helper.close();

        stepCount = scDetail.getTotalSteps();
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

        // Load user from file
        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        Utilities.setUser(u);

        // Start counting steps
        startStepsCalculatorService();

        ///when activity is  created user gets the text for the joke of the day here
        TextView textViewjoke=findViewById(R.id.tv_textJoke);
        textViewjoke.setText(IOHelper.getJokeOfTheDay(this));
        System.out.println((u != null ? u.toString() : ""));
        //makes the progress bar
        populateProgress();
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

        //showSummaryNotification();

        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = getString(R.string.notifChannelId);
            CharSequence name = getString(R.string.notifChannel_name);
            String description = getString(R.string.notifChannel_description);
            NotificationChannel channel = new NotificationChannel(id, name,
                    NotificationManager.IMPORTANCE_HIGH);
            //channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Show other Notification.
    // Not required at present, if at all we need it
    private void showSummaryNotification() {
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra(NotificationService.EXTRA_TITLE, "Congratulation!");
        intent.putExtra(NotificationService.EXTRA_TEXT, "You have completed 25% of your goal.");
        intent.setAction(NotificationService.ACTION_SUMMARY);

        startService(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch (id)
        {
            case R.id.userProfile:
                intent = new Intent(MainActivity.this, UserProfileActivity.class);
                break;
            case R.id.setStepGoal:
                intent = new Intent(getApplicationContext(), SetStepGoal.class);
                break;
            case R.id.stepCounter:
                intent = new Intent(getApplicationContext(), StepCounterActivity.class);
                break;
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

    // Start StepsCalculatorService
    public void startStepsCalculatorService() {
        Intent serviceIntent = new Intent(this, StepsCalculatorService.class);
        //Utilities.NotificationString = "";
        serviceIntent.setAction(StepsCalculatorService.ACTION_START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    // Code to Stop the Service, if we want to
    // In release version, we would not have this event.
    public void stopForegroundService(View view) {
        try {
            Intent serviceIntent = new Intent(this, StepsCalculatorService.class);
            serviceIntent.setAction(StepsCalculatorService.ACTION_STOP_FOREGROUND_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }catch (Exception ex) {}
    }
    public  void populateProgress(){
        User user = Utilities.getUser();
        // Retrieve Views
        TextView stepCountV = findViewById(R.id.number_of_calories);
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        TextView percentV = findViewById(R.id.percent);

        int goal = user.getGoal();
        double percent;

        int progress = scDetail.getProgress();

        pieChart.setProgress(progress);

        stepCountV.setText(stepCount + " / " + goal);
        percent = (double)stepCount/(double)goal;
        percentV.setText(percent + "%");

        return;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
