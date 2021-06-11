package com.personalchef.mealplan.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "personalStuff";
    public static final String STEPS_TABLE_NAME = "StepDetails";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "create table " + STEPS_TABLE_NAME + "(" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "totalSteps INTEGER NOT NULL, " +
                            "calBurnt INTEGER NOT NULL, " +
                            "totalSteps_Week INTEGER NOT NULL, " +
                            "totalCal_Intake INTEGER NOT NULL," +
                            " totalCal_Burned INTEGER NOT NULL)"
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

    public boolean insert(int ts, int cb, int tsw, int tci, int tcb) {
        try {
            System.out.println("I am inserting???");
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            delete();
            contentValues.put("totalSteps", ts);
            contentValues.put("calBurnt", cb);
            contentValues.put("totalSteps_Week", tsw);
            contentValues.put("totalCal_Intake", tci);
            contentValues.put("totalCal_Burned", tcb);
            db.replace(STEPS_TABLE_NAME, null, contentValues);

        } catch (SQLiteException e){
            System.out.println("insert failed :" + e.toString());
        }

        return true;
    }

    public StepCalorieDetails getStepDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> array_list = new ArrayList<Integer>();
        Cursor res = db.rawQuery("select * from " + STEPS_TABLE_NAME, null); //(totalSteps || calBurnt || totalSteps_Week || totalCal_Intake || totalCal_Burned)
        res.moveToFirst();
        while (res.isAfterLast() == false) { //use while loop if for more than 1 week stored
            if ((res != null) && (res.getCount() > 0))
                array_list.add(res.getInt(res.getColumnIndex("totalSteps")));
                array_list.add(res.getInt(res.getColumnIndex("calBurnt")));
                array_list.add(res.getInt(res.getColumnIndex("totalSteps_Week")));
                array_list.add(res.getInt(res.getColumnIndex("totalCal_Intake")));
                array_list.add(res.getInt(res.getColumnIndex("totalCal_Burned")));
            res.moveToNext();
        }

        //System.out.println(array_list.get(1) + " attempt to get array list to work for us");
        StepCalorieDetails stepCalorieDetails = null;
        if (array_list.isEmpty()){
            insert(0,0,0,0,0);
            stepCalorieDetails = new StepCalorieDetails(0,0,0,0,0);
        } else {
            stepCalorieDetails = new StepCalorieDetails(array_list.get(0), array_list.get(1), array_list.get(2), array_list.get(3), array_list.get(4));
        }

        return stepCalorieDetails;
    }

//    public boolean update(String s, String s1) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("UPDATE " + STEPS_TABLE_NAME + " SET name = " + "'" + s + "', " + "salary = " + "'" + s1 + "'");
//        return true;
//    }
    public boolean delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from " + STEPS_TABLE_NAME);
        return true;
    }
}

