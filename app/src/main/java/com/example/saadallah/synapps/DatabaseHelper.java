package com.example.saadallah.synapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JadHosn on 3/27/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_Name = "devices2021.db";
    public static final String TABLE_DEVICESS = "DevicesTable";
    public static final String COLUMN_DEVICESS_ID = "_Did";
    public static final String COLUMN_DEVICESS_MAC = "device_MAC";
    public static final String COLUMN_DEVICESS_LAST_TIME_DETECTION = "lt_detection";
    public static final String COLUMN_DEVICESS_LAST_TIME_INIT = "lt_init";
    public static final String COLUMN_DEVICESS_LAST_TIME_RANGE = "lt_range";
    public static final String COLUMN_DEVICESS_DETECTION_FREQUENCY= "detection_frequency";
    public static final String COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION = "cum_detection_duration";
    public static final String COLUMN_DEVICESS_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_DEVICESS_DESCRIPTIVE_NAME = "description_name";
    public static final String COLUMN_DEVICESS_EXISTS = "device_exists";

    //---------------------------------------------------------------------------------------------
    private static final String SQL_CREATE_TABLE_DEVICESS = "CREATE TABLE " + TABLE_DEVICESS + "("
            + COLUMN_DEVICESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DEVICESS_MAC + " TEXT NOT NULL UNIQUE, "
            + COLUMN_DEVICESS_LAST_TIME_DETECTION + " NUMERIC, "
            + COLUMN_DEVICESS_LAST_TIME_INIT + " NUMERIC,"
            + COLUMN_DEVICESS_LAST_TIME_RANGE + " REAL, "
            + COLUMN_DEVICESS_DETECTION_FREQUENCY + " REAL, "
            + COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION + " REAL, "
            + COLUMN_DEVICESS_PHONE_NUMBER + " VARCHAR(50), "
            + COLUMN_DEVICESS_DESCRIPTIVE_NAME + " TEXT, "
            + COLUMN_DEVICESS_EXISTS + " INTEGER "
            +");";

    public DatabaseHelper(Context context) {
        super(context, DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DEVICESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DEVICESS + "");
        onCreate(db);
    }

    public void onDropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table DevicesTable");

    }



    public boolean insertData(String MAC, String lt_detection, String lt_init, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number, String description_name, int device_exists) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(COLUMN_DEVICESS_MAC, MAC);
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_DETECTION, lt_detection);
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_RANGE, lt_range);
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_INIT, lt_init);
        contentValues.put(COLUMN_DEVICESS_DETECTION_FREQUENCY, detection_frequency);
        contentValues.put(COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION, cum_detection_duration);
        contentValues.put(COLUMN_DEVICESS_PHONE_NUMBER, phone_number);
        contentValues.put(COLUMN_DEVICESS_DESCRIPTIVE_NAME, description_name);
        contentValues.put(COLUMN_DEVICESS_EXISTS, device_exists);

        long result = db.insert(TABLE_DEVICESS, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public boolean updateDescriptionName(String MAC, String description_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_MAC, MAC);
        contentValues.put(COLUMN_DEVICESS_DESCRIPTIVE_NAME, description_name);
        db.update(TABLE_DEVICESS, contentValues, "device_MAC =?", new String[]{MAC});
        return true;
    }

    //to use this function, we must access the db first to get the detection frequency
    public boolean updateDetectionFrequency(String MAC, int detection_freq)
    {   int detection_freq_updated = detection_freq +1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_MAC, MAC);
        contentValues.put(COLUMN_DEVICESS_DETECTION_FREQUENCY, detection_freq_updated);
        db.update(TABLE_DEVICESS, contentValues, "device_MAC =?", new String[]{MAC});
        return true;
    }

    public boolean updateExistsStatus(String MAC, int device_exists)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_MAC, MAC);
        contentValues.put(COLUMN_DEVICESS_EXISTS, device_exists);
        db.update(TABLE_DEVICESS, contentValues, "device_MAC =?", new String[]{MAC});
        return true;
    }

    //not yet used
    public boolean updatebyDescriptionName(String description_name, String lt_detection,String lt_init, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_DETECTION, lt_detection);
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_RANGE, lt_range);
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_INIT, lt_init);
        contentValues.put(COLUMN_DEVICESS_DETECTION_FREQUENCY, detection_frequency);
        contentValues.put(COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION, cum_detection_duration);
        contentValues.put(COLUMN_DEVICESS_PHONE_NUMBER, phone_number);
        contentValues.put(COLUMN_DEVICESS_DESCRIPTIVE_NAME, description_name);
        db.update(TABLE_DEVICESS, contentValues,"description_name =?",new String[] { description_name });
        return true;
    }

    public Integer deleteData(String MAC) // returns the number of affected rows
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_DEVICESS, "device_MAC =?", new String[]{MAC});

    }

    public Cursor getAllData()
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_DEVICESS, null);
        return result;
    }


    public Cursor getDatabyID(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICESS+" where _Did = "+id,null);
        return result;
    }

    public Cursor getDatabyMAC(String MAC)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_DEVICESS + " where device_MAC = '" + MAC + "'", null);
        return result;

    }
    public Cursor getMAC_ID()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select _Did, device_MAC from " + TABLE_DEVICESS, null);
        return result;

    }
    //reset flags function
    public boolean resetFlags()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
       // db.update(TABLE_DEVICESS, contentValues, "device_MAC =?",);


        db.rawQuery("UPDATE "+ TABLE_DEVICESS + " SET '"+ COLUMN_DEVICESS_EXISTS +"' = " + "('"+COLUMN_DEVICESS_EXISTS +"' * -1)", null);
        db.rawQuery("update DevicesTable set device_exists = 0 ",null);
        return true;
    }




}

