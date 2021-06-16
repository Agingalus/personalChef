package com.personalchef.mealplan.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.personalchef.mealplan.IOHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "personalStuff";
    public static final String STEPS_TABLE_NAME = "StepDetails";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "create table " + STEPS_TABLE_NAME + "(" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "weekDay INTEGER NOT NULL, " +
                            "totalSteps INTEGER NOT NULL, " +
                            "calBurnt INTEGER NOT NULL, " +
                            "totalCal_Intake INTEGER NOT NULL)"
                    //"create table " + STEPS_TABLE_NAME + "(id INTEGER PRIMARY KEY, name text,salary
            );
        } catch (SQLiteException e) {
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STEPS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(int wd, int ts, int cb, int tci) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("weekDay", wd);
            contentValues.put("totalSteps", ts);
            contentValues.put("calBurnt", cb);
            contentValues.put("totalCal_Intake", tci);
            db.replace(STEPS_TABLE_NAME, null, contentValues);
            //System.out.println("INSERTED To DB Successfully");
        } catch (SQLiteException e){
            System.out.println("insert failed :" + e.toString());
        }

        return true;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public StepCalorieDetails getStepDetails() {
        //int currentDay = LocalDate.now().getDayOfWeek().getValue();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date date = calendar.getTime();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        int totSteps;
        int totCalBurned;
        int avgSteps;

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> array_list = new ArrayList<Integer>();
        Cursor res = db.rawQuery("select * from " + STEPS_TABLE_NAME + " WHERE weekDay =" + currentDay, null); //(totalSteps || calBurnt || totalSteps_Week || totalCal_Intake || totalCal_Burned)
        res.moveToFirst();
        while (res.isAfterLast() == false) { //use while loop if for more than 1 week stored
            if ((res != null) && (res.getCount() > 0))
                //array_list.add(res.getInt(res.getColumnIndex("weekDay")));
                array_list.add(res.getInt(res.getColumnIndex("totalSteps")));
                array_list.add(res.getInt(res.getColumnIndex("calBurnt")));
                array_list.add(res.getInt(res.getColumnIndex("totalCal_Intake")));
            res.moveToNext();
        }

        // query to return total steps and total calBurnt
        res = db.rawQuery("select SUM(totalSteps), SUM(calBurnt)  from " + STEPS_TABLE_NAME, null);
        res.moveToFirst();
        totSteps = res.getInt(0);
        totCalBurned = res.getInt(1);

        //query to return total days where steps were taken
        res = db.rawQuery("select COUNT(totalSteps)  from " + STEPS_TABLE_NAME +" WHERE totalSteps != 0 ", null);
        res.moveToFirst();
        avgSteps = res.getInt(0);



        //System.out.println(array_list.get(1) + " attempt to get array list to work for us");
        StepCalorieDetails stepCalorieDetails = null;
        if (array_list.isEmpty()){
            addToDb(1,20,0,0);
            addToDb(2,0,0,0);
            addToDb(3,0,0,0);
            addToDb(4,0,0,0);
            addToDb(5,0,0,0);
            addToDb(6,0,0,0);
            addToDb(7,10,0,0);
            avgSteps++;
            stepCalorieDetails = new StepCalorieDetails(-1,0,0,0,0, avgSteps);
        } else {
            totSteps = totSteps - array_list.get(0);
            totCalBurned = totCalBurned  - array_list.get(1);
            stepCalorieDetails = new StepCalorieDetails(array_list.get(0), array_list.get(1), totSteps, array_list.get(2),totCalBurned, avgSteps);
        }

        return stepCalorieDetails;
    }
    public void addToDb (int wd, int ts, int cb, int tci){
        int day = check(wd);
        if(day != -1){
            update(wd, ts, cb, tci);
        }
        else{
            insert(wd, ts, cb, tci);
        }
    }

    public boolean update(int wd, int ts, int cb, int tci) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("UPDATE " + STEPS_TABLE_NAME + " " +
                    "SET weekDay = "+ wd +",  " +
                    "totalSteps = "+ ts +"," +
                    "calBurnt = "+ cb +"," +
                    "totalCal_Intake = "+ tci + " " +
                    "WHERE weekDay ="+ wd);
            //System.out.println("UPDATED To DB Successfully");
        } catch (SQLiteException e){
            System.out.println("update failed :" + e.toString());
        }
        return true;
    }
    public int check(int day){
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id, weekDay from " + STEPS_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) { //use while loop if for more than 1 week stored
            if ((res != null) && (res.getCount() > 0))
                if(res.getInt(res.getColumnIndex("weekDay")) == day){
                    return res.getInt(res.getColumnIndex("id"));
                }
                res.moveToNext();
        }
        return id;
    }
    public boolean delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from " + STEPS_TABLE_NAME);
        return true;
    }
}

