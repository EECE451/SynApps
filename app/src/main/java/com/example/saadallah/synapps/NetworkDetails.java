package com.example.saadallah.synapps;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class NetworkDetails extends AppCompatActivity {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---
    DatabaseHelper myDb;
    private static Button Btn_ReadAll;
    TextView txt_ReadAllData, txt_ReadSpecific;
    android.widget.EditText t1,t2;




    //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_details);


        //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---

        myDb = new DatabaseHelper(this);
        txt_ReadAllData = (TextView)findViewById(R.id.txt_ReadAllData);
        txt_ReadSpecific = (TextView)findViewById(R.id.txt_ReadSpecific);

        //Filling Date
        java.util.Date date = new java.util.Date();
        String Detection_time = String.format("%tc", date);

        //Dummy Filling
//        myDb.insertData("12AB25CF57CE",1234567,12367,1278,5,4999,"03649774","Arnabit",0,1);
//        myDb.insertData("12AB25C234CE",123456,12567,5678,7,4775,"06849774","batata",0,1);
//        myDb.insertData("124212312CEA",12345,127,45678,6,488,"03655674","Cauliflower",0,1);



        t2 = (android.widget.EditText)findViewById(R.id.t2);

        //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---



        //-----------------------------------------------------------------------------------
        // drawer stuff! To copy paste on each activity...
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        bar = this.getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.myBlue)));

        toggle = new ActionBarDrawerToggle(this, drawer,0,0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        if (toggle != null) {
            toggle.syncState();
            drawer.setDrawerListener(toggle);
        }

        //---------------------------------------------------------------------------------



        Intent intent = getIntent();
        String[] mystringarray = intent.getStringArrayExtra("MacArray");

        ///////////////

        Cursor result2 = myDb.getMAC_ID();
        int devices_number =  result2.getCount(); // the total number of devices stored
        Log.d("number", String.valueOf(devices_number));

        //////////////// Don't touch me

        showall_orderOfAddition();


    }



    //--------------------------------------------------------------------------------------------------
    //Settings option already implemented
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.openDrawer(Gravity.LEFT);
        }
        else{
            drawer.closeDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void showall_orderOfAddition() {


        Cursor result = myDb.getAllData();
        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());

    }



    public void getAllData_ascen_descriptionname() {


        Cursor result = myDb.getAllData_ascen_descriptionname();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());


    }

    public void getAllData_ascen_detectionFrequency() {


        Cursor result = myDb.getAllData_ascen_detectionFrequency();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());


    }


    public void getAllData_ascen_ltdetection() {


        Cursor result = myDb.getAllData_ascen_ltdetection();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());


    }


    public void getAllData_ascen_cumulativedetection() {


        Cursor result = myDb.getAllData_ascen_cumulativedetection();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());


    }


    public void getAllData_ascen_ltrange() {


        Cursor result = myDb.getAllData_ascen_ltrange();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());


    }


}



