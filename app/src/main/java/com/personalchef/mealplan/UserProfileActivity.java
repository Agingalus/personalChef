package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.personalchef.mealplan.models.User;

public class UserProfileActivity extends AppCompatActivity {

    //import user model
    private User user = new User();

    //Global variables
    int age, weight;
    float height;
    EditText ageInput, heightInput, weightInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ageInput = (EditText) findViewById(R.id.ageInput);
        heightInput = (EditText) findViewById(R.id.heightInput);
        weightInput = (EditText) findViewById(R.id.weightInput);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                age = Integer.valueOf(ageInput.getText().toString());
                height = Float.valueOf(heightInput.getText().toString());
                weight = Integer.valueOf(weightInput.getText().toString());

                showToast(String.valueOf(age));
                showToast(String.valueOf(height));
                showToast(String.valueOf(weight));
            }
        });
    }

    private void showToast(String text){
        Toast.makeText(UserProfileActivity.this, text, Toast.LENGTH_SHORT).show();
    }


}