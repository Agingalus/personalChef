package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.personalchef.mealplan.models.User;

public class UserProfileActivity extends AppCompatActivity {

    private int weight;
    private float height;
    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if (savedInstanceState != null){
            age = savedInstanceState.getInt("age");
            height = savedInstanceState.getFloat("height");
            weight = savedInstanceState.getInt("weight");
        }
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

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

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

}