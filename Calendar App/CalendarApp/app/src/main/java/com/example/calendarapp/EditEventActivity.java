package com.example.calendarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditEventActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete,btnShare;
    private EditText editable_item,descText,date_time_in,date_time_in2,locationText;

    RadioButton rb1,rb2,rb3,rb4,rb5;
    RadioGroup radioGroup;
    CheckBox cbVibrate, cbSound;
    DatabaseHelper mDatabaseHelper;

    private String selectedName,selectedStart,selectedEnd,selectedDesc,selectedRemind,selectedLocation,selectedNotify;
    private int selectedID;
    private static final int RB1_ID = 1000;//first radio button id
    private static final int RB2_ID = 1001;//second radio button id
    private static final int RB3_ID = 1002;//third radio button id
    private static final int RB4_ID = 1003;
    private static final int RB5_ID = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);
        editable_item = (EditText) findViewById(R.id.editable_item);
        descText = (EditText) findViewById(R.id.descriptionText);
        locationText = (EditText) findViewById(R.id.locationText);
        date_time_in=findViewById(R.id.date_time_input);
        date_time_in2=findViewById(R.id.date_time_input2);
        radioGroup = findViewById(R.id.radioGr);
        rb1 = findViewById(R.id.radioDaily);
        rb2 = findViewById(R.id.radioWeekly);
        rb3 = findViewById(R.id.radioMonthly);
        rb4 = findViewById(R.id.radioYearly);
        rb5 = findViewById(R.id.radioNoReminder);
        rb1.setId(RB1_ID);
        rb2.setId(RB2_ID);
        rb3.setId(RB3_ID);
        rb4.setId(RB4_ID);
        rb5.setId(RB5_ID);
        cbVibrate = findViewById(R.id.cbVibration);
        cbSound = findViewById(R.id.cbSound);

        mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();
        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value
        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        selectedStart = receivedIntent.getStringExtra("start");
        selectedEnd = receivedIntent.getStringExtra("end");
        selectedDesc = receivedIntent.getStringExtra("desc");
        selectedRemind = receivedIntent.getStringExtra("reminder");
        selectedLocation = receivedIntent.getStringExtra("location");
        selectedNotify = receivedIntent.getStringExtra("notify");

        switch (selectedRemind){
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
            case "":
                rb5.setChecked(true);
                break;
        }

        switch (selectedNotify) {
            case "Vibrate+Sound":
                cbVibrate.setChecked(true);
                cbSound.setChecked(true);
                break;
            case "Vibrate":
                cbVibrate.setChecked(true);
                cbSound.setChecked(false);
                break;
            case "Sound":
                cbVibrate.setChecked(false);
                cbSound.setChecked(true);
                break;
            case "None":
                cbVibrate.setChecked(false);
                cbSound.setChecked(false);
                break;
        }


        //set the text to show the current selected name
        editable_item.setText(selectedName);
        descText.setText(selectedDesc);
        date_time_in.setText(selectedStart);
        date_time_in2.setText(selectedEnd);
        locationText.setText(selectedLocation);


        date_time_in.setInputType(InputType.TYPE_NULL);
        date_time_in2.setInputType(InputType.TYPE_NULL);

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String reminderStr;
                int remindId = radioGroup.getCheckedRadioButtonId();
                switch (remindId){
                    case 1000:
                        selectedRemind = "Daily";
                        break;
                    case 1001:
                        selectedRemind = "Weekly";
                        break;
                    case 1002:
                        selectedRemind = "Monthly";
                        break;
                    case 1003:
                        selectedRemind = "Yearly";
                        break;
                    case 1004:
                        selectedRemind = "";
                        break;
                }

                String newName = editable_item.getText().toString();
                String newStart = date_time_in.getText().toString();
                String newEnd = date_time_in2.getText().toString();
                String newDesc = descText.getText().toString();
                String newLoc = locationText.getText().toString();
                Calendar cal = StringToDate(newStart);
                String notifyOption="";

                if(cbVibrate.isChecked() && cbSound.isChecked())
                    notifyOption = "Vibrate+Sound";
                else if(!cbVibrate.isChecked() && !cbSound.isChecked())
                    notifyOption = "None";
                else if(cbVibrate.isChecked() && !cbSound.isChecked())
                    notifyOption = "Vibrate";
                else if(!cbVibrate.isChecked() && cbSound.isChecked())
                    notifyOption = "Sound";

                long result;

                if(!newName.equals("") && !newStart.equals("") && !newEnd.equals("") && !newDesc.equals("")){
                    result = mDatabaseHelper.update(newName, newStart, newEnd, newDesc, selectedID, selectedRemind, newLoc, notifyOption);
                    String tostmsg = "Event informations updated";
                    toastMessage(tostmsg);

                    Intent alarmIntent = new Intent(getBaseContext(), AlarmReceiver.class);
                    alarmIntent.putExtra("set", "notify");
                    alarmIntent.putExtra("option",notifyOption);
                    alarmIntent.putExtra("title",newName);
                    alarmIntent.putExtra("desc",newDesc);
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),selectedID , alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    switch (remindId){
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
                        case 1004:
                            alarmManager.cancel(pendingIntent);
                            break;
                    }

                    Intent intent = new Intent(EditEventActivity.this, ListEventActivity.class);

                    startActivity(intent);
                }
                else
                    toastMessage("You must enter all informations");
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                toastMessage("Event deleted");

                Intent alarmIntent = new Intent(getBaseContext(), AlarmReceiver.class);
                alarmIntent.putExtra("set", "notify");
                alarmIntent.putExtra("title",selectedName);
                alarmIntent.putExtra("desc",selectedDesc);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),selectedID , alarmIntent, 0);
                alarmManager.cancel(pendingIntent);

                Intent intent = new Intent(EditEventActivity.this, ListEventActivity.class);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Intent> targetedShareIntents = new ArrayList<Intent>();
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
                if (!resInfo.isEmpty()) {
                    for (ResolveInfo resolveInfo : resInfo) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        targetedShareIntent.setType("text/plain");
                        targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, editable_item.getText().toString());
                        targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, editable_item.getText().toString()+":"+
                                descText.getText().toString()+" at "+locationText.getText().toString()+", "+date_time_in.getText().toString());
                        targetedShareIntent.setPackage(packageName);
                        targetedShareIntents.add(targetedShareIntent);
                    }
                    Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
                    startActivity(chooserIntent);
                }
            }
        });
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
                new TimePickerDialog(EditEventActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(EditEventActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
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
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(locationText.getText().toString()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}