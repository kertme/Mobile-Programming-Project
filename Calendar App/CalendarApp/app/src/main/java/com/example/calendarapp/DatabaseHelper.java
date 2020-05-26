package com.example.calendarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 2/28/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "event_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "NAME";
    private static final String COL3 = "START";
    private static final String COL4 = "FINISH";
    private static final String COL5 = "DESCRIPTION";
    private static final String COL6 = "REMINDER";
    private static final String COL7 = "LOCATION";
    private static final String COL8 = "NOTIFY";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, START TEXT, FINISH TEXT, DESCRIPTION TEXT, REMINDER TEXT, LOCATION TEXT, NOTIFY TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addData(String eventName, String eventStart, String eventEnd, String eventDesc, String remindStr, String location, String notify) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, eventName);
        contentValues.put(COL3, eventStart);
        contentValues.put(COL4, eventEnd);
        contentValues.put(COL5, eventDesc);
        contentValues.put(COL6, remindStr);
        contentValues.put(COL7, location);
        contentValues.put(COL8, notify);

        //Log.d(TAG, "addData: Adding " + eventName + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        return result;
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getSelectedData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT NAME,START, strftime('%Y',START) as \"Year\", strftime('%m',START) as \"Month\", strftime('%W',START) as \"Week\", strftime('%d',START) as \"Day\" FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *" + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, String newStart, String newEnd, String newDesc, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET NAME = " + newName +
                ", START = " + newStart +
                ", FINISH = " + newEnd +
                ", DESCRIPTION = " + newDesc +
                " WHERE ID = " + id + " AND NAME = " + oldName;
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }

    public long update(String newName, String newStart, String newEnd, String newDesc, int id, String remindStr, String locationStr, String notify) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,newName);
        contentValues.put(COL3,newStart);
        contentValues.put(COL4,newEnd);
        contentValues.put(COL5,newDesc);
        contentValues.put(COL6,remindStr);
        contentValues.put(COL7,locationStr);
        contentValues.put(COL8,notify);
        return db.update(TABLE_NAME, contentValues,"ID =" + id, null);
    }


    /**
     * Delete from database
     * @param id
     * @param name
     */
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }

}
