package com.example.saadallah.synapps;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    DatabaseHelper451 myDb;
    private static Button Btn_ReadAll;
    TextView Txt_Test, Text2;




    //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_details);

        //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---

        myDb = new DatabaseHelper451(this);
        Txt_Test = (TextView)findViewById(R.id.Txt_Test);
        Text2 = (TextView)findViewById(R.id.Text2);

        //Filling Date
        java.util.Date date = new java.util.Date();
        String Detection_time = String.format("%tc", date);

        //Dummy Filling
        myDb.insertData("0153456787AE",Detection_time,0,2,4,"03649774","1");
        myDb.insertData("01:24:45:67:89:AC",Detection_time,1,2,4,"03649674","1");
        myDb.insertData("01:26:45:67:89:AE",Detection_time,3,2,4,"03549774","1");
        myDb.insertData("01:24:45:67:89:AF",Detection_time,5,2,4,"01642774","1");


//                        Cursor result2 = myDb.getDevice("0143456789AB");
//
//                        while (result.moveToNext()) {
//                            Text2.setText(result.getString(0));
//                        }




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


        /*
        ListView network_details_listview = (ListView)findViewById(R.id.network_details_listview); // referencing the ListView object


        deviceNames = getResources().getStringArray(R.array.device_names);
        MACaddresses = getResources().getStringArray(R.array.MAC_addresses);
        phoneNumbers = getResources().getStringArray(R.array.phone_numbers);
        lastDetected = getResources().getStringArray(R.array.last_detected);
        lastStarted = getResources().getStringArray(R.array.last_started);
        lastDuration = getResources().getStringArray(R.array.last_duration);
        numberOfDetections = getResources().getStringArray(R.array.number_detection);
        cummulativeTime = getResources().getStringArray(R.array.cummulative_detection);

        CustomAdapterNetwork myNetworkListAdapter = new CustomAdapterNetwork(this,deviceNames,MACaddresses,phoneNumbers,
                lastDetected,lastStarted,lastDuration,numberOfDetections,cummulativeTime);

        network_details_listview.setAdapter(myNetworkListAdapter);

        */


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


    public void onbtnclick(View view) {

        Cursor result = myDb.getAllData();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + "\n");

        }
        Txt_Test.setText(buffer.toString());

    }
}



