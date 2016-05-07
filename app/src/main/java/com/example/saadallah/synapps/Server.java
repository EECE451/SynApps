package com.example.saadallah.synapps;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Saadallah on 4/30/2016.
 */

public class Server extends AppCompatActivity implements View.OnClickListener {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    // date pickers
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;

    DatabaseHelper myDb = new DatabaseHelper(this);
    private String MACMasterDevice = "";
    TextView textViewdisplaydata;
    com.android.volley.RequestQueue requestQueue;

    //   String insertUrl = "http://192.168.16.4:80/DevicesServer/insertEntry.php";  //in order to contain the url for our php files
    //   String showUrl = "http://192.168.16.4:80/DevicesServer/displayEntry.php";

//    String insertUrl = "http://192.168.210.1:80/DevicesServer/insertEntry.php";
//    String showUrl = "http://192.168.210.1:80/DevicesServer/displayEntry.php";
//    String showspecificUrl = "http://192.168.210.1:80/DevicesServer/displayspecific2.php";
//    String showSpecificUrl2 = "http://192.168.210.1:80/DevicesServer/displayspecific4.php";
//    String fetchnumberurl =   "http://192.168.210.1:80/DevicesServer/fetchnumber.php";

    String insertUrl = "http://192.168.36.1:80/DevicesServer/insertEntry.php";
    String showUrl = "http://192.168.36.1:80/DevicesServer/displayEntry.php";
    String showspecificUrl = "http://192.36.210.1:80/DevicesServer/displayspecific2.php";
    String showSpecificUrl2 = "http://192.168.36.1:80/DevicesServer/displayspecific4.php";
    String fetchnumberurl =   "http://192.168.36.1:80/DevicesServer/fetchnumber.php";


    // String insertUrl = "http://10.168.46.13:80/DevicesServer/insertEntry.php";
    // String showUrl = "http://10.168.46.13:80/DevicesServer/displayEntry.php";


    WifiManager wifiManager;

    // Bluetooth stuff
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //Cellular Network
    TelephonyManager teleMan;
    String phoneNumber;

    // Notifs SMS
    boolean notifsflag = false;

    // Location
    double Longitude;   // to send to server
    double Latitude;    // to send to server

    // Battery
    int batteryPct;     // to send to server

    // Data Display
    Spinner displayBySpinner;

    // Date Display
    EditText fromDayEdittext;
    EditText toDayEdittext;
    EditText fromTimeEdittext;
    EditText toTimeEdittext;
    SimpleDateFormat dateFormatter;
    SimpleDateFormat timeFormatter;

    Button generateButton;
    Button generateButton2;
    Button generateButton3;

    EditText batteryFrom;
    EditText batteryTo;

    EditText location_radius_edittext;

    Spinner mac_spinner;


    // time
    int year1, year2, month1, month2, day1, day2, hour1, hour2, minute1, minute2;

    //Battery
    int batt_from_level, batt_to_level;

    // Distance
    int distance_radius;

     //MAC
    int selectedMacPosition;

    // Arrays
    String[] slaveMACArray, slaveNamesArray, masterMACArray, phoneNumbersArray, notifFlagArray, LatArray, LongArray, frequencyArray, lastDetectionArray, cummulativeDetectionArray;
    int arraySize;
    String[] myAllDeviceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textViewdisplaydata =(TextView)findViewById(R.id.textViewdisplaydata);
        displayBySpinner = (Spinner) findViewById(R.id.display_by_spinner);


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

        //Cellular Network
        teleMan =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //phoneNumber = teleMan.getLine1Number(); // get the phone number

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

        Intent receivedIntent = getIntent();

        // Notification Switch
        Switch notifSwitch = (Switch) findViewById(R.id.notif_switch);

        notifsflag = receivedIntent.getBooleanExtra("notif", notifsflag); // setting initial value

