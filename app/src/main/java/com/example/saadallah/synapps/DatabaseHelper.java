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

    public static final String DB_Name = "devices2027.db";
    public static final String TABLE_DEVICESS = "DevicesTable";
    public static final String COLUMN_DEVICESS_ID = "_Did";
    public static final String COLUMN_DEVICESS_MAC = "device_MAC";
    public static final String COLUMN_DEVICESS_LAST_TIME_DETECTION = "ltdetection";
    public static final String COLUMN_DEVICESS_LAST_TIME_INIT = "ltinit";
    public static final String COLUMN_DEVICESS_LAST_TIME_RANGE = "ltrange";
    public static final String COLUMN_DEVICESS_DETECTION_FREQUENCY= "detectionfrequency";
    public static final String COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION = "cumdetectionduration";
    public static final String COLUMN_DEVICESS_PHONE_NUMBER = "phonenumber";
    public static final String COLUMN_DEVICESS_DESCRIPTIVE_NAME = "descriptionname";
    public static final String COLUMN_DEVICESS_EXISTS = "deviceexists";

    //---------------------------------------------------------------------------------------------
    private static final String SQL_CREATE_TABLE_DEVICESS = "CREATE TABLE " + TABLE_DEVICESS + "("
            + COLUMN_DEVICESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DEVICESS_MAC + " TEXT NOT NULL UNIQUE, "
            + COLUMN_DEVICESS_LAST_TIME_DETECTION + " NUMERIC, "
            + COLUMN_DEVICESS_LAST_TIME_INIT + " NUMERIC,"
            + COLUMN_DEVICESS_LAST_TIME_RANGE + " NUMERIC, "
            + COLUMN_DEVICESS_DETECTION_FREQUENCY + " REAL, "
            + COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION + " NUMERIC, "
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



    public boolean insertData(String MAC, long lt_detection, long lt_init, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number, String description_name, int device_exists) {
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
    {  int detection_freq_updated = detection_freq + 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_MAC, MAC);
        contentValues.put(COLUMN_DEVICESS_DETECTION_FREQUENCY, detection_freq_updated);
        db.update(TABLE_DEVICESS, contentValues, "device_MAC =?", new String[]{ MAC });
        return true;
    }

    public boolean updateCumulativeDetectionDuration(String MAC,long last_lt_range, long cumulative)
    {   long cumulative_updated = cumulative + last_lt_range;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_MAC, MAC);
        contentValues.put(COLUMN_DEVICESS_CUMULATIVE_DETECTION_DURATION, cumulative_updated);
        db.update(TABLE_DEVICESS, contentValues, "device_MAC =?", new String[]{ MAC });
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
    public boolean updatebyDescriptionName(String description_name, long lt_detection,long lt_init, long lt_range, int detection_frequency,long cum_detection_duration,String phone_number)
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


    public Cursor getDetectionFrequency(String MAC)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select detectionfrequency from " + TABLE_DEVICESS + " where device_MAC = '" + MAC + "'", null);
        return result;
    }

    public Cursor getCumulativeDuration(String MAC)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select cumdetectionduration from " + TABLE_DEVICESS + " where device_MAC = '" + MAC + "'", null);
        return result;
    }


    public Cursor getDatabyID(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICESS+" where _Did = "+id,null);
        return result;
    }

    public Cursor getlttimeinit(String MAC)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select ltinit from "+TABLE_DEVICESS+" where device_MAC = '" + MAC + "'",null);
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
        Cursor result = db.rawQuery("select _Did from " + TABLE_DEVICESS, null);
        for(int i = 1;i<=result.getCount();i++)
        {
            contentValues.put(COLUMN_DEVICESS_EXISTS, 0);
            db.update(TABLE_DEVICESS, contentValues,"_Did =?",new String[] {Integer.toString(i) });
        }

        return true;
    }


    public boolean update_lt_detection_lt_range(String MAC, long lt_detection,long lt_range)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_DETECTION, lt_detection);
        contentValues.put(COLUMN_DEVICESS_LAST_TIME_RANGE, lt_range);

        db.update(TABLE_DEVICESS, contentValues,"device_MAC =?",new String[] { MAC });
        return true;
    }


}

