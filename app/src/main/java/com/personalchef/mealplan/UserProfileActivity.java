package com.personalchef.mealplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.personalchef.mealplan.models.User;

public class UserProfileActivity extends AppCompatActivity {

    //import user model
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }

    //call method when user clicks on submit button
   public void onClickSubmit(){
       //get ref to TextView
       TextView brands = (TextView) findViewById(R.id.ageInput);
   }

}