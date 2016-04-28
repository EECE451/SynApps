package com.example.saadallah.synapps;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
          myDb.insertData("0153456787ae",111111,111111,0,5,4,"03649774","1",0,1);
//        myDb.insertData("0124456789ac",111111,111111,1,9,4,"03649674","1",1);
//        myDb.insertData("0126456789ae",111111,111111,3,2,4,"03549774","1",1);
//        myDb.insertData("0124456789af",111111,111111,5,2,4,"01642774","1",1);
//        myDb.insertData("0125456789af",111111,111111,5,2,4,"01642774","1",1);
//        myDb.insertData("0153451787ae",111111,111111,0,2,4,"03649774","1",1);
//        myDb.insertData("0125556789a1",111111,111111,1,2,4,"03649674","1",1);
//        myDb.insertData("0126451119ae",111111,111111,3,2,4,"03549774","1",1);
//        myDb.insertData("0114116789af",111111,111111,5,2,4,"01642774","1",1);
//        myDb.insertData("1124991199af",111111,111111,5,2,4,"01642774","1",1);
//        myDb.insertData("0153116787ae",111111,111111,0,2,4,"03649774","1",1);
//        myDb.insertData("0124451189ac",111111,111111,1,2,4,"03649674","1",1);
//        myDb.insertData("1126456711ae",111111,111111,3,2,4,"03549774","1",1);
//        myDb.insertData("017777678911",111111,111111,5,2,4,"01642774","1",1);
//        myDb.insertData("0121156789af",111111,111111,5,2,4,"01642774","1",1);


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
//Testing
        /*
        String tempString = "";
        for(int i=0;i<mystringarray.length;i++)
        {
            tempString = tempString + " " + mystringarray[i];
        }
        txt_ReadSpecific.setText(tempString);
        String macAddress = tempString;

        */

            // commented out since we decided to do this in the main activity
//        String[] macAddressParts = macAddress.split(":");
//        String MACS= macAddressParts[0]+macAddressParts[1]+macAddressParts[2]+macAddressParts[3]+macAddressParts[4]+macAddressParts[5];
//        txt_ReadAllData.setText(MACS);
//        myDb.insertData(MACS, Detection_time, 0, 2, 4, "03649774", "1", 1);


        // Dummy filling
         //   myDb.insertData(macAddress,Detection_time,0,2,3,"03649774","1",1);


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
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

        }
        txt_ReadAllData.setText(buffer.toString());

        Cursor result2 = myDb.getMAC_ID();

        StringBuffer buffer2 = new StringBuffer();
        while (result2.moveToNext()) {
            buffer2.append(result2.getString(0)+" "+result2.getString(1)+"\n");
        }
        txt_ReadSpecific.setText(buffer2.toString());

    }

    public void onbtnclick2(View view) {


            Cursor result2 = myDb.getDatabyMAC("0153456787ae");

        StringBuffer buffer = new StringBuffer();
        while (result2.moveToNext()) {
            buffer.append(result2.getString(0)+" "+result2.getString(1)+" "+result2.getString(2)+"\n");
        }
        txt_ReadSpecific.setText(buffer.toString());


    }


    public void onclickbtnreadID(View view) {

        Cursor result = myDb.getDatabyID(t1.getText().toString());
        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0)+" "+result.getString(1)+" "+result.getString(2)+"\n");
        }
        txt_ReadSpecific.setText(buffer.toString());
    }

    public void onbtnclickupdatemac(View view) {

        myDb.updateDescriptionName("1124991199af",t2.getText().toString());
    }

    public void onbtnclickdelete(View view) {
        myDb.deleteData(t2.getText().toString());

//        Cursor result = myDb.getDetectionFrequency("226432b98ea0");
//
//        if (result != null && result.getCount() > 0 ) {
//            result.moveToFirst();
//            String lightRowId = result.getString(0);
//            t2.setText(lightRowId);
//        }
//        myDb.updateDetectionFrequency("2264323ad46a",0);


        //  int testing = result_Detection_Frequency.getCount();

       // myDb.updateDetectionFrequency("2264323ad46a", result.getString(0));
        Cursor result_lt_init = myDb.getlttimeinit("2264323ad46a");

//        long fetched_lt_init = Long.valueOf(result_lt_init.getString(0)).longValue();
//        long lt_range = Detection_time - fetched_lt_init;
//        myDb.update_lt_detection_lt_range(peersMacArrayStr[i],Detection_time,lt_range);


    }

    public void onbtnclickdroptable(View view) {
        myDb.onDropTable();
    }

    public void onbtnclickresetflags(View view) {
        myDb.resetFlags();
    }

    public void onbtnclickclear(View view) {
        myDb.ClearTable();
    }
}



