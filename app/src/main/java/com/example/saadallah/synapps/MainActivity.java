package com.example.saadallah.synapps;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.net.InetAddress;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, DeviceActionListener {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    // WiFi p2p stuff
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WiFiDirectBroadcastReceiver mReceiver;
    IntentFilter p2pIntent;

    // Bluetooth stuff
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //Cellular Network
    TelephonyManager teleMan;

    // Discovered Device List
    private String[] peersMacArrayStr;
    private ArrayList<WifiP2pDevice> PeerNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //-----------------------------------------------------------------------------------
        // WiFi p2p status checking

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        p2pIntent = new IntentFilter();
        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);


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
                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    Log.d("wifiIsEnabled=", "true");
                }
                else {
                    wifiManager.setWifiEnabled(false);
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
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    Log.d("bluetoothIsEnabled=", "true");
                }
                else {
                    mBluetoothAdapter.disable();
                    Log.d("bluetoothIsEnabled=", "false");
                }
            }
        });

        //Cellular Network
        teleMan =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        //-------------------------------------------------------------------------------------------------------------
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() { // starts discovering peers
            @Override
            public void onSuccess() {
                Log.d("p2p Notification", "Starting Discovery");
                Toast.makeText(getApplicationContext(), "Starting Discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Could not initiate peer discovery", Toast.LENGTH_SHORT).show();


            }
        });

        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        PeerNames = mReceiver.getPeerNames(); // Now that we have a list of peers, we try to connect to each of them
        peersMacArrayStr = new String[PeerNames.size()];
         //error before: size =0

        for (int i=0; i<PeerNames.size(); i++){

            // retrieve MAC Address of device i
            WifiP2pDevice targetDevice = PeerNames.get(i);
            peersMacArrayStr[i] = targetDevice.deviceAddress;

            // connect to all the devices
            connect(i);
        }



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.openDrawer(Gravity.LEFT);
        }
        else{
            drawer.closeDrawer(Gravity.LEFT);
        }

        int id = item.getItemId();

        if (id == R.id.action_connectivity_state) {
            Intent connectivityStateIntent = new Intent(this, Connectivity_State.class);
            connectivityStateIntent.putExtra("bluetooth_state", mBluetoothAdapter.isEnabled());
            connectivityStateIntent.putExtra("wifi_state", wifiManager.isWifiEnabled());
            connectivityStateIntent.putExtra("network_type", teleMan.getNetworkType());
            startActivity(connectivityStateIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, p2pIntent);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void onClickClearData(View view) { //Don't forget to implement this method!
    }

    public void onGenerateGraphClick(View view) { //Don't forget to implement this method!
    }

    public void onNetworkDetailsClick(View view) {
        Intent networkDetailsIntent = new Intent(this, NetworkDetails.class);
        startActivity(networkDetailsIntent);
    }

    public void onClickOk(View view) { //Don't forget to implement this method!
    }

    public void onClickIgnore(View view) { //Don't forget to implement this method!
    }

    public void onClickYes(View view) { //Don't forget to implement this method!
    }

    public void onClickNo(View view) { //Don't forget to implement this method!

    }

    @Override
    public void connect(int deviceIndex) {
        // Picking the first device found on the network.
        WifiP2pDevice device = PeerNames.get(deviceIndex);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Connect succeeded!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//   @Override
//    public void connect() {
//        // Picking the first device found on the network.
//        WifiP2pDevice device = PeerNames.get(0);
//
//        WifiP2pConfig config = new WifiP2pConfig();
//        config.deviceAddress = device.deviceAddress;
//        config.wps.setup = WpsInfo.PBC;
//
//        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                Toast.makeText(MainActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more

        if (mManager != null) {
            Toast.makeText(getApplicationContext(), "Channel lost. Trying again", Toast.LENGTH_SHORT).show();
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(getApplicationContext(), "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void disconnect(WifiP2pDevice device) {
        WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Disconnected Successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Error Disconnecting", Toast.LENGTH_SHORT).show();
            }
        };
        if (device.status == WifiP2pDevice.INVITED) {
            mManager.cancelConnect(mChannel, actionListener);
        } else if (device.status == WifiP2pDevice.CONNECTED) {
            mManager.removeGroup(mChannel, actionListener);
        }
    }

    @Override
    public void disconnect() {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Disconnected Successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Error Disconnecting", Toast.LENGTH_SHORT).show();
            }
        });
    }

}


