package com.example.saadallah.synapps;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class NetworkDetails extends AppCompatActivity {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    // Arrays of data gathered from the devices

    private String[] deviceNames; // or set as input parameter the whole database
    private String[] MACaddresses;
    private String[] phoneNumbers;
    private String[] lastDetected; //can create a new class called Time, may facilitate a lot...
    private String[] lastStarted; //Time
    private String[] lastDuration; // Time
    private String[] numberOfDetections;
    private String[] cummulativeTime; // Time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_details);

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

        ListView network_details_listview = (ListView)findViewById(R.id.network_details_listview); // referencing the ListView object

        // populating the arrays
        // Note that I populated the array from static dummy string arrays for testing.
        // We need to populate them programmatically using our data

        // Try to do the following: input: database; output: write directly into the ListView (no CustomAdapter needed anymore)
        // activity_network_details.xml is NOT modified
        // CustomAdapterNetwork.java + network_details_list.xml are not called/used anymore
        // Changes in NetworkDetails.java --> comment out the code below and rewrite the query;
        // also comment out the private string arrays

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
    }


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
}
