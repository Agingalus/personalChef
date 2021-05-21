package com.personalchef.mealplan.models;

public class User {
    private String userName;
    private String hash;
    private int weight;
    private int height;
    private int age;


    // Functionality
    protected void SaveUserLocally() {
        return;
    }

    protected User LoadUser() {
        return new User();
    }

}
