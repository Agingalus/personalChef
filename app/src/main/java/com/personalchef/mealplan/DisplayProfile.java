package com.personalchef.mealplan;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

public class DisplayProfile extends AppCompatActivity {

    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);


        //Toolbar and Nav Drawer set up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close);

        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        navView.setNavigationItemSelectedListener(this);
        toggle.syncState();
    }
}