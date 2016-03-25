package com.example.saadallah.synapps;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;


/**
 * Created by Jad Aboul Hosn on 3/24/2016.
 */

// We must add a clear function
// For the dates, there is the possibility to add java.util.date in order to manage it well for the
// detection time and range

public class DatabaseHelper451 extends SQLiteOpenHelper {

    public static final String DB_Name = "devices101.db";
    public static final String TABLE_DEVICE = "device";
    public static final String COLUMN_DEVICE_ID = "_Did_MAC";
    public static final String COLUMN_DEVICE_LAST_TIME_DETECTION = "lt_detection";
    public static final String COLUMN_DEVICE_LAST_TIME_RANGE = "lt_range";
    public static final String COLUMN_DEVICE_DETECTION_FREQUENCY= "detection_frequency";
    public static final String COLUMN_DEVICE_CUMULATIVE_DETECTION_DURATION = "cum_detection_duration";
    public static final String COLUMN_DEVICE_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_DEVICE_DESCRIPTIVE_NAME = "description_name";

    //---------------------------------------------------------------------------------------------
    private static final String SQL_CREATE_TABLE_DEVICE = "CREATE TABLE " + TABLE_DEVICE + "("
            + COLUMN_DEVICE_ID + " TEXT PRIMARY KEY, "
            + COLUMN_DEVICE_LAST_TIME_DETECTION + " NUMERIC, "
            + COLUMN_DEVICE_LAST_TIME_RANGE + " REAL, "
            + COLUMN_DEVICE_DETECTION_FREQUENCY + " INTEGER, "
            + COLUMN_DEVICE_CUMULATIVE_DETECTION_DURATION + " REAL, "
            + COLUMN_DEVICE_PHONE_NUMBER + " VARCHAR(50), "
            + COLUMN_DEVICE_DESCRIPTIVE_NAME + " TEXT "
            +");";

    public DatabaseHelper451(Context context) {
        super(context, DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DEVICE + "");
        onCreate(db);
    }

    public boolean insertData(String MAC, String lt_detection, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number, String description_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(COLUMN_DEVICE_ID, MAC);
        contentValues.put(COLUMN_DEVICE_LAST_TIME_DETECTION, lt_detection);
        contentValues.put(COLUMN_DEVICE_LAST_TIME_RANGE, lt_range);
        contentValues.put(COLUMN_DEVICE_DETECTION_FREQUENCY, detection_frequency);
        contentValues.put(COLUMN_DEVICE_CUMULATIVE_DETECTION_DURATION, cum_detection_duration);
        contentValues.put(COLUMN_DEVICE_PHONE_NUMBER, phone_number);
        contentValues.put(COLUMN_DEVICE_DESCRIPTIVE_NAME, description_name);

        long result = db.insert(TABLE_DEVICE, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICE,null);
        return result;
    }


    public Cursor getDevice(String MAC)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select count(*) from "+TABLE_DEVICE+" where _Did_MAC = "+MAC,null);
        return result;
    }

    public Cursor checkDevice(String MAC)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICE+" where _Did_MAC = '"+MAC+"'",null);
        return result;
    }

    /*
    public int update(String MAC, String lt_detection, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number, String description_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        return 1;
    }
    */

}

