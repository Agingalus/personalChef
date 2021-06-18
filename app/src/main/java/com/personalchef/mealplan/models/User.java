package com.personalchef.mealplan.models;

import java.io.Serializable;

public class User implements Serializable {
    public static String EXTRA_USEROBJ = "com.personalchef.mealplan.models.User";


    private int weight;
    private float height;
    private int age;


    public User(int weight, float height, int age) {
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
        return weight;
    }
    public void setWeight(int _weight) {
        this.weight = _weight;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }




    public String toString() {
        return /*"UserName: " + this.userName + " Hash: " + this.hash +*/
                " Weight: " + this.weight + " Height: " + this.height +
                " Age: " + this.age;
    }

}