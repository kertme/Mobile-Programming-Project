package com.example.calendarapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    EditText date_time_in,date_time_in2,textEventName,textEventDesc,textLocation;
    Button saveButton;
    DatabaseHelper mDatabaseHelper;
    RadioButton rb1,rb2,rb3,rb4;
    RadioGroup radioGroup;
    CheckBox cbVibrate, cbSound;
    SharedPreferences sharedpreferences;

    private TimePickerDialog timePickerDialog;
    final static int REQUEST_CODE = 1;
    private static final int RB1_ID = 1000;//first radio button id
    private static final int RB2_ID = 1001;//second radio button id
    private static final int RB3_ID = 1002;//third radio button id
    private static final int RB4_ID = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        radioGroup = findViewById(R.id.radioGr);
        rb1 = findViewById(R.id.radioDaily);
        rb2 = findViewById(R.id.radioWeekly);
        rb3 = findViewById(R.id.radioMonthly);
        rb4 = findViewById(R.id.radioYearly);
        date_time_in=findViewById(R.id.date_time_input);
        date_time_in2=findViewById(R.id.date_time_input2);
        textEventName=findViewById(R.id.textEventName);
        textEventDesc=findViewById(R.id.textEventDesc);
        textLocation=findViewById(R.id.textLocation);
        saveButton = findViewById(R.id.buttonSave);
        cbVibrate = findViewById(R.id.cbVibration);
        cbSound = findViewById(R.id.cbSound);

        rb1.setId(RB1_ID);
        rb2.setId(RB2_ID);
        rb3.setId(RB3_ID);
        rb4.setId(RB4_ID);

        mDatabaseHelper = new DatabaseHelper(this);
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        date_time_in.setInputType(InputType.TYPE_NULL);
        date_time_in2.setInputType(InputType.TYPE_NULL);
        date_time_in.setFocusable(false);
        date_time_in.setKeyListener(null);
        date_time_in2.setFocusable(false);
        date_time_in2.setKeyListener(null);

        String time = sharedpreferences.getString("timeKey","Now");
        String frequency = sharedpreferences.getString("frequencyKey","Once");

        if (time.equals("Now")){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            date_time_in.setText(simpleDateFormat.format(date.getTime()));
        }
        else{
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String today = simpleDateFormat.format(date.getTime());
            today = today + " "+time;
            date_time_in.setText(today);
        }

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
                break;
        }

        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date_time_in);
            }
        });

        date_time_in2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date_time_in2);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = textEventName.getText().toString();
                String eventDesc = textEventDesc.getText().toString();
                String startTime = date_time_in.getText().toString();
                String endTime = date_time_in2.getText().toString();
                String location = textLocation.getText().toString();
                String notifyOption="";

                if(cbVibrate.isChecked() && cbSound.isChecked())
                    notifyOption = "Vibrate+Sound";
                else if(!cbVibrate.isChecked() && !cbSound.isChecked())
                    notifyOption = "None";
                else if(cbVibrate.isChecked() && !cbSound.isChecked())
                    notifyOption = "Vibrate";
                else if(!cbVibrate.isChecked() && cbSound.isChecked())
                    notifyOption = "Sound";

                int reminder = radioGroup.getCheckedRadioButtonId();

                if(startTime != "" && endTime != "" && eventName != ""){
                    SaveData(eventName,startTime,endTime,eventDesc,location,notifyOption ,reminder);
                }
            }
        });
    }

    private void SaveData(String eventName, String eventStart, String eventEnd, String eventDesc, String location, String notifyOption, int reminder){
        String remindStr ="";
        switch (reminder){
            case -1:
                remindStr = "";
                break;
            case 1000:
                remindStr = "Daily";
                break;
            case 1001:
                remindStr = "Weekly";
                break;
            case 1002:
                remindStr = "Monthly";
                break;
            case 1003:
                remindStr = "Yearly";
                break;
        }

        long insertDataId = mDatabaseHelper.addData(eventName, eventStart, eventEnd, eventDesc, remindStr, location, notifyOption);

        if (insertDataId != -1) {
            toastMessage("Event Successfully Saved!");

            Calendar cal = StringToDate(eventStart);
            Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
            intent.putExtra("set", "notify");
            intent.putExtra("option",notifyOption);
            intent.putExtra("title",eventName);
            intent.putExtra("desc",eventDesc);

            int id = (int) insertDataId;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),id , intent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

            switch (reminder){
                case -1:
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    break;
                case 1000:
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
                    break;
                case 1001:
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    break;
                case 1002:
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 30, pendingIntent);
                    break;
                case 1003:
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 365 + AlarmManager.INTERVAL_HOUR*6, pendingIntent);
                    break;
            }
        } else
            toastMessage("Something went wrong");
    }



    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(CreateEventActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(CreateEventActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private Calendar StringToDate(String date){
        Calendar calendar=Calendar.getInstance();
        String[] tokens = date.split("-");
        String year = tokens[0];
        String month = tokens[1];
        String day = tokens[2].substring(0,2);
        String hour = tokens[2].substring(3,5);
        String minute = tokens[2].substring(6,8);
        int iYear = Integer.parseInt(year);
        int iMonth = Integer.parseInt(month) -1;
        int iDay = Integer.parseInt(day);
        int iHour = Integer.parseInt(hour);
        int iMinute = Integer.parseInt(minute);

        calendar.set(Calendar.YEAR,iYear);
        calendar.set(Calendar.MONTH,iMonth);
        calendar.set(Calendar.DATE,iDay);
        calendar.set(Calendar.HOUR_OF_DAY,iHour);
        calendar.set(Calendar.MINUTE,iMinute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        return calendar;
    }

    public void showMap(View view){
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(textLocation.getText().toString()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

}