package com.example.calendarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;


public class ListEventActivity extends AppCompatActivity {

    private static final String TAG = "ListEventActivity";
    private static final int RB1_ID = 1000;//first radio button id
    private static final int RB2_ID = 1001;//second radio button id
    private static final int RB3_ID = 1002;//third radio button id
    private String dateString=LocalDate.now().toString();
    RadioButton rb1,rb2,rb3;
    RadioGroup radioGroup;
    EditText date_time_in;
    DatabaseHelper mDatabaseHelper;
    Button buttonShow,buttonMenu;
    CalendarView cv;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event);
        mListView = (ListView) findViewById(R.id.listView);
        //date_time_in=findViewById(R.id.date_time_input);
        buttonShow = findViewById(R.id.buttonShow);
        buttonMenu = findViewById(R.id.buttonMenu);
        cv = findViewById(R.id.calendarView);
        radioGroup = findViewById(R.id.radioGr);
        rb1 = findViewById(R.id.radioDaily);
        rb2 = findViewById(R.id.radioWeekly);
        rb3 = findViewById(R.id.radioMonthly);
        rb1.setId(RB1_ID);
        rb2.setId(RB2_ID);
        rb3.setId(RB3_ID);
        rb1.setChecked(true);

        mDatabaseHelper = new DatabaseHelper(this);



        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                String m = Integer.toString(month);
                String d = Integer.toString(dayOfMonth);
                if (dayOfMonth<10)
                    d = "0"+d;
                if(month<10)
                    m = "0"+m;

                dateString = year + "-" + m + "-" + d;
                //date_time_in.setText(dateString);
            }
        });


        /*date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(date_time_in);
            }
        });*/

        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListEventActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //toastMessage("Select a date");
                Cursor data = mDatabaseHelper.getData();
                ArrayList<String> listData = new ArrayList<>();
                int btn = radioGroup.getCheckedRadioButtonId();

                String[] tokens = dateString.split("-");
                String currentYear = tokens[0];
                String currentMonth = tokens[1];
                String currentDay = tokens[2];
                int currDay = Integer.parseInt(currentDay);

                if (btn == RB1_ID){
                    while(data.moveToNext()){
                        //get the value from the database in column 1
                        //then add it to the ArrayList
                        String start = data.getString(2);
                        String days = start.substring(8,10);
                        String months = start.substring(5,7);
                        String years = start.substring(0,4);

                        if (days.equals(currentDay) && months.equals(currentMonth) && years.equals(currentYear))
                            listData.add(data.getString(1));
                    }
                }

                if (btn == RB2_ID){
                    while(data.moveToNext()){
                        //get the value from the database in column 1
                        //then add it to the ArrayList
                        String start = data.getString(2);

                        String days = start.substring(8,10);
                        String months = start.substring(5,7);
                        String years = start.substring(0,4);
                        int day = Integer.parseInt(days);

                        if ((currDay+7 > day) && (day >= currDay ) && months.equals(currentMonth) && years.equals(currentYear))
                            listData.add(data.getString(1));
                    }
                }

                if (btn == RB3_ID){
                    while(data.moveToNext()){
                        //get the value from the database in column 1
                        //then add it to the ArrayList
                        String start = data.getString(2);

                        String months = start.substring(5,7);
                        String years = start.substring(0,4);
                        if (months.equals(currentMonth) && years.equals(currentYear))
                            listData.add(data.getString(1));
                    }
                }

                populateListView(data,listData);
            }
        });
    }

    private void populateListView(Cursor data, ArrayList listData) {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");
        //create the list adapter and set the adapter
        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        ArrayAdapter <String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);
                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                if ( sharedPreferences.getBoolean("darkmodeKey", false)) {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        mListView.setAdapter(arrayAdapter);

        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + name);

                Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                int itemID = -1;
                String start="";
                String end="";
                String desc="";
                String remind="";
                String location="";
                String notifyOption="";

                while(data.moveToNext()){
                    itemID = data.getInt(0);
                    start = data.getString(2);
                    end = data.getString(3);
                    desc = data.getString(4);
                    remind = data.getString(5);
                    location = data.getString(6);
                    notifyOption = data.getString(7);
                }
                if(itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent editScreenIntent = new Intent(ListEventActivity.this, EditEventActivity.class);
                    editScreenIntent.putExtra("id",itemID);
                    editScreenIntent.putExtra("name",name);
                    editScreenIntent.putExtra("start", start);
                    editScreenIntent.putExtra("end", end);
                    editScreenIntent.putExtra("desc", desc);
                    editScreenIntent.putExtra("reminder",remind);
                    editScreenIntent.putExtra("location",location);
                    editScreenIntent.putExtra("notify",notifyOption);
                    startActivity(editScreenIntent);
                }
                else
                    toastMessage("No ID associated with that name");
            }
        });
    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(ListEventActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
