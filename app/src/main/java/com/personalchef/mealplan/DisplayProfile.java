package com.personalchef.mealplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.personalchef.mealplan.models.User;
import com.personalchef.mealplan.models.Utilities;

public class DisplayProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;
    public User user = Utilities.getUser();

    private int weight;
    private float height;
    private int age;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);
        if (savedInstanceState != null){
            age = savedInstanceState.getInt("age");
            height = savedInstanceState.getFloat("height");
            weight = savedInstanceState.getInt("weight");
        }

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

        populateTextViews();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("age", age);
        savedInstanceState.putFloat("height", height);
        savedInstanceState.putInt("weight", weight);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void populateTextViews(){
        /*Bundle extras = getIntent().getExtras();
        age = extras.getInt("age");
        height = extras.getFloat("height");
        weight = extras.getInt("weight");*/

        TextView ageDisplay = findViewById(R.id.ageDisplay);
        TextView heightDisplay = findViewById(R.id.heightDisplay);
        TextView weightDisplay = findViewById(R.id.weightDisplay);


        //Set values to TextViews
        ageDisplay.setText(String.valueOf(user.getAge()));
        heightDisplay.setText(String.valueOf(user.getHeight()));
        weightDisplay.setText(String.valueOf(user.getWeight()));
        return;
    }

    public void onEditUserProfile(View view){
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
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
           /* case R.id.userProfile:
                intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                break;*/
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
}