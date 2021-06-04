package com.personalchef.mealplan.models;

public class Joke {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return "{ Joke:" +  text  + " }";
    }
}
