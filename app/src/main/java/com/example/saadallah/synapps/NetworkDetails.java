package com.example.saadallah.synapps;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;


public class NetworkDetails extends AppCompatActivity {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---
    DatabaseHelper myDb;
    TextView txt_ReadAllData;



    //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---


    // Temp Arrays
    int arrayIndex = 0;
    String[] device_name_array;
    String[] mac_address_array;
    String[] phone_number_array;
    String[] detection_frequency_array;
    String[] cumulative_time_array;
    String[] last_time_detected_array;
    String[] last_detection_duration_array;

    ListView myDataList;
    Spinner dataSortingSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_details);

        // ListView and Spinner
        myDataList = (ListView) findViewById(R.id.mydatalist);
        dataSortingSpinner = (Spinner) findViewById(R.id.data_sorting_spinner);


        //---DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area------DB_Area---

        myDb = new DatabaseHelper(this);
        txt_ReadAllData = (TextView)findViewById(R.id.txt_ReadAllData);
        //Filling Date
        java.util.Date date = new java.util.Date();
        String Detection_time = String.format("%tc", date);

        //Dummy Filling
//        myDb.insertData("12AB25CF57CE",1234567,12367,1278,5,4999,"03649774","Arnabit",0,1);
//        myDb.insertData("12AB25C234CE",123456,12567,5678,7,4775,"06849774","batata",0,1);
//        myDb.insertData("124212312CEA",12345,127,45678,6,488,"03655674","Cauliflower",0,1);



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

        device_name_array = new String[devices_number];
        mac_address_array = new String[devices_number];
        phone_number_array = new String[devices_number];
        detection_frequency_array = new String[devices_number];
        cumulative_time_array = new String[devices_number];
        last_time_detected_array = new String[devices_number];
        last_detection_duration_array = new String[devices_number];

        showall_orderOfAddition();


        // Spinner Logic (Adapter)
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_sorting_choice, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataSortingSpinner.setAdapter(spinnerAdapter);

        dataSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                 switch (pos) {
                     case 0:
                        showall_orderOfAddition();
                         break;

                     case 1:
                         getAllData_ascen_ltdetection();
                         break;

                     case 2:
                         getAllData_ascen_descriptionname();
                         break;

                     case 3:
                         getAllData_ascen_detectionFrequency();
                         break;

                     case 4:
                         getAllData_ascen_cumulativedetection();
                         break;

                     case 5:
                         getAllData_ascen_ltrange();
                         break;

                  }


             }

             public void onNothingSelected(AdapterView<?> parent) {
             // Another interface callback
             }
         });



    } //  end onCreate



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

            device_name_array[arrayIndex] = result.getString(8);
            mac_address_array[arrayIndex] = macTranslation(result.getString(1).toUpperCase());
            phone_number_array[arrayIndex] = result.getString(7);
            detection_frequency_array[arrayIndex] = result.getString(5);
            cumulative_time_array[arrayIndex] = msToTime(result.getString(6));
            last_detection_duration_array[arrayIndex] = msToTime(result.getString(4));

            if (phone_number_array[arrayIndex] == "")
                phone_number_array[arrayIndex] = "Unknown";

            Date myDate = new Date(Long.parseLong(result.getString(2)));
            last_time_detected_array[arrayIndex] = myDate.toString();

            arrayIndex ++;
        }
        txt_ReadAllData.setText(buffer.toString());
        arrayIndex = 0;

        NetworkDetailsAdapter myAdapter = new NetworkDetailsAdapter(this, device_name_array, mac_address_array, phone_number_array,
                detection_frequency_array, cumulative_time_array, last_time_detected_array, last_detection_duration_array);

        myDataList.setAdapter(myAdapter);
    }



    public void getAllData_ascen_descriptionname() {


        Cursor result = myDb.getAllData_ascen_descriptionname();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

            device_name_array[arrayIndex] = result.getString(8);
            mac_address_array[arrayIndex] = macTranslation(result.getString(1).toUpperCase());
            phone_number_array[arrayIndex] = result.getString(7);
            detection_frequency_array[arrayIndex] = result.getString(5);
            cumulative_time_array[arrayIndex] = msToTime(result.getString(6));
            last_detection_duration_array[arrayIndex] = msToTime(result.getString(4));

            if (phone_number_array[arrayIndex] == "")
                phone_number_array[arrayIndex] = "Unknown";

            Date myDate = new Date(Long.parseLong(result.getString(2)));
            last_time_detected_array[arrayIndex] = myDate.toString();

            arrayIndex ++;
        }
        txt_ReadAllData.setText(buffer.toString());
        arrayIndex = 0;

        NetworkDetailsAdapter myAdapter = new NetworkDetailsAdapter(this, device_name_array, mac_address_array, phone_number_array,
                detection_frequency_array, cumulative_time_array, last_time_detected_array, last_detection_duration_array);

        myDataList.setAdapter(myAdapter);

    }

    public void getAllData_ascen_detectionFrequency() {


        Cursor result = myDb.getAllData_ascen_detectionFrequency();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

            device_name_array[arrayIndex] = result.getString(8);
            mac_address_array[arrayIndex] = macTranslation(result.getString(1).toUpperCase());
            phone_number_array[arrayIndex] = result.getString(7);
            detection_frequency_array[arrayIndex] = result.getString(5);
            cumulative_time_array[arrayIndex] = msToTime(result.getString(6));
            last_detection_duration_array[arrayIndex] = msToTime(result.getString(4));

            if (phone_number_array[arrayIndex] == "")
                phone_number_array[arrayIndex] = "Unknown";

            Date myDate = new Date(Long.parseLong(result.getString(2)));
            last_time_detected_array[arrayIndex] = myDate.toString();

            arrayIndex ++;
        }
        txt_ReadAllData.setText(buffer.toString());
        arrayIndex = 0;

        NetworkDetailsAdapter myAdapter = new NetworkDetailsAdapter(this, device_name_array, mac_address_array, phone_number_array,
                detection_frequency_array, cumulative_time_array, last_time_detected_array, last_detection_duration_array);

        myDataList.setAdapter(myAdapter);

    }


    public void getAllData_ascen_ltdetection() {


        Cursor result = myDb.getAllData_ascen_ltdetection();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

            device_name_array[arrayIndex] = result.getString(8);
            mac_address_array[arrayIndex] = macTranslation(result.getString(1).toUpperCase());
            phone_number_array[arrayIndex] = result.getString(7);
            detection_frequency_array[arrayIndex] = result.getString(5);
            cumulative_time_array[arrayIndex] = msToTime(result.getString(6));
            last_detection_duration_array[arrayIndex] = msToTime(result.getString(4));

            if (phone_number_array[arrayIndex] == "")
                phone_number_array[arrayIndex] = "Unknown";

            Date myDate = new Date(Long.parseLong(result.getString(2)));
            last_time_detected_array[arrayIndex] = myDate.toString();

            arrayIndex ++;
        }
        txt_ReadAllData.setText(buffer.toString());
        arrayIndex = 0;

        NetworkDetailsAdapter myAdapter = new NetworkDetailsAdapter(this, device_name_array, mac_address_array, phone_number_array,
                detection_frequency_array, cumulative_time_array, last_time_detected_array, last_detection_duration_array);

        myDataList.setAdapter(myAdapter);
    }


    public void getAllData_ascen_cumulativedetection() {


        Cursor result = myDb.getAllData_ascen_cumulativedetection();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

            device_name_array[arrayIndex] = result.getString(8);
            mac_address_array[arrayIndex] = macTranslation(result.getString(1).toUpperCase());
            phone_number_array[arrayIndex] = result.getString(7);
            detection_frequency_array[arrayIndex] = result.getString(5);
            cumulative_time_array[arrayIndex] = msToTime(result.getString(6));
            last_detection_duration_array[arrayIndex] = msToTime(result.getString(4));

            if (phone_number_array[arrayIndex] == "")
                phone_number_array[arrayIndex] = "Unknown";

            Date myDate = new Date(Long.parseLong(result.getString(2)));
            last_time_detected_array[arrayIndex] = myDate.toString();

            arrayIndex ++;
        }
        txt_ReadAllData.setText(buffer.toString());
        arrayIndex = 0;

        NetworkDetailsAdapter myAdapter = new NetworkDetailsAdapter(this, device_name_array, mac_address_array, phone_number_array,
                detection_frequency_array, cumulative_time_array, last_time_detected_array, last_detection_duration_array);

        myDataList.setAdapter(myAdapter);
    }


    public void getAllData_ascen_ltrange() {


        Cursor result = myDb.getAllData_ascen_ltrange();

        StringBuffer buffer = new StringBuffer();
        while (result.moveToNext()) {
            buffer.append(result.getString(0) + " " + result.getString(1) + " " + result.getString(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7)+  " " + result.getString(8)+  " " + result.getString(9) + " " + result.getString(10) + "\n");

            device_name_array[arrayIndex] = result.getString(8);
            mac_address_array[arrayIndex] = macTranslation(result.getString(1).toUpperCase());
            phone_number_array[arrayIndex] = result.getString(7);
            detection_frequency_array[arrayIndex] = result.getString(5);
            cumulative_time_array[arrayIndex] = msToTime(result.getString(6));
            last_detection_duration_array[arrayIndex] = msToTime(result.getString(4));

            if (phone_number_array[arrayIndex] == "")
                phone_number_array[arrayIndex] = "Unknown";

            Date myDate = new Date(Long.parseLong(result.getString(2)));
            last_time_detected_array[arrayIndex] = myDate.toString();

            arrayIndex ++;
        }
        txt_ReadAllData.setText(buffer.toString());
        arrayIndex = 0;

        NetworkDetailsAdapter myAdapter = new NetworkDetailsAdapter(this, device_name_array, mac_address_array, phone_number_array,
                detection_frequency_array, cumulative_time_array, last_time_detected_array, last_detection_duration_array);

        myDataList.setAdapter(myAdapter);

    }

    private String macTranslation(String MAC){

        return MAC.substring(0,2) + ":" + MAC.substring(2,4) + ":" + MAC.substring(4,6) + ":" + MAC.substring(6,8) + ":" +
                MAC.substring(8,10) + ":" + MAC.substring(10, 12); // puts back the MAC address into a standardized form
    }

    private String msToTime(String ms){

        long m = Long.parseLong(ms);

        String h = String.valueOf(m/(1000*3600));
        m = m%(1000*3600);

        String min = String.valueOf(m/(1000*60));
        m = m%(1000*60);

        String s = String.valueOf(m/1000);

        if (Long.parseLong(h) > 0)
            return h + " hours " + min + " min " + s + " seconds";

        else if (Long.parseLong(min) > 0)
            return min + " min " + s + " seconds";

        else
            return s + " seconds";

    }

}
