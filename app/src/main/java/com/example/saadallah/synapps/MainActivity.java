package com.example.saadallah.synapps;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, DeviceActionListener {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    // WiFi p2p stuff
    private WifiManager wifiManager;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WiFiDirectBroadcastReceiver mReceiver;
    private final IntentFilter p2pIntent = new IntentFilter();

    // Database helper
    DatabaseHelper myDb =new DatabaseHelper(this);

    // Bluetooth stuff
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //Cellular Network
    TelephonyManager teleMan;
    String phoneNumber;

    // Discovered Device List
    private String[] peersMacArrayStr;
    private ArrayList<WifiP2pDevice> PeerNames;

    // Time discovered = connected Device List
    private Date[] timeDiscovered;

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
                } else {
                    mBluetoothAdapter.disable();
                    Log.d("bluetoothIsEnabled=", "false");
                }
            }
        });

        //----------------------------------------------------------------------------------
        //Cellular Network
        teleMan =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = teleMan.getSimSerialNumber(); // get the phone number

        //-----------------------------------------------------------------------------------
        // WiFi p2p status checking

        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pIntent.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        //-------------------------------------------------------------------------------------------------------------
        // WiFi p2p discovering peers and connecting to them

        // discovering peers

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


        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                synchronized (this) {
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Thread calling connect

                PeerNames = mReceiver.getPeerNames(); // Now that we have a list of peers, we try to connect to each of them
                    peersMacArrayStr = new String[PeerNames.size()];
                    timeDiscovered = new java.util.Date[PeerNames.size()];

                    for (int i = 0; i < PeerNames.size(); i++) {

                        // retrieve MAC Address of device i
                        WifiP2pDevice targetDevice = PeerNames.get(i);
                        peersMacArrayStr[i] = targetDevice.deviceAddress;

                        // removing the columns from the strings in MAC addresses
                        String[] macAddressParts = peersMacArrayStr[i].split(":");
                        peersMacArrayStr[i]= macAddressParts[0]+macAddressParts[1]+macAddressParts[2]+macAddressParts[3]+macAddressParts[4]+macAddressParts[5];

                        Cursor result = myDb.getDatabyMAC(peersMacArrayStr[i]);
                        if (result.getCount() == 0) { // if new device (first time detected)
                        }

                        //saves the time at which the device got connected/discovered
                        timeDiscovered[i] = new java.util.Date();

                        // connect to all the devices
                        connect(i);
                    }

            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        //------------------------------------------------------------------------------------------


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

            Intent connectivityStateIntent = new Intent(MainActivity.this, Connectivity_State.class);
            connectivityStateIntent.putExtra("bluetooth_state", mBluetoothAdapter.isEnabled());
            connectivityStateIntent.putExtra("wifi_state", wifiManager.isWifiEnabled());
            connectivityStateIntent.putExtra("network_type", teleMan.getNetworkType());
            connectivityStateIntent.putExtra("phone_number", phoneNumber); // attach the phone number to the intent
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
        Intent networkDetailsIntent = new Intent(MainActivity.this, NetworkDetails.class);
        networkDetailsIntent.putExtra("MacArray", peersMacArrayStr);
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

    public void onConnectivityClick(View view) {
        Intent connectivityStateIntent = new Intent(MainActivity.this, Connectivity_State.class);
        connectivityStateIntent.putExtra("bluetooth_state", mBluetoothAdapter.isEnabled());
        connectivityStateIntent.putExtra("wifi_state", wifiManager.isWifiEnabled());
        connectivityStateIntent.putExtra("network_type", teleMan.getNetworkType());
        connectivityStateIntent.putExtra("phone_number", phoneNumber); // attach the phone number to the intent
        startActivity(connectivityStateIntent);
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
                Toast.makeText(getApplicationContext(), "Connection succeeded!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Connection failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


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


