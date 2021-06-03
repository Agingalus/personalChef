package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.personalchef.mealplan.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import androidx.appcompat.app.ActionBar;


public class UserProfileActivity extends AppCompatActivity {


    private int weight;
    private float height;
    private int age;

    EditText ageInput, heightInput, weightInput;
    Button submitButton, resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //get ref to toolbar and set it as the activity app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Enable Home Up feature
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null){
            age = savedInstanceState.getInt("age");
            height = savedInstanceState.getFloat("height");
            weight = savedInstanceState.getInt("weight");

        }

        ageInput = (EditText) findViewById(R.id.ageInput);
        heightInput = (EditText) findViewById(R.id.heightInput);
        weightInput = (EditText) findViewById(R.id.weightInput);
        resetButton = (Button) findViewById(R.id.resetButton);
        submitButton = (Button) findViewById(R.id.submitButton);

        //Clear user input
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ageInput.setText("");
                heightInput.setText("");
                weightInput.setText("");

                //set setEnabled false
                ageInput.setEnabled(true);
                heightInput.setEnabled(true);
                weightInput.setEnabled(true);
            }
        });

        //save user input
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Store input values
                age = Integer.valueOf(ageInput.getText().toString());
                height = Float.valueOf(heightInput.getText().toString());
                weight = Integer.valueOf(weightInput.getText().toString());

                ageInput.setText(String.valueOf(age));
                heightInput.setText(String.valueOf(height));
                weightInput.setText(String.valueOf(weight));

                //set setEnabled false
                ageInput.setEnabled(false);
                heightInput.setEnabled(false);
                weightInput.setEnabled(false);

                // Create/update user object
                // save it to the Utilities
                User user = new User("chefUser", "HASH", weight, height, age);
                IOHelper.SaveUserToFile(getApplicationContext(), user);

                Intent intent = new Intent(getApplicationContext(), UserProfileSubmitted.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("age", age);
        savedInstanceState.putFloat("height", height);
        savedInstanceState.putInt("weight", weight);
    }

    //nav button for testing
    public void onHomeButtonClick(View view) {



    }

    //private String uname;
    //private String hash;
    //import user model
    //private User user = new User(uname, hash, weight, height, age);


}