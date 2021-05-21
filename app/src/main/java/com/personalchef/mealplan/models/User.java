package com.personalchef.mealplan.models;


import java.text.DecimalFormat;

public class User {
    private String userName;
    private String hash;
    private int weight;
    private int height;
    private int age;

    //set age
    public void setAge(int age)
    {
        this.age = age;
    }

    //set height
    public void setHeight(int height)
    {
        this.height = height;
    }

    //set weight
    public void setWeight(int weight)
    {
        this.weight = weight;
    }


    // Functionality
    protected void SaveUserLocally() {
        return;
    }

    protected User LoadUser() {
        return new User();
    }


}
