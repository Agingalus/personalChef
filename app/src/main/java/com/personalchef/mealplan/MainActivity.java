package com.personalchef.mealplan;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseHelper helper = null;
    private StepCounterActivity sc = new StepCounterActivity();
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;
    public StepCalorieDetails scDetail;
    public StepCounter stepCounter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register Notification Channel
        createNotificationChannel();

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


        ///when activity is  created user gets the text for the joke of the day here
        TextView textViewjoke=findViewById(R.id.tv_textJoke);
        textViewjoke.setText(IOHelper.getJokeOfTheDay(this));
        System.out.println((u != null ? u.toString() : ""));
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
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showSummaryNotification() {
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra(NotificationService.EXTRA_TITLE, "Congratulation!");
        intent.putExtra(NotificationService.EXTRA_TEXT, "You have completed 25% of your goal.");
        intent.setAction(NotificationService.ACTION_SUMMARY);

        startService(intent);
    }

//    @Override
//    protected void onDestroy() {
//        System.out.println("im a stepCounter" + stepCounter.GetStepCount());
//        helper.insert(stepCounter.GetStepCount(),stepCounter.CalculateCaloriesBurnt(),scDetail.getTotalSteps_Week(),scDetail.getTotalCal_Intake(),scDetail.getTotalCal_Burned());
//        helper.close();
//        super.onDestroy();
//    }



    /*public void stepCounterDisplay(View view) {
        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        startActivity(intent);
    }*/

    /*public void stepGoalSubmitted(View view) {
        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        startActivity(intent);
    }*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
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
        }
        startActivity(intent);


        //Close drawer when user selects option
        drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}