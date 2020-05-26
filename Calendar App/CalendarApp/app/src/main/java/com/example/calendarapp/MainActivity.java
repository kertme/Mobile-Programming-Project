package com.example.calendarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button createEventButton,listEventButton,settingsButton;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createEventButton = (Button) findViewById(R.id.buttonCreateEvent);
        listEventButton = (Button) findViewById(R.id.buttonListEvent);
        settingsButton = (Button) findViewById(R.id.buttonSettings);

        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedpreferences.edit();
        Boolean darkmode = sharedpreferences.getBoolean("darkmodeKey",false);
        if (darkmode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //editor.putBoolean("darkmodeKey",false);
        //editor.apply();



        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

        listEventButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openActivity3();
        }
    });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity5();
            }
        });

    }

    public void openActivity2(){
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    public void openActivity3(){
        Intent intent = new Intent(this, ListEventActivity.class);
        startActivity(intent);
    }

    public void openActivity5(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
