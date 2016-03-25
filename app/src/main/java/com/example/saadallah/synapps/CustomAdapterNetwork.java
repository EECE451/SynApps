package com.example.saadallah.synapps;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Saadallah on 3/21/2016.
 */
public class CustomAdapterNetwork extends ArrayAdapter<String> {

    private Activity context;

    private String[] deviceNames; // or set as input parameter the whole database
    private String[] MACaddresses;
    private String[] phoneNumbers;
    private String[] lastDetected; //can create a new class called Time, may facilitate a lot...
    private String[] lastStarted; //Time
    private String[] lastDuration; // Time
    private String[] numberOfDetections;
    private String[] cummulativeTime; // Time

    public CustomAdapterNetwork(Activity context, String[] deviceNames, String[] MACaddresses,
                                String[] phoneNumbers, String[] lastDetected, String[] lastStarted,
                                String[] lastDuration, String[] numberOfDetections, String[] cummulativeTime) {
        super(context, R.layout.activity_network_details, deviceNames); // last parameter?
        this.context = context;
        this.deviceNames = deviceNames;
        this.MACaddresses = MACaddresses;
        this.phoneNumbers = phoneNumbers;
        this.lastDetected = lastDetected;
        this.lastStarted = lastStarted;
        this.lastDuration = lastDuration;
        this.numberOfDetections = numberOfDetections;
        this.cummulativeTime = cummulativeTime;
    }
/*
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.network_details_list_row, null, true);

        TextView deviceName = (TextView) rowView.findViewById(R.id.deviceName);
        TextView MACaddress_val = (TextView) rowView.findViewById(R.id.mac_address_val);
        TextView phone_number_val = (TextView) rowView.findViewById(R.id.phone_number_val);
        TextView last_time_detected_val = (TextView) rowView.findViewById(R.id.last_time_detected_val);
        TextView last_time_started_val = (TextView) rowView.findViewById(R.id.last_time_started_val);
        TextView last_duration_val = (TextView) rowView.findViewById(R.id.last_duration_val);
        TextView number_times_detected_val = (TextView) rowView.findViewById(R.id.number_times_detected_val);
        TextView cummulative_detection_val = (TextView) rowView.findViewById(R.id.cummulative_detection_val);

        deviceName.setText(deviceNames[position]);
        MACaddress_val.setText(MACaddresses[position]);
        phone_number_val.setText(phoneNumbers[position]);
        last_time_detected_val.setText(lastDetected[position]);
        last_time_started_val.setText(lastStarted[position]);
        last_duration_val.setText(lastDuration[position]);
        number_times_detected_val.setText(numberOfDetections[position]);
        cummulative_detection_val.setText(cummulativeTime[position]);

        return rowView;
    }

    */
}