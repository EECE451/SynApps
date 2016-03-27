package com.example.saadallah.synapps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Connectivity_State extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity__state);

        Intent receivedIntent = getIntent();
        boolean bt_state_value = receivedIntent.getBooleanExtra("bluetooth_state", false);
        boolean wifi_state_value = receivedIntent.getBooleanExtra("wifi_state", false);
        int cell_network_type = receivedIntent.getIntExtra("network_type",0);

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

//        Intent intent = getIntent();
//        String mystring = intent.getStringArrayExtra("MacArray")[0];
//        cell_value_textview.setText(mystring);


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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
