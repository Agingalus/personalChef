package com.personalchef.mealplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.personalchef.mealplan.models.User;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int weight;
    private float height;
    private int age;


    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
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
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("age", age);
        savedInstanceState.putFloat("height", height);
        savedInstanceState.putInt("weight", weight);
    }

    // Validates the inputs & displays appropriate error messages
    private boolean canSaveUser() {
        EditText ageInput = (EditText) findViewById(R.id.ageInput);
        EditText heightInput = (EditText) findViewById(R.id.heightInput);
        EditText weightInput = (EditText) findViewById(R.id.weightInput);

        Integer val = null;
        if ((val = tryParseInt(ageInput.getText().toString())) != null) {
            age = val.intValue();
        } else {
            Toast.makeText(this, "Invalid Age.", Toast.LENGTH_LONG).show();
            ageInput.requestFocus();
            return false;
        }

        if ((val = tryParseInt(heightInput.getText().toString())) != null) {
            height = val.intValue();
        } else {
            Toast.makeText(this, "Invalid Height.", Toast.LENGTH_LONG).show();
            heightInput.requestFocus();
            return false;
        }

        if ((val = tryParseInt(weightInput.getText().toString())) != null) {
            weight = val.intValue();
        } else {
            Toast.makeText(this, "Invalid Weight.", Toast.LENGTH_LONG).show();
            weightInput.requestFocus();
            return false;
        }

        if (age < 1 || height < 1 || weight < 1) {
            Toast.makeText(this, "Invalid Value. Value cannot be less than 1.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //save user input
    public void onSaveUserProfile(View view) {
        if (canSaveUser()) {
            // Create/update user object
            User user = new User(weight, height, age);
            IOHelper.SaveUserToFile(getApplicationContext(), user);

            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            /*Intent intent = new Intent(getApplicationContext(), SetStepGoal.class);
            startActivity(intent);*/

            finish();
        }
    }

    /**
     * Parses a String to Integer
     * @param value - String to be parsed to Integer
     * @return Integer - if can parse String successfully to Integer, else returns null
     */
    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch (id)
        {
            case R.id.userProfile:
                intent = new Intent(getApplicationContext(), UserProfileActivity.class);
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