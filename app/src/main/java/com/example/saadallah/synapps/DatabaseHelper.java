package com.example.saadallah.synapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_Name = "devices10.db";
    public static final String TABLE_DEVICES = "Devices";
    public static final String COLUMN_DEVICES_ID = "_Did";
    public static final String COLUMN_DEVICES_MAC = "device_MAC";
    public static final String COLUMN_DEVICES_LAST_TIME_DETECTION = "lt_detection";
    public static final String COLUMN_DEVICES_LAST_TIME_RANGE = "lt_range";
    public static final String COLUMN_DEVICES_DETECTION_FREQUENCY= "detection_frequency";
    public static final String COLUMN_DEVICES_CUMULATIVE_DETECTION_DURATION = "cum_detection_duration";
    public static final String COLUMN_DEVICES_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_DEVICES_DESCRIPTIVE_NAME = "description_name";

    //---------------------------------------------------------------------------------------------
    private static final String SQL_CREATE_TABLE_DEVICES = "CREATE TABLE " + TABLE_DEVICES + "("
            + COLUMN_DEVICES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DEVICES_MAC + " TEXT NOT NULL UNIQUE, "
            + COLUMN_DEVICES_LAST_TIME_DETECTION + " NUMERIC, "
            + COLUMN_DEVICES_LAST_TIME_RANGE + " REAL, "
            + COLUMN_DEVICES_DETECTION_FREQUENCY + " REAL, "
            + COLUMN_DEVICES_CUMULATIVE_DETECTION_DURATION + " REAL, "
            + COLUMN_DEVICES_PHONE_NUMBER + " VARCHAR(50), "
            + COLUMN_DEVICES_DESCRIPTIVE_NAME + " TEXT "
            +");";

    public DatabaseHelper(Context context) {
        super(context, DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICES);
    }

    @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_DEVICES + "");
           onCreate(db);
          }





    public boolean insertData(String MAC, String lt_detection, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number, String description_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(COLUMN_DEVICES_MAC, MAC);
        contentValues.put(COLUMN_DEVICES_LAST_TIME_DETECTION, lt_detection);
        contentValues.put(COLUMN_DEVICES_LAST_TIME_RANGE, lt_range);
        contentValues.put(COLUMN_DEVICES_DETECTION_FREQUENCY, detection_frequency);
        contentValues.put(COLUMN_DEVICES_CUMULATIVE_DETECTION_DURATION, cum_detection_duration);
        contentValues.put(COLUMN_DEVICES_PHONE_NUMBER, phone_number);
        contentValues.put(COLUMN_DEVICES_DESCRIPTIVE_NAME, description_name);

        long result = db.insert(TABLE_DEVICES, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public boolean updateDescriptionName(String MAC, String description_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICES_MAC, MAC);
        contentValues.put(COLUMN_DEVICES_DESCRIPTIVE_NAME, description_name);
        db.update(TABLE_DEVICES, contentValues, "device_MAC =?", new String[]{MAC});
        return true;
    }

    //not yet used
    public boolean updatebyDescriptionName(String description_name, String lt_detection, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICES_LAST_TIME_DETECTION, lt_detection);
        contentValues.put(COLUMN_DEVICES_LAST_TIME_RANGE, lt_range);
        contentValues.put(COLUMN_DEVICES_DETECTION_FREQUENCY, detection_frequency);
        contentValues.put(COLUMN_DEVICES_CUMULATIVE_DETECTION_DURATION, cum_detection_duration);
        contentValues.put(COLUMN_DEVICES_PHONE_NUMBER, phone_number);
        contentValues.put(COLUMN_DEVICES_DESCRIPTIVE_NAME, description_name);
        db.update(TABLE_DEVICES, contentValues,"description_name =?",new String[] { description_name });
        return true;
    }

    public Integer deleteData(String MAC) // returns the number of affected rows
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_DEVICES, "device_MAC =?", new String[]{MAC});

    }

    public Cursor getAllData()
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICES,null);
        return result;
    }


    public Cursor getDatabyID(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICES+" where _Did = "+id,null);
        return result;
    }

    public Cursor getDatabyMAC(String MAC)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_DEVICES + " where device_MAC = '" + MAC + "'", null);
        return result;

    }
    public Cursor getMAC_ID()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select _Did, device_MAC from " + TABLE_DEVICES, null);
        return result;

    }



}

