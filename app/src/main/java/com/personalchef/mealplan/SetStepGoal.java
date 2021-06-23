package com.personalchef.mealplan;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

public class SetStepGoal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    int goal;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;
    public EditText stepGoalET;
    public User user = Utilities.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_step_goal);

        //Toolbar and Nav Drawer set up 
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.my_drawer_layout);
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close);

        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        navView.setNavigationItemSelectedListener(this);
        toggle.syncState();
        navView.getMenu().getItem(2).setChecked(true);

        stepGoalET = findViewById(R.id.stepGoal);

        if(user != null){
            stepGoalET.setText(String.valueOf(user.getGoal()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt("goal", goal);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void stepGoalSubmitted(View view) {
        int sGoal = 0;
        boolean isValid = false;
        try {
            sGoal = Integer.parseInt(stepGoalET.getText().toString());
            isValid = true;
        } catch (NumberFormatException e) {
            isValid = false;
        }

        //int validation
        if(isValid){
            if(user == null) {
                user = IOHelper.loadUserFromFile(this);
            }
            user.setGoal(sGoal);
            Utilities.setUser(user);
            Utilities.goal = sGoal;
            IOHelper.SaveUserToFile(this, user);
            user = IOHelper.loadUserFromFile(this);
            this.goal = sGoal;

            Intent intent = new Intent(getApplicationContext(), StepCounterActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Invalid value.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch (id)
        {
            case R.id.homeMenu:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.userProfile:
                intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                break;
            case R.id.setStepGoal:
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
        drawer = findViewById(R.id.my_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}