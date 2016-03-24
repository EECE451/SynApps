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


public class DatabaseHelper451 extends SQLiteOpenHelper {

    public static final String DB_Name = "devices.db";

    // Creation table for devices
    public static final String TABLE_DEVICE = "device";
    public static final String COLUMN_DEVICE_ID = "_Pid";
    public static final String COLUMN_DEVICE_DESCRIPTION = "description";
    public static final String COLUMN_DEVICE_START_DATE = "start_date";
    public static final String COLUMN_DEVICE_END_DATE = "end_date";
    public static final String COLUMN_DEVICE_COST = "estimated_cost";
    public static final String COLUMN_DEVICE_LEADER_ID = "device_id";

    // SQL QUERY FOR DEVICES
    private static final String SQL_CREATE_TABLE_DEVICE = "CREATE TABLE " + TABLE_DEVICE + "("
            + COLUMN_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DEVICE_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_DEVICE_START_DATE + " TEXT NOT NULL, "
            + COLUMN_DEVICE_END_DATE + " TEXT NOT NULL, "
            + COLUMN_DEVICE_COST + " REAL NOT NULL, "
            + COLUMN_DEVICE_LEADER_ID + " INTEGER NOT NULL "
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
        db.execSQL("drop table if exists " + TABLE_DEVICE +"");
        onCreate(db);
    }

    public boolean insertData(String desc,String startdate, String enddate, String Cost,String leaderid)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DEVICE_DESCRIPTION, desc);
        contentValues.put(COLUMN_DEVICE_START_DATE,startdate);
        contentValues.put(COLUMN_DEVICE_END_DATE,enddate);
        contentValues.put(COLUMN_DEVICE_COST, Cost);
        contentValues.put(COLUMN_DEVICE_LEADER_ID, leaderid);

        long result = db.insert(TABLE_DEVICE,null,contentValues);
        if(result == -1)return false;
        else return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICE,null);
        return result;
    }


    public Cursor getdonationdonor(String DEVICEMAC)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select count(*) from "+TABLE_DEVICE+" where _Did = "+DEVICEMAC,null);
        return result;
    }

    public Cursor checkDEVICE(String DEVICEID, String phone)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_DEVICE+" where name = '"+DEVICEID+"' and phone_number = '"+phone+"'",null);
        return result;
    }

}
