package com.example.saadallah.synapps;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Connectivity_State extends AppCompatActivity {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    private WifiManager wifiManager;

    // Bluetooth stuff
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Textviews References


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity__state);

        //--------------------------------------------------------------------------------
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

        toggle.syncState();
        drawer.setDrawerListener(toggle);

        //------------------------------------------------------------------------------------
        // setting the toggle button in drawer

        //Wifi

        Switch wifiSwitch = (Switch) findViewById(R.id.wifi_switch);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()) // checks is Wifi is ON or OFF and sets the initial value of the toggle
            wifiSwitch.setChecked(true);
        else
            wifiSwitch.setChecked(false);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // switches wifi
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // Enable/Disable wifi when switch event
                TextView wifi_value_textview = (TextView) findViewById(R.id.bt_value);
                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    wifi_value_textview.setText("Enabled");
                    Log.d("wifiIsEnabled=", "true");
                }
                else {
                    wifiManager.setWifiEnabled(false);
                    wifi_value_textview.setText("Disabled");
                    Log.d("wifiIsEnabled=", "false");
                }
            }
        });

        //Bluetooth
        Switch bluetoothSwitch = (Switch) findViewById(R.id.bluetooth_switch);

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth, popup here??
        }

        if(mBluetoothAdapter.isEnabled()) // checks is Bluetooth is ON or OFF and sets the initial value of the toggle
            bluetoothSwitch.setChecked(true);
        else
            bluetoothSwitch.setChecked(false);

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // switches wifi
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // Enable/Disable wifi when switch event
                TextView bt_value_textview = (TextView) findViewById(R.id.bt_value);
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    bt_value_textview.setText("Enabled");
                    Log.d("bluetoothIsEnabled=", "true");
                } else {
                    mBluetoothAdapter.disable();
                    bt_value_textview.setText("Disabled");
                    Log.d("bluetoothIsEnabled=", "false");
                }
            }
        });


        Intent receivedIntent = getIntent();
        boolean bt_state_value = receivedIntent.getBooleanExtra("bluetooth_state", false);
        boolean wifi_state_value = receivedIntent.getBooleanExtra("wifi_state", false);
        int cell_network_type = receivedIntent.getIntExtra("network_type", 0);
        String phone_number = receivedIntent.getStringExtra("phone_number");

        TextView bt_value_textview = (TextView) findViewById(R.id.bt_value);
        if (bt_state_value)
            bt_value_textview.setText("Enabled");
        else
            bt_value_textview.setText("Disabled");

        TextView wifi_value_textview = (TextView) findViewById(R.id.wifi_value);
        if (wifi_state_value)
            wifi_value_textview.setText("Enabled");
        else
            wifi_value_textview.setText("Disabled");

        TextView cell_value_textview = (TextView) findViewById(R.id.cell_value);

        switch (cell_network_type)
        {
            case 7:
                cell_value_textview.setText("1xRTT");
                break;
            case 4:
                cell_value_textview.setText("CDMA");
                break;
            case 2:
                cell_value_textview.setText("EDGE");
                break;
            case 14:
                cell_value_textview.setText("eHRPD");
                break;
            case 5:
                cell_value_textview.setText("EVDO rev. 0");
                break;
            case 6:
                cell_value_textview.setText("EVDO rev. A");
                break;
            case 12:
                cell_value_textview.setText("EVDO rev. B");
                break;
            case 1:
                cell_value_textview.setText("GPRS");
                break;
            case 8:
                cell_value_textview.setText("HSDPA");
                break;
            case 10:
                cell_value_textview.setText("HSPA");
                break;
            case 15:
                cell_value_textview.setText("HSPA+");
                break;
            case 9:
                cell_value_textview.setText("HSUPA");
                break;
            case 11:
                cell_value_textview.setText("iDen");
                break;
            case 13:
                cell_value_textview.setText("LTE");
                break;
            case 3:
                cell_value_textview.setText("UMTS");
                break;
            case 0:
                cell_value_textview.setText("Unknown");
                break;
        }

        TextView phone_number_textview = (TextView) findViewById(R.id.phone_nb_value);
        phone_number_textview.setText(phone_number);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connectivity__state, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (!drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.openDrawer(Gravity.LEFT);
        }
        else{
            drawer.closeDrawer(Gravity.LEFT);
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