        if(notifsflag) { // checks is Notification Switch is ON or OFF and sets the initial value of the toggle
            notifSwitch.setChecked(true);

        }
        else {
            notifSwitch.setChecked(false);
        }

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // switches notif
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // Enable/Disable notifs when switch event
                if (isChecked) {
                    notifsflag = true;

                } else {
                    notifsflag = false;

                }
            }
        });

        phoneNumber = receivedIntent.getStringExtra("phoneNumber"); // sets phone number

        if (phoneNumber == null)
            phoneNumber = "";

        Log.d("phone and flag", phoneNumber);
        Log.d("phone and flag", String.valueOf(notifsflag));

        //Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            final String[] INITIAL_PERMS={
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

            requestPermissions(INITIAL_PERMS, 1337);
        }
        GPSTracker myLocationListener = new GPSTracker(this);
        Location myLocation = myLocationListener.getLocation();

        if (myLocation != null) {
            Log.d("location", "Longitude=" + myLocation.getLongitude());
            Log.d("location", "Latitude=" + myLocation.getLatitude());
            Longitude = myLocation.getLongitude();
            Latitude = myLocation.getLatitude();

        } else {
            Log.d("location", "No location available");
            Longitude = -1;
            Latitude = -1;
        }

        // Battery Level
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        batteryPct = (int) (100*(batteryLevel / (double) batteryScale));
        Log.d("BatteryPct", String.valueOf(batteryPct));

        // Views
        fromDayEdittext = (EditText) findViewById(R.id.date1_edittext);
        toDayEdittext = (EditText) findViewById(R.id.date2_edittext);
        fromTimeEdittext = (EditText) findViewById(R.id.time1_edittext);
        toTimeEdittext = (EditText) findViewById(R.id.time2_edittext);
        generateButton = (Button) findViewById(R.id.generate_graph_button);
        batteryFrom = (EditText) findViewById(R.id.battery_from_edittext);
        batteryTo = (EditText) findViewById(R.id.battery_to_edittext);
        generateButton2 = (Button) findViewById(R.id.generate_button_2);
        location_radius_edittext = (EditText) findViewById(R.id.location_radius_edittext);
        generateButton3 = (Button) findViewById(R.id.generate3_button);
        mac_spinner = (Spinner) findViewById(R.id.mac_spinner);

        fromDayEdittext.setInputType(InputType.TYPE_NULL);
        toDayEdittext.setInputType(InputType.TYPE_NULL);
        fromTimeEdittext.setInputType(InputType.TYPE_NULL);
        toTimeEdittext.setInputType(InputType.TYPE_NULL);



        ///////////////



        // Jad's Volley stuff-----------------------------------------------------------------------------------------------------



        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // MAC address of the device reunning the app
        Intent intent = getIntent();
        String deviceMAC = intent.getStringExtra("MAC");
        String[] macAddressParts = deviceMAC.split(":");
        MACMasterDevice = macAddressParts[0] + macAddressParts[1] + macAddressParts[2] + macAddressParts[3] + macAddressParts[4] + macAddressParts[5];

        // show all logic

        Result = "";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, new Response.Listener<JSONObject>()
        {
            public void onResponse(JSONObject response)
            {
                try {
                    JSONArray posts = response.getJSONArray("devices");

                    arraySize = posts.length();
                    slaveMACArray = new String[posts.length()];
                    slaveNamesArray= new String[posts.length()];
                    masterMACArray= new String[posts.length()];
                    phoneNumbersArray= new String[posts.length()];
                    notifFlagArray= new String[posts.length()];
                    LatArray= new String[posts.length()];
                    LongArray= new String[posts.length()];
                    frequencyArray= new String[posts.length()];
                    lastDetectionArray= new String[posts.length()];
                    cummulativeDetectionArray = new String[posts.length()];

                    for (int i = 0; i < posts.length();i++) {

                        JSONObject post = posts.getJSONObject(i);

                        String MACConnected = post.getString("MACConnected");
                        String MACMasterDevice = post.getString("MACMasterDevice");
                        String DeviceName = post.getString("DeviceName");
                        String DateLastDetection = post.getString("DateLastDetection");
                        String FrequencyDetection = post.getString("FrequencyDetection");
                        String CumulativeDetection = post.getString("CumulativeDetection");
                        String phoneNumber = post.getString("MasterName");  //Bassel use this for phone Number
                        String flag = post.getString("flag");   // Bassel use this flag for sms
                        String longitude = post.getString("longitude");     // longitude fetched from server tied to MAC Master
                        String latitude = post.getString("latitude");       // latitude fetched from server tied to MAC Master


                        Result = Result + "\n"+ MACConnected+ " " + DeviceName + " " + DateLastDetection+" "+FrequencyDetection+" "+ CumulativeDetection+" " +
                                MACMasterDevice+" "+phoneNumber+" "+flag+" "+longitude+" "+latitude+" ";
                        String result2 = Result;
                        //textViewdisplaydata.setText(Result);

                        slaveMACArray[i] = MACConnected;
                        slaveNamesArray[i] = DeviceName;
                        masterMACArray[i] = MACMasterDevice;
                        phoneNumbersArray[i] = phoneNumber;
                        notifFlagArray[i] = flag;
                        LatArray[i] = latitude;
                        LongArray[i] = longitude;
                        frequencyArray[i] = FrequencyDetection;
                        lastDetectionArray[i] = DateLastDetection;
                        cummulativeDetectionArray[i] = CumulativeDetection;

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener()
        {
            public void onErrorResponse(VolleyError error)
            {

            }
        });
        requestQueue.add(jsonObjectRequest);



        // Spinner -----------------------------------------------------------------------------------------------
        // Spinner Logic (Adapter)

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("HH:mm a");

        fromDayEdittext.setOnClickListener(this);
        toDayEdittext.setOnClickListener(this);
        fromTimeEdittext.setOnClickListener(this);
        toTimeEdittext.setOnClickListener(this);

        fromDayEdittext.setVisibility(View.GONE);
        toDayEdittext.setVisibility(View.GONE);
        fromTimeEdittext.setVisibility(View.GONE);
        toTimeEdittext.setVisibility(View.GONE);
        generateButton.setVisibility(View.GONE);
        generateButton2.setVisibility(View.GONE);
        location_radius_edittext.setVisibility(View.GONE);
        generateButton3.setVisibility(View.GONE);
        mac_spinner.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_display_choice, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        displayBySpinner.setAdapter(spinnerAdapter);

         final TextView graphDescription = (TextView) findViewById(R.id.graph_description);

        displayBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                switch (pos) {
                    case 0: // All
                        graphDescription.setText("Displays all the devices connected");

                        batteryFrom.setVisibility(View.GONE);
                        batteryTo.setVisibility(View.GONE);
                        fromDayEdittext.setVisibility(View.GONE);
                        toDayEdittext.setVisibility(View.GONE);
                        fromTimeEdittext.setVisibility(View.GONE);
                        toTimeEdittext.setVisibility(View.GONE);
                        generateButton.setVisibility(View.GONE);
                        generateButton2.setVisibility(View.GONE);
                        location_radius_edittext.setVisibility(View.GONE);
                        generateButton3.setVisibility(View.GONE);
                        mac_spinner.setVisibility(View.GONE);

                        textViewdisplaydata.setText("");

                        // Graph algorithm here
                        textViewdisplaydata.setText(generateGraph(masterMACArray, slaveNamesArray, frequencyArray, cummulativeDetectionArray, arraySize));
                        break;

                    case 1: // Time Range
                        graphDescription.setText("Displays all the devices connected between");

                        textViewdisplaydata.setText("");

                        batteryFrom.setVisibility(View.GONE);
                        batteryTo.setVisibility(View.GONE);
                        generateButton2.setVisibility(View.GONE);
                        location_radius_edittext.setVisibility(View.GONE);
                        generateButton3.setVisibility(View.GONE);
                        mac_spinner.setVisibility(View.GONE);

                        fromDayEdittext.setVisibility(View.VISIBLE);
                        toDayEdittext.setVisibility(View.VISIBLE);
                        fromTimeEdittext.setVisibility(View.VISIBLE);
                        toTimeEdittext.setVisibility(View.VISIBLE);
                        generateButton.setVisibility(View.VISIBLE);

                        Calendar newCalendar = Calendar.getInstance();

                        // INITIALIZING VALUES
                        year1 = newCalendar.get(Calendar.YEAR);
                        month1 = newCalendar.get(Calendar.MONTH);
                        day1 = newCalendar.get(Calendar.DAY_OF_MONTH);
                        hour1 = newCalendar.get(Calendar.HOUR_OF_DAY);
                        minute1 = newCalendar.get(Calendar.MINUTE);

                        year2 = newCalendar.get(Calendar.YEAR);
                        month2 = newCalendar.get(Calendar.MONTH);
                        day2 = newCalendar.get(Calendar.DAY_OF_MONTH);
                        hour2 = newCalendar.get(Calendar.HOUR_OF_DAY);
                        minute2 = newCalendar.get(Calendar.MINUTE);

                        fromDatePickerDialog = new DatePickerDialog(Server.this, new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar date1 = Calendar.getInstance();
                                date1.set(year, monthOfYear, dayOfMonth);
                                year1 = year; month1 = monthOfYear; day1 = dayOfMonth;
                                fromDayEdittext.setText(dateFormatter.format(date1.getTime()));
                            }

                        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                        toDatePickerDialog = new DatePickerDialog(Server.this, new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar date2 = Calendar.getInstance();
                                date2.set(year, monthOfYear, dayOfMonth);
                                year2 = year; month2 = monthOfYear; day2 = dayOfMonth;

                                toDayEdittext.setText(dateFormatter.format(date2.getTime()));
                            }

                        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                        fromTimePickerDialog = new TimePickerDialog(Server.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour1 = hourOfDay;
                                minute1 = minute;

                                fromTimeEdittext.setText(hourOfDay + ":" + minute);


                            }
                        },hour1,minute1, true);

                        toTimePickerDialog = new TimePickerDialog(Server.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour2 = hourOfDay;
                                minute2 = minute;

                                toTimeEdittext.setText(hourOfDay + ":" + minute);

                            }
                        },hour2,minute2,true);

                        // THE REST IS IN ON CLICK GENERATE

                        break;

                    case 2: // By MAC
                        graphDescription.setText("Displays all the devices that connected to a specific one");

                        textViewdisplaydata.setText("");

                        batteryFrom.setVisibility(View.GONE);
                        batteryTo.setVisibility(View.GONE);
                        fromDayEdittext.setVisibility(View.GONE);
                        toDayEdittext.setVisibility(View.GONE);
                        fromTimeEdittext.setVisibility(View.GONE);
                        toTimeEdittext.setVisibility(View.GONE);
                        generateButton.setVisibility(View.GONE);
                        generateButton2.setVisibility(View.GONE);
                        location_radius_edittext.setVisibility(View.GONE);
                        generateButton3.setVisibility(View.GONE);

                        mac_spinner.setVisibility(View.VISIBLE);

                        final ArrayList<String> allDevicesList = new ArrayList<String>();

                        for (int i=0; i<arraySize; i++){

                            if (isMacinList2(allDevicesList, masterMACArray[i]) == -1)
                                allDevicesList.add(0, masterMACArray[i]);
                        }

                        for (int i=0; i<arraySize; i++){

                            if (isMacinList2(allDevicesList,slaveMACArray[i]) == -1){
                                allDevicesList.add(0, slaveNamesArray[i]);
                            }
                        }

                        myAllDeviceList = new String[allDevicesList.size()];
                        myAllDeviceList = allDevicesList.toArray(myAllDeviceList);

                        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(Server.this, android.R.layout.simple_spinner_item, myAllDeviceList);
                        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mac_spinner.setAdapter(spinnerAdapter2);

                        mac_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                selectedMacPosition = pos;
                                onMacPositionSelection(myAllDeviceList[pos]); // IMPLEMENT THE METHOD!
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;

                     case 3: // LOCATION
                         graphDescription.setText("Displays all the devices that were detected within a certain distance radius from the current location");

                         textViewdisplaydata.setText("");

                         batteryFrom.setVisibility(View.GONE);
                         batteryTo.setVisibility(View.GONE);
                         fromDayEdittext.setVisibility(View.GONE);
                         toDayEdittext.setVisibility(View.GONE);
                         fromTimeEdittext.setVisibility(View.GONE);
                         toTimeEdittext.setVisibility(View.GONE);
                         generateButton.setVisibility(View.GONE);
                         generateButton2.setVisibility(View.GONE);
                         mac_spinner.setVisibility(View.GONE);

                         location_radius_edittext.setVisibility(View.VISIBLE);
                         generateButton3.setVisibility(View.VISIBLE);

                         // THE REST IS IN ON CLICK GENERATE

                         break;

                    case 4: //BATTERY LIFE
                        graphDescription.setText("Displays all the devices having a battery life between");

                        textViewdisplaydata.setText("");

                        fromDayEdittext.setVisibility(View.GONE);
                        toDayEdittext.setVisibility(View.GONE);
                        fromTimeEdittext.setVisibility(View.GONE);
                        toTimeEdittext.setVisibility(View.GONE);
                        generateButton.setVisibility(View.GONE);
                        location_radius_edittext.setVisibility(View.GONE);
                        generateButton3.setVisibility(View.GONE);
                        mac_spinner.setVisibility(View.GONE);

                        batteryFrom.setVisibility(View.VISIBLE);
                        batteryTo.setVisibility(View.VISIBLE);
                        generateButton2.setVisibility(View.VISIBLE);

                        // THE REST IS IN ON CLICK GENERATE

                        break;
                 }


            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    } // end onCreate



    String Result = "";

    public void onbtnclickinsertall(final View view) {


        Cursor result_NumberofDevices = myDb.getNumberofDevices();
        String result_NumberofDevices_String = "";
        String MACConnected = "";
        String DeviceName = "";
        String DateLastDetection = "";
        String FrequencyDetection = "";
        String CumulativeDetection = "";



        if (result_NumberofDevices != null && result_NumberofDevices.getCount() > 0) {
            result_NumberofDevices.moveToFirst();
            result_NumberofDevices_String = result_NumberofDevices.getString(0);
        }
        int result_NumberofDevices_String_int = Integer.parseInt(result_NumberofDevices_String);

        for (int i = 1; i <= result_NumberofDevices_String_int; i++) {
            Log.d("Loop Flag", String.valueOf(i));

            Cursor result_getalldata = myDb.getAllDatabyDid(i);

            while (result_getalldata.moveToNext()) {
                MACConnected = result_getalldata.getString(0);
                DeviceName = result_getalldata.getString(1);
                DateLastDetection = result_getalldata.getString(2);
                FrequencyDetection = result_getalldata.getString(3);
                CumulativeDetection = result_getalldata.getString(4);



            }
            final String finalDateLastDetection = DateLastDetection;
            final String finalDeviceName = DeviceName;
            final String finalMACConnected = MACConnected;
            final String finalFrequencyDetection = FrequencyDetection;
            final String finalCumulativeDetection = CumulativeDetection;


            StringRequest request = new StringRequest(Request.Method.POST, showspecificUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        System.out.println(response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        if (Integer.parseInt(jsonObject.getString("exists")) == 1) {
                            Log.d("batata", "batata");
                            onbtnspecific(finalMACConnected,finalDeviceName,finalDateLastDetection,finalFrequencyDetection,finalCumulativeDetection);
                        } else {
                            Log.d("batata", "Mech batata");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("MACConnected", finalMACConnected);
                    parameters.put("MACMasterDevice", MACMasterDevice);
                    parameters.put("DeviceName", finalDeviceName);
                    parameters.put("DateLastDetection", finalDateLastDetection);
                    parameters.put("FrequencyDetection", finalFrequencyDetection);
                    parameters.put("CumulativeDetection", finalCumulativeDetection);
                    parameters.put("MasterName", phoneNumber);
                    parameters.put("flag", String.valueOf(notifsflag));
                    parameters.put("longitude", String.valueOf(Longitude));
                    parameters.put("latitude", String.valueOf(Latitude));


                    return parameters;
                }
            };

            requestQueue.add(request);
        }

        for (int i =0; i<arraySize; i++){
            Log.d("phonenbavailable", phoneNumbersArray[i]);
            Log.d("phonenbavailable", notifFlagArray[i]);
        }
    }

    public void onbtnspecific(final String s1, final String s2, final String s3, final String s4, final String s5) {


        StringRequest request = new StringRequest(Request.Method.POST, showSpecificUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.names().get(0).equals("exists")) {

                        if(Integer.parseInt(jsonObject.getString("exists")) == 1)
                        {
                            Log.d("batata","batata22");
                        }
                        else
                        {
                            Log.d("batata","Mech batata23");

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters  = new HashMap<String, String>();
                parameters.put("MACConnected",s1);
                parameters.put("MACMasterDevice",MACMasterDevice);
                parameters.put("DeviceName",s2);
                parameters.put("DateLastDetection",s3);
                parameters.put("FrequencyDetection",s4);
                parameters.put("CumulativeDetection", s5);
                parameters.put("MasterName", phoneNumber);
                parameters.put("flag", String.valueOf(notifsflag));
                parameters.put("longitude", String.valueOf(Longitude));
                parameters.put("latitude", String.valueOf(Latitude));


                return parameters;
            }
        };

        requestQueue.add(request);

    }

    public void fetchnumber()
    {
        StringRequest request = new StringRequest(Request.Method.POST, fetchnumberurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    System.out.println(response.toString());
                    JSONObject jsonObject = new JSONObject(response);

                    Log.d("number",jsonObject.getString("exists")); // Numbers are given here: total number connected to my Master MAC
                    Log.d("number",jsonObject.getString("exists2")); // Total Number of MACs

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters  = new HashMap<String, String>();
                parameters.put("MACMasterDevice",MACMasterDevice);


                return parameters;
            }
        };

        requestQueue.add(request);
    }

    //Settings option already implemented
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.openDrawer(Gravity.LEFT);
        }
        else{
            drawer.closeDrawer(Gravity.LEFT);
        }

        int id = item.getItemId();

        if (id == R.id.action_settings){
            if (!drawer.isDrawerOpen(Gravity.LEFT)){
                drawer.openDrawer(Gravity.LEFT);
            }
            else{
                drawer.closeDrawer(Gravity.LEFT);
            }
        }

        if (id == R.id.action_connectivity_state) {

            Intent connectivityStateIntent = new Intent(Server.this, Connectivity_State.class);
            connectivityStateIntent.putExtra("bluetooth_state", mBluetoothAdapter.isEnabled());
            connectivityStateIntent.putExtra("wifi_state", wifiManager.isWifiEnabled());
            connectivityStateIntent.putExtra("network_type", teleMan.getNetworkType());
            connectivityStateIntent.putExtra("phoneNumber", phoneNumber); // attach the phone number to the intent
            connectivityStateIntent.putExtra("notif", notifsflag);
            connectivityStateIntent.putExtra("Longitude", Longitude);
            connectivityStateIntent.putExtra("Latitude", Latitude);
            connectivityStateIntent.putExtra("battery", batteryPct);
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

    @Override
    public void onBackPressed() {

        Intent mainActIntent = new Intent(Server.this, MainActivity.class);
        mainActIntent.putExtra("notifFlag", notifsflag);
        mainActIntent.putExtra("phoneNumber", phoneNumber); // resend the same that was received

        startActivity(mainActIntent);
        finish();

    }

    @Override
    public void onClick(View v) {
        if(v == fromDayEdittext) {
            fromDatePickerDialog.show();
        } else if(v == toDayEdittext) {
            toDatePickerDialog.show();
        } else if(v == fromTimeEdittext) {
            fromTimePickerDialog.show();
        } else if(v == toTimeEdittext) {
            toTimePickerDialog.show();
        }
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public void onClickGenerate(View view) {

        Calendar date1 = Calendar.getInstance();
        date1.set(year1,month1,day1, hour1, minute1, 0);
        long milli1 = date1.getTimeInMillis();

        Calendar date2 = Calendar.getInstance();
        date1.set(year2,month2,day2, hour2, minute2, 0);
        long milli2 = date2.getTimeInMillis();

        ArrayList<String> slaveList = new ArrayList<>();
        ArrayList<String> masterList = new ArrayList<>();
        ArrayList<String> frequenyList = new ArrayList<>();
        ArrayList<String> CummulativeList = new ArrayList<>();

        for (int i=0; i<arraySize; i++){

            if(Long.parseLong(lastDetectionArray[i]) >= milli1 && Long.parseLong(lastDetectionArray[i]) <= milli2){
                slaveList.add(0, slaveNamesArray[i]);
                masterList.add(0, masterMACArray[i]);
                frequenyList.add(0, frequencyArray[i]);
                CummulativeList.add(0, cummulativeDetectionArray[i]);
            }
        }

        String[] mySlaveList = new String[slaveList.size()];
        String[] myMasterList = new String[masterList.size()];
        String[] myFrequencyList = new String[frequenyList.size()];
        String[] myCummulativeList = new String[CummulativeList.size()];

        mySlaveList = slaveList.toArray(mySlaveList);
        myMasterList = masterList.toArray(myMasterList);
        myFrequencyList = frequenyList.toArray(myFrequencyList);
        myCummulativeList = CummulativeList.toArray(myCummulativeList);

        textViewdisplaydata.setText(generateGraph(myMasterList, mySlaveList, myFrequencyList, myCummulativeList, slaveList.size()));

    }

    public void onClickGenerate2(View view) {
        batt_from_level = Integer.valueOf(String.valueOf(batteryFrom.getText()));
        batt_to_level = Integer.valueOf(String.valueOf(batteryTo.getText()));
    }

    public void onClickGenerate3(View view) {


        distance_radius = Integer.valueOf(String.valueOf(location_radius_edittext.getText()));

        ArrayList<String> slaveList = new ArrayList<>();
        ArrayList<String> masterList = new ArrayList<>();
        ArrayList<String> frequenyList = new ArrayList<>();
        ArrayList<String> CummulativeList = new ArrayList<>();

        for (int i=0; i<arraySize; i++){

            float arrayLongitude = Float.parseFloat(LongArray[i]);
            float arrayLatitude =Float.parseFloat(LatArray[i]);

            float deviceDistance = distFrom((float) Latitude, (float) Longitude, arrayLatitude, arrayLongitude);

            if(deviceDistance < distance_radius){
                slaveList.add(0, slaveNamesArray[i]);
                masterList.add(0, masterMACArray[i]);
                frequenyList.add(0, frequencyArray[i]);
                CummulativeList.add(0, cummulativeDetectionArray[i]);
            }
        }

        String[] mySlaveList = new String[slaveList.size()];
        String[] myMasterList = new String[masterList.size()];
        String[] myFrequencyList = new String[frequenyList.size()];
        String[] myCummulativeList = new String[CummulativeList.size()];

        mySlaveList = slaveList.toArray(mySlaveList);
        myMasterList = masterList.toArray(myMasterList);
        myFrequencyList = frequenyList.toArray(myFrequencyList);
        myCummulativeList = CummulativeList.toArray(myCummulativeList);

        textViewdisplaydata.setText(generateGraph(myMasterList, mySlaveList, myFrequencyList, myCummulativeList, slaveList.size()));


    }

    private void onMacPositionSelection(String mac){

        ArrayList<String> slaveList = new ArrayList<>();
        ArrayList<String> masterList = new ArrayList<>();
        ArrayList<String> frequenyList = new ArrayList<>();
        ArrayList<String> CummulativeList = new ArrayList<>();

        for (int i=0; i<arraySize; i++){

            if(masterMACArray[i].compareTo(mac) == 0 || slaveNamesArray[i].compareTo(mac) == 0){
                slaveList.add(0, slaveNamesArray[i]);
                masterList.add(0, masterMACArray[i]);
                frequenyList.add(0, frequencyArray[i]);
                CummulativeList.add(0, cummulativeDetectionArray[i]);
            }
        }

        String[] mySlaveList = new String[slaveList.size()];
        String[] myMasterList = new String[masterList.size()];
        String[] myFrequencyList = new String[frequenyList.size()];
        String[] myCummulativeList = new String[CummulativeList.size()];

        mySlaveList = slaveList.toArray(mySlaveList);
        myMasterList = masterList.toArray(myMasterList);
        myFrequencyList = frequenyList.toArray(myFrequencyList);
        myCummulativeList = CummulativeList.toArray(myCummulativeList);

        textViewdisplaydata.setText(generateGraph(myMasterList, mySlaveList, myFrequencyList, myCummulativeList, slaveList.size()));
    }

    private String generateGraph(String[] macAddressMaster, String[] macAddressSlave, String[] frequency, String[] CumulativeDetection, int size){

        // GRAPH START----------------------------------------------------------------------------
        // DUMMY DATA
//        String[] macAddressMaster = {"MAC1", "MAC2", "MAC3", "MAC1", "MAC1", "MAC4"};
//        String[] macAddressSlave = {"MAC3", "MAC3", "MAC1", "MAC5", "MAC2", "MAC6"};
//        String[] dummyFrequency = {"1","2","3","4","5","6"};
//        String[] dummyCumulativeDetection = {"120000","240000","360000","480000","590000","660000"};

        String output = "";

        Graph myDevicesGraph = new Graph();

        int graphSize = size; // enter the size here

        //initialize some vertices and add them to the graph
        //Edge[] edges = new Edge[graphSize];
        //Integer[] weights = new Integer[graphSize];
        ArrayList<Vertex> vertices = new ArrayList<>();

        for(int i = 0; i < graphSize; i++){

            if (isMacinList(vertices,macAddressMaster[i]) == -1) // if the master device vertex is not in the vertices list, add it at position 0
            {
                vertices.add(0, new Vertex(macAddressMaster[i]));
                myDevicesGraph.addVertex(vertices.get(0), true); // and add it to the graph
            }

            if (isMacinList(vertices,macAddressSlave[i]) == -1) // if the slave device vertex is not in the vertices list, add it at position 0
            {
                vertices.add(0, new Vertex(macAddressSlave[i]));
                myDevicesGraph.addVertex(vertices.get(0), true); // and add it to the graph
            }

            // adds an edge between the two vertices
            myDevicesGraph.addEdge(vertices.get(isMacinList(vertices,macAddressMaster[i])), vertices.get(isMacinList(vertices,macAddressSlave[i])), frequency[i], CumulativeDetection[i]);
        }

        //display the initial setup- all vertices adjacent to each other
        for(int i = 0; i < vertices.size(); i++){
            output = output + vertices.get(i) + "\n";

            for(int j = 0; j < vertices.get(i).getNeighborCount(); j++){
                output = output + vertices.get(i).getNeighbor(j) + "\n";
            }

            output = output + "\n";
        }

        // GRAPH END----------------------------------------------------------------------------

        return output;

    }

    int isMacinList(ArrayList<Vertex> vertexArray, String mac){ // return the index of the vertex of label mac or returns -1 if not in the list

        for (int i=0; i< vertexArray.size(); i++){
            Log.d("vertexArray", String.valueOf(vertexArray.get(i).getLabel().compareTo(mac)));
            if (vertexArray.get(i).getLabel().compareTo(mac) == 0) {
                return i;
            }
        }

        return -1;
    }

    int isMacinList2(ArrayList<String> stringArray, String mac){ // return the index of the vertex of label mac or returns -1 if not in the list

        for (int i=0; i< stringArray.size(); i++){
            if (stringArray.get(i).compareTo(mac) == 0) {
                return i;
            }
        }

        return -1;
    }
}
