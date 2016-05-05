package com.example.saadallah.synapps;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.wifi.WifiInfo;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    DatabaseHelper myDb = new DatabaseHelper(this);

    // Bluetooth stuff
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //Cellular Network
    TelephonyManager teleMan;
    String phoneNumber;

    // Notifs SMS
    boolean notifsflag = false;

    // Discovered Device List
    private String[] peersMacArrayStr;
    private String[] peersNameArrayStr;
    private ArrayList<WifiP2pDevice> PeerNames;

    // Time discovered = connected Device List
    private Date[] timeDiscovered;

    // Location
    //LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    // popup name
    boolean popupNameButtonFlag = false; // flag on click
    String DeviceNameFromUser = "";
    String PhoneNumberFromUser = "";

    // flags
    boolean running = true;
    boolean running2 = true;

    // Device Mac
    String deviceP2pMac;




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

        toggle = new ActionBarDrawerToggle(this, drawer, 0, 0) {
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

        if (wifiManager.isWifiEnabled()) // checks is Wifi is ON or OFF and sets the initial value of the toggle
            wifiSwitch.setChecked(true);
        else
            wifiSwitch.setChecked(false);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // switches wifi
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // Enable/Disable wifi when switch event
                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    Log.d("wifiIsEnabled=", "true");
                } else {
                    wifiManager.setWifiEnabled(false);
                    Log.d("wifiIsEnabled=", "false");
                }
            }
        });

        //WiFi MAC Address retrieval
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        String WiFiMacAddress = getWifiMacAddress();
        Log.d("Device MAC = ", WiFiMacAddress);

        deviceP2pMac = convertMacAddress(WiFiMacAddress);
        Log.d("p2p MAC = ", deviceP2pMac);

        //Bluetooth
        Switch bluetoothSwitch = (Switch) findViewById(R.id.bluetooth_switch);

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth, popup here??
        }

        if (mBluetoothAdapter.isEnabled()) // checks is Bluetooth is ON or OFF and sets the initial value of the toggle
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

        // Notification Switch
        Switch notifSwitch = (Switch) findViewById(R.id.notif_switch);
        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // switches wifi
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // Enable/Disable notifs when switch event
                if (isChecked) {
                    notifsflag = true;

                } else {
                    notifsflag = false;

                }
            }
        });



        //Location


        GPSTracker myLocationListener = new GPSTracker(this);
        Location myLocation = myLocationListener.getLocation();

        if(myLocation != null) {
            Log.d("location", "Longitude=" + myLocation.getLongitude());
            Log.d("location", "Latitude=" + myLocation.getLatitude());
        }
        else {
            Log.d("location", "No location available");
        }

        // Device Mac

        //----------------------------------------------------------------------------------
        //Cellular Network
        teleMan =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //phoneNumber = teleMan.getLine1Number(); // get the phone number

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

        running = true;
        myThread.start();

    }

    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
                // discovering peers
            while (running2) {
                while (running) {

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

                    // Thread calling connect
                    Log.d("Thread", "entered");

                    // Database flags

                    String detected_frequency = null;
                    String fetched_lt = null;
                    String fetched_cumulative = null;
                    String fetched_getOldexistsFlag = null;
                    String fetched_getexistsFlag = null;
                    long fetched_getexistsFlag_long = 0;
                    long fetched_getOldexistsFlag_long = 0;



                    boolean deviceDetectedFlag = mReceiver.isBroadcastFlag(); // THIS IS ZE FLAG... HAPPY NOW??

                    PeerNames = mReceiver.getPeerNames(); // Now that we have a list of peers, we try to connect to each of them
                    //Up here, we are feeling the MAC array string: the thread takes MAC address from devices that are discovered
                    //It only discovers devices
                    peersMacArrayStr = new String[PeerNames.size()];
                    timeDiscovered = new java.util.Date[PeerNames.size()];


                        myDb.updateOldExistsFlag(); // shifts new to old all rows
                        myDb.resetFlags(); // reset the flags to zeros





                    for (int i = 0; i < PeerNames.size(); i++) {
                        //saves the time at which the device got connected/discovered
                        long Detection_time = System.currentTimeMillis();


                        timeDiscovered[i] = new java.util.Date();

                        // retrieve MAC Address of device i
                        WifiP2pDevice targetDevice = PeerNames.get(i);
                        peersMacArrayStr[i] = targetDevice.deviceAddress;


                        // removing the columns from the strings in MAC addresses
                        String[] macAddressParts = peersMacArrayStr[i].split(":");
                        peersMacArrayStr[i] = macAddressParts[0] + macAddressParts[1] + macAddressParts[2] + macAddressParts[3] + macAddressParts[4] + macAddressParts[5];





                        Cursor result_getOldexistsFlag = myDb.getOldExistsFlag(peersMacArrayStr[i]);

                        if (result_getOldexistsFlag != null && result_getOldexistsFlag.getCount() > 0) {
                            result_getOldexistsFlag.moveToFirst();
                            fetched_getOldexistsFlag = result_getOldexistsFlag.getString(0);
                            fetched_getOldexistsFlag_long = Long.parseLong(fetched_getOldexistsFlag); // this long returns the value of old flag
                        }







                        //Checking if its a new device
                        Cursor result = myDb.getDatabyMAC(peersMacArrayStr[i]);

                        if (result.getCount() == 0) {
                            Log.d("Device=", "New");

                            String descriptionNamePopup = null;

                            openDescriptionNamePopup(peersMacArrayStr[i], targetDevice.deviceName);

                            while (!popupNameButtonFlag) ; // wait until a button is pressed

                            Log.d("popup", "New device detected called: " + descriptionNamePopup);

                            if (DeviceNameFromUser == "") {
                                DeviceNameFromUser = targetDevice.deviceName;
                            }

                            myDb.insertData(peersMacArrayStr[i], Detection_time, Detection_time, 0, 1, 0, PhoneNumberFromUser, DeviceNameFromUser, 0, 1);
                            //myDb.updateDescriptionName(peersMacArrayStr[i], DeviceNameFromUser);// to connect to pop up function

                        }

                        else if (result.getCount() == 1)   //Its an old device:  if the MAC appears here, it means that its still connected
                        {
//-----------------------------------------------------------check here for the states only
                            Log.d("Device=", "old");

                            myDb.updateExistsStatus(peersMacArrayStr[i], 1);


                            Cursor result_exist_flag = myDb.getExists(peersMacArrayStr[i]);

                            if (result_exist_flag != null && result_exist_flag.getCount() > 0) {
                                result_exist_flag.moveToFirst();
                                fetched_getexistsFlag = result_exist_flag.getString(0);
                                fetched_getexistsFlag_long = Long.parseLong(fetched_getexistsFlag);
                            }

                            Log.d("fetched_getOldexists_Flag1", peersMacArrayStr[i]);
                            Log.d("fetched_getOldexists_Flag2", String.valueOf(fetched_getOldexistsFlag_long));
                            Log.d("fetched_getexists_Flag3", String.valueOf(fetched_getexistsFlag_long));



                            Cursor result_lt_init = myDb.getlttimeinit(peersMacArrayStr[i]); //fetching the old time stamp stored already in the db

                            if (result_lt_init != null && result_lt_init.getCount() > 0) {
                                result_lt_init.moveToFirst();
                                fetched_lt = result_lt_init.getString(0);
                            }
                            long fetched_lt_init_long = Long.valueOf(fetched_lt);
                            long lt_range = Detection_time - fetched_lt_init_long;



                            if(fetched_getexistsFlag_long == 1 && fetched_getOldexistsFlag_long == 0)
                            {

                                Cursor result_Detection_Frequency = myDb.getDetectionFrequency(peersMacArrayStr[i]);


                                if (result_Detection_Frequency != null && result_Detection_Frequency.getCount() > 0) {
                                    result_Detection_Frequency.moveToFirst();
                                    detected_frequency = result_Detection_Frequency.getString(0);
                                }
                                int detected_frequency_int = 0;
                                detected_frequency_int = Integer.parseInt(detected_frequency);

                                myDb.updateDetectionFrequency(peersMacArrayStr[i], detected_frequency_int);

                                myDb.update_lt_init(peersMacArrayStr[i], Detection_time);



                            }
                            if((fetched_getexistsFlag_long == 1 && fetched_getOldexistsFlag_long == 0)||(fetched_getexistsFlag_long == 1 && fetched_getOldexistsFlag_long == 1))
                                    {
                                        myDb.update_lt_detection_lt_range(peersMacArrayStr[i], Detection_time, lt_range);// anyways update the ltrange since it always must be up to date
                                    }




                            if(fetched_getexistsFlag_long == 1 && fetched_getOldexistsFlag_long == 1) {

                                Cursor result_cum_result = myDb.getCumulativeDuration(peersMacArrayStr[i]);

                                if (result_cum_result != null && result_cum_result.getCount() > 0) {
                                    result_cum_result.moveToFirst();
                                    fetched_cumulative = result_cum_result.getString(0);
                                }
                                long fetched_cumulative_long = Long.valueOf(fetched_cumulative);
                                myDb.updateCumulativeDetectionDuration(peersMacArrayStr[i], lt_range, fetched_cumulative_long);


                            }



//-------------------------------------------------------------Check above here








                        }
                        // connect to all the devices
                        // connect(i);

                        popupNameButtonFlag = false; // resetting the flags
                        DeviceNameFromUser = "";
                    }


                    PeerNames.clear();


                    mReceiver.setBroadcastFlag(false);
                    Log.d("device detected flag", "false");

                    //                   while(!mReceiver.isBroadcastFlag()){} // waits for the flag Broadcast flag to turn true


                    synchronized (this) {
                        try {
                            wait(65000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }


        }
    };

    // Thread definition
    Thread myThread = new Thread(myRunnable);


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, p2pIntent);

        // WiFi p2p discovering peers and connecting to them

        running = true;

        //------------------------------------------------------------------------------------------

        // Notif Switch
        Switch notifSwitch = (Switch) findViewById(R.id.notif_switch);

        Intent receivedIntent = getIntent();
        notifsflag = receivedIntent.getBooleanExtra("notifFlag", false);
        phoneNumber = receivedIntent.getStringExtra("phoneNumber");

        if(notifsflag) { // checks is Notification Switch is ON or OFF and sets the initial value of the toggle
            notifSwitch.setChecked(true);

        }
        else {
            notifSwitch.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

        running = false;
    }

    public void onClickClearData(View view) { //Don't forget to implement this method!
    }

    public void onGenerateGraphClick(View view) { //Don't forget to implement this method!

        /*Graph myDevicesGraph = new Graph();

        int graphSize = 5; // enter the size here

        //initialize some vertices and add them to the graph
        Vertex[] vertices = new Vertex[graphSize];
        Integer[] weights = new Integer[graphSize -1]; // since star graph, we have n-1 edges (n vertices)

        for(int i = 0; i < graphSize; i++){
            vertices[i] = new Vertex("" + i); // instead of i: MacAddress[i]
            myDevicesGraph.addVertex(vertices[i], true);
        }

        // building the edges between the vertices
        for(int i = 1; i < graphSize; i++){ // i=0 is the root vertex
                myDevicesGraph.addEdge(vertices[0], vertices[i], weights[i]);
        }

        //display the initial setup- all vertices adjacent to each other
        for(int i = 0; i < graphSize; i++){
            System.out.println(vertices[i]);

            for(int j = 0; j < vertices[i].getNeighborCount(); j++){
                System.out.println(vertices[i].getNeighbor(j));
            }

            System.out.println();
        }*/

        Intent serverIntent = new Intent(MainActivity.this, Server.class);
        serverIntent.putExtra("MAC", deviceP2pMac);
        serverIntent.putExtra("phoneNumber", phoneNumber); // attach the phone number to the intent
        serverIntent.putExtra("notif", notifsflag);
        startActivity(serverIntent);
        finish();
    }

    public void onNetworkDetailsClick(View view) {
        Intent networkDetailsIntent = new Intent(MainActivity.this, NetworkDetails.class);
        networkDetailsIntent.putExtra("MacArray", peersMacArrayStr);
        networkDetailsIntent.putExtra("phoneNumber", phoneNumber); // attach the phone number to the intent
        networkDetailsIntent.putExtra("notif", notifsflag);
        startActivity(networkDetailsIntent);
        finish();
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
        connectivityStateIntent.putExtra("phoneNumber", phoneNumber); // attach the phone number to the intent
        connectivityStateIntent.putExtra("notif", notifsflag);
        startActivity(connectivityStateIntent);
        finish();
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
            connectivityStateIntent.putExtra("phoneNumber", phoneNumber); // attach the phone number to the intent
            connectivityStateIntent.putExtra("notif", notifsflag);
            startActivity(connectivityStateIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void openDescriptionNamePopup(String MAC, String defaultDeviceName){

        class myRunnableClass implements Runnable{
            String MAC;
            String defaultDeviceName;
            String DeviceName;

            public myRunnableClass(String MAC, String defaultDeviceName){
                this.MAC = MAC;
                this.defaultDeviceName = defaultDeviceName;
            }

            public String getDeviceName(){
                return DeviceName;
            }

            public void run() {
                final RelativeLayout popupName = (RelativeLayout) findViewById(R.id.popup_name);
                TextView MacAddressValuePopup = (TextView) findViewById(R.id.mac_address_value);
                final EditText popupEditText = (EditText) findViewById(R.id.popup_editText);
                final EditText EditTextPhone = (EditText) findViewById(R.id.editText_phone);
                Button popupOkButton = (Button) findViewById(R.id.popup_ok_button);
                Button popupIgnoreButton = (Button) findViewById(R.id.popup_ignore_button);

                String MacFormatted = MAC.substring(0,2) + ":" + MAC.substring(2,4) + ":" + MAC.substring(4,6) + ":" + MAC.substring(6,8) + ":" +
                        MAC.substring(8,10) + ":" + MAC.substring(10, 12); // puts back the MAC address into a standardized form

                MacAddressValuePopup.setText(MacFormatted);
                popupEditText.setText(defaultDeviceName);

                popupName.setVisibility(View.VISIBLE);

                // OK button click listener
                View.OnClickListener onOKClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        DeviceNameFromUser = String.valueOf(popupEditText.getText());
                        PhoneNumberFromUser = String.valueOf(EditTextPhone.getText());
                        popupName.setVisibility(View.GONE);
                        EditTextPhone.setText("");
                        popupNameButtonFlag = true;
                    }
                };
                popupOkButton.setOnClickListener(onOKClickListener);

                // Ignore button click listener

                View.OnClickListener onIgnoreClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        DeviceNameFromUser = defaultDeviceName;
                        PhoneNumberFromUser = "";
                        popupName.setVisibility(View.GONE);
                        popupNameButtonFlag = true;
                    }
                };
                popupIgnoreButton.setOnClickListener(onIgnoreClickListener);

            } // end run
        } // end class




        myRunnableClass myUIRunnable = new myRunnableClass(MAC, defaultDeviceName);
        runOnUiThread(myUIRunnable);
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

    @Override
    protected void onStop() {
        super.onStop();

        running = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        running = false;
        running2 = false;
    }

    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    String convertMacAddress(String wifiMac){

        String n = Integer.toHexString(wifiMac.charAt(1) +2);

        String p2pMac = wifiMac.substring(0,1) + n.substring(1).toUpperCase() + wifiMac.substring(2);

        return  p2pMac;

    }
}