package com.personalchef.mealplan.models;

import java.io.Serializable;

public class User implements Serializable {
    public static String EXTRA_USEROBJ = "com.personalchef.mealplan.models.User";

    private String userName;
    private String hash;
    private int weight;
    private float height;
    private int age;

    private User() {}

    public User(String uname, String hash, int weight, float height, int age) {
        this.userName = uname;
        this.hash = hash;
        this.weight = weight;
        this.height = height;
        this.age = age;
    }

    public float getHeight() {
        return height;
    }
    public void setHeight(float _height) {
        this.height = _height;
    }

    public int getWeight() {
        return this.weight;
    }
    public void setWeight(int _weight) {
        this.weight = _weight;
    }

    // Functionality
    protected void SaveUserLocally() {
        return;
    }

    protected User LoadUser() {
        return new User();
    }

}
