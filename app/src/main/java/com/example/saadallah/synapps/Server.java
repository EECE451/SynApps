package com.example.saadallah.synapps;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saadallah on 4/30/2016.
 */

public class Server extends AppCompatActivity {

    private android.support.v7.app.ActionBar bar; //ActionBar-Drawer
    private ActionBarDrawerToggle toggle; //ActionBar-Drawer
    private DrawerLayout drawer; //ActionBar-Drawer

    DatabaseHelper myDb = new DatabaseHelper(this);
    private String MACMasterDevice = "";
    TextView textViewdisplaydata;
    com.android.volley.RequestQueue requestQueue;

    //   String insertUrl = "http://192.168.16.4:80/DevicesServer/insertEntry.php";  //in order to contain the url for our php files
    //   String showUrl = "http://192.168.16.4:80/DevicesServer/displayEntry.php";

    String insertUrl = "http://192.168.210.1:80/DevicesServer/insertEntry.php";
    String showUrl = "http://192.168.210.1:80/DevicesServer/displayEntry.php";
    String showspecificUrl = "http://192.168.210.1:80/DevicesServer/displayspecific2.php";
    String showSpecificUrl2 = "http://192.168.210.1:80/DevicesServer/displayspecific4.php";
    String fetchnumberurl =   "http://192.168.210.1:80/DevicesServer/fetchnumber.php";



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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textViewdisplaydata =(TextView)findViewById(R.id.textViewdisplaydata);


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

        Log.d("phone and flag", phoneNumber);
        Log.d("phone and flag", String.valueOf(notifsflag));

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
                    for (int i = 0; i < posts.length();i++) {

                        JSONObject post = posts.getJSONObject(i);

                        String MACConnected = post.getString("MACConnected");
                        String MACMasterDevice = post.getString("MACMasterDevice");
                        String DeviceName = post.getString("DeviceName");
                        String DateLastDetection = post.getString("DateLastDetection");
                        String FrequencyDetection = post.getString("FrequencyDetection");
                        String CumulativeDetection = post.getString("CumulativeDetection");
                        String MasterName = post.getString("MasterName");


                        Result = Result + "\n"+ MACConnected+ " " + DeviceName + " " + DateLastDetection+" "+FrequencyDetection+" "+ CumulativeDetection+" " + MACMasterDevice+" "+MasterName+" ";
                        String result2 = Result;
                        textViewdisplaydata.setText(Result);

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
    }
    String Result = "";
    String Result2 = "";

//    public void onbtnclickshowall(View view) {
//
//    }

//    public void onbtnclickinsertall(View view) {
//
//        Cursor result_NumberofDevices = myDb.getNumberofDevices();
//        String result_NumberofDevices_String = "";
//        String MACConnected ="";
//        String DeviceName = "";
//        String DateLastDetection = "";
//        String FrequencyDetection = "";
//        String CumulativeDetection = "";
//
//
//        if (result_NumberofDevices != null &&result_NumberofDevices.getCount() > 0) {
//            result_NumberofDevices.moveToFirst();
//            result_NumberofDevices_String = result_NumberofDevices.getString(0);
//        }
//        int result_NumberofDevices_String_int = Integer.parseInt(result_NumberofDevices_String);
//
//        for(int i=1;i<=result_NumberofDevices_String_int;i++)
//        {   Log.d("Loop Flag", String.valueOf(i));
//
//            Cursor result_getalldata = myDb.getAllDatabyDid(i);
//
//            while (result_getalldata.moveToNext()) {
//                MACConnected = result_getalldata.getString(0);
//                DeviceName = result_getalldata.getString(1);
//                DateLastDetection = result_getalldata.getString(2);
//                FrequencyDetection = result_getalldata.getString(3);
//                CumulativeDetection = result_getalldata.getString(4);
//
//            }
//            final String finalDateLastDetection = DateLastDetection;
//            final String finalDeviceName = DeviceName;
//            final String finalMACConnected = MACConnected;
//            final String finalFrequencyDetection = FrequencyDetection;
//            final String finalCumulativeDetection = CumulativeDetection;
//
//            StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//
//                    System.out.println(response.toString());
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }) {
//
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String,String> parameters  = new HashMap<String, String>();
//                    parameters.put("MACConnected", finalMACConnected);
//                    parameters.put("MACMasterDevice",MACMasterDevice);
//                    parameters.put("DeviceName", finalDeviceName);
//                    parameters.put("DateLastDetection", finalDateLastDetection);
//                    parameters.put("FrequencyDetection", finalFrequencyDetection);
//                    parameters.put("CumulativeDetection", finalCumulativeDetection);
//                    parameters.put("MasterName","");
//
//
//                    return parameters;
//                }
//            };
//            requestQueue.add(request);
//        }
//
//
//
//
//    }



    public void onbtnclickinsertall(final View view) {


        Cursor result_NumberofDevices = myDb.getNumberofDevices();
        String result_NumberofDevices_String = "";
        String MACConnected = "";
        String DeviceName = "";
        String DateLastDetection = "";
        String FrequencyDetection = "";
        String CumulativeDetection = "";
        String phonenumber = "";


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
                phonenumber = result_getalldata.getString(5);


            }
            final String finalDateLastDetection = DateLastDetection;
            final String finalDeviceName = DeviceName;
            final String finalMACConnected = MACConnected;
            final String finalFrequencyDetection = FrequencyDetection;
            final String finalCumulativeDetection = CumulativeDetection;
            final String finalPhonenumber = phonenumber;

            StringRequest request = new StringRequest(Request.Method.POST, showspecificUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        System.out.println(response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        if (Integer.parseInt(jsonObject.getString("exists")) == 1) {
                            Log.d("batata", "batata");
                            onbtnspecific(finalMACConnected,finalDeviceName,finalDateLastDetection,finalFrequencyDetection,finalCumulativeDetection, finalPhonenumber);
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
                    parameters.put("MasterName", finalPhonenumber);


                    return parameters;
                }
            };

            requestQueue.add(request);
        }
    }

    public void onbtnspecific(final String s1, final String s2, final String s3, final String s4, final String s5, final String s6) {


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
                parameters.put("CumulativeDetection",s5);
                parameters.put("MasterName","");




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

        if (id == R.id.action_connectivity_state) {

            Intent connectivityStateIntent = new Intent(Server.this, Connectivity_State.class);
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

    @Override
    public void onBackPressed() {

        Intent mainActIntent = new Intent(Server.this, MainActivity.class);
        mainActIntent.putExtra("notifFlag", notifsflag);
        mainActIntent.putExtra("phoneNumber", phoneNumber); // resend the same that was received

        startActivity(mainActIntent);
        finish();

    }


}
