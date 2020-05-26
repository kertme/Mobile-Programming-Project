package com.example.calendarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class SettingsActivity extends AppCompatActivity implements OnItemSelectedListener {

    public static final String MyPREFERENCES = "MyPrefs" ;
    private static final int RB1_ID = 1000;//first radio button id
    private static final int RB2_ID = 1001;//second radio button id
    private static final int RB3_ID = 1002;//third radio button id
    private static final int RB4_ID = 1003;
    private static final int RB5_ID = 1004;
    private static final int RB6_ID = 1005;
    SharedPreferences sharedpreferences;
    EditText remindTime;
    CheckBox checkBox;
    RadioButton rb1,rb2,rb3,rb4,rb5,rb6;
    RadioGroup radioGroup;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        saveButton = findViewById(R.id.buttonSave);
        remindTime=findViewById(R.id.remindTime);
        checkBox=findViewById(R.id.checkBox);
        radioGroup = findViewById(R.id.radioGr);
        rb1 = findViewById(R.id.radioDaily);
        rb2 = findViewById(R.id.radioWeekly);
        rb3 = findViewById(R.id.radioMonthly);
        rb4 = findViewById(R.id.radioYearly);
        rb5 = findViewById(R.id.radioNoReminder);
        rb6 = findViewById(R.id.radioNow);

        rb1.setId(RB1_ID);
        rb2.setId(RB2_ID);
        rb3.setId(RB3_ID);
        rb4.setId(RB4_ID);
        rb5.setId(RB5_ID);
        rb6.setId(RB6_ID);

        remindTime.setFocusable(false);
        remindTime.setKeyListener(null);

        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        spinner.setOnItemSelectedListener(this);
        List<String> options = new ArrayList<String>();
        options.add("Default");
        options.add("Got-It-Done.mp3");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        String sound = sharedpreferences.getString("soundKey","Default");
        String remindStr = sharedpreferences.getString("timeKey","Now");
        String frequency = sharedpreferences.getString("frequencyKey","Once");
        Boolean darkMode = sharedpreferences.getBoolean("darkmodeKey",false);

        if(sound.equals("Default"))
            spinner.setSelection(0);
        else
            spinner.setSelection(1);

        if(remindStr.equals("Now"))
            rb6.setChecked(true);
        else
            remindTime.setText(remindStr);

        switch (frequency) {
            case "Daily":
                rb1.setChecked(true);
                break;
            case "Weekly":
                rb2.setChecked(true);
                break;
            case "Monthly":
                rb3.setChecked(true);
                break;
            case "Yearly":
                rb4.setChecked(true);
                break;
            case "Once":
                rb5.setChecked(true);
                break;
        }

        if(darkMode)
            checkBox.setChecked(true);

        rb6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindTime.setText("");
            }
        });

        remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(remindTime);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = spinner.getSelectedItem().toString();
                String t;
                String f="";
                Boolean d=false;
                if(rb6.isChecked())
                    t = "Now";
                else
                    t = remindTime.getText().toString();

                int remindId = radioGroup.getCheckedRadioButtonId();
                switch (remindId){
                    case 1000:
                        f = "Daily";
                        break;
                    case 1001:
                        f = "Weekly";
                        break;
                    case 1002:
                        f = "Monthly";
                        break;
                    case 1003:
                        f = "Yearly";
                        break;
                    case 1004:
                        f = "Once";
                        break;
                }

                if(checkBox.isChecked())
                    d = true;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("soundKey", s);
                editor.putString("timeKey", t);
                editor.putString("frequencyKey", f);
                editor.putBoolean("darkmodeKey", d);
                editor.apply();
                Toast.makeText(SettingsActivity.this,"Settings Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Boolean darkMode = sharedpreferences.getBoolean("darkmodeKey",false);
        if(darkMode && ((TextView) parent.getChildAt(0))!=null)
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                time_in.setText(simpleDateFormat.format(calendar.getTime()));
                rb6.setChecked(false);
            }
        };

        new TimePickerDialog(SettingsActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
}
