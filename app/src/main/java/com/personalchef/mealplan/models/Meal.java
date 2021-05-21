package com.personalchef.mealplan.models;

/***
 * Details of a meal
 * DayPlan & WeekPlan objects will have meals for day/plan
 */
public class Meal {
    private  int mealId;
    private String title;
    private String category;
    private String imageType;
    private int readyInMinutes;
    private int serving;
    private String sourceUrl;

    // We are not focusing on API based, hence added all meal details here in 1 place
    private double calories;
    private double carbohydrates;
    private double fat;
    private double protein;
}
