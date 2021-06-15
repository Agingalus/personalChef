package com.personalchef.mealplan;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.personalchef.mealplan.models.DatabaseHelper;
import com.personalchef.mealplan.models.StepCalorieDetails;
import com.personalchef.mealplan.models.StepCounter;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;
import com.personalchef.mealplan.services.NotificationService;
import com.personalchef.mealplan.services.StepsCalculatorService;

public class MainActivity extends AppCompatActivity {
    private StepCounterActivity sc = new StepCounterActivity();
    public StepCalorieDetails scDetail;
    public StepCounter stepCounter;
    private DatabaseHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register Notification Channel
        createNotificationChannel();

        // Load user from file
        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        Utilities.setUser(u);

        // Start counting steps
        startStepsCalculatorService();

        ///when activity is  created user gets the text for the joke of the day here
        TextView textViewjoke=findViewById(R.id.tv_textJoke);
        textViewjoke.setText(IOHelper.getJokeOfTheDay(this));
        System.out.println((u != null ? u.toString() : ""));
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

        showSummaryNotification();

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

    private void showSummaryNotification() {
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra(NotificationService.EXTRA_TITLE, "Congratulation!");
        intent.putExtra(NotificationService.EXTRA_TEXT, "You have completed 25% of your goal.");
        intent.setAction(NotificationService.ACTION_SUMMARY);

        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void stepCounterDisplay(View view) {
        Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
        startActivity(intent);
    }

    // Start StepsCalculatorService
    public void startStepsCalculatorService() {
        Intent serviceIntent = new Intent(this, StepsCalculatorService.class);
        Utilities.NotificationString = "Text for Calc Service.";
        //serviceIntent.putExtra(Constants.EXTRA_TEXT, "Text for Calc Service.");
        serviceIntent.setAction(StepsCalculatorService.ACTION_START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    public void stopForegroundService(View view) {
        Intent serviceIntent = new Intent(this, StepsCalculatorService.class);
        serviceIntent.setAction(StepsCalculatorService.ACTION_STOP_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}