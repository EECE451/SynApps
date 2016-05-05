package com.example.saadallah.synapps;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Saadallah on 5/4/2016.
 */
public class NetworkDetailsAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] device_name_array;
    private String[] mac_address_array;
    private String[] phone_number_array;
    private String[] detection_frequency_array;
    private String[] cumulative_time_array;
    private String[] last_time_detected_array;
    private String[] last_detection_duration_array;

    public NetworkDetailsAdapter(Activity context, String[] device_name_array, String[] mac_address_array, String[] phone_number_array, String[] detection_frequency_array, String[] cumulative_time_array, String[] last_time_detected_array, String[] last_detection_duration_array) {
        super(context, R.layout.device_row, device_name_array);
        this.context = context;
        this.device_name_array = device_name_array;
        this.mac_address_array = mac_address_array;
        this.phone_number_array = phone_number_array;
        this.detection_frequency_array = detection_frequency_array;
        this.cumulative_time_array = cumulative_time_array;
        this.last_time_detected_array = last_time_detected_array;
        this.last_detection_duration_array = last_detection_duration_array;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.device_row, null, true);

        TextView device_name = (TextView) rowView.findViewById(R.id.deviceName_value);
        TextView mac_address = (TextView) rowView.findViewById(R.id.mac_address_value);
        TextView phone_number = (TextView) rowView.findViewById(R.id.phone_number_value);
        TextView detection_fq = (TextView) rowView.findViewById(R.id.detection_fq_value);
        TextView cumulative_time = (TextView) rowView.findViewById(R.id.cumulative_time_value);
        TextView last_time_detection = (TextView) rowView.findViewById(R.id.last_time_detection_value);
        TextView last_time_range = (TextView) rowView.findViewById(R.id.last_time_range_value);


        device_name.setText(device_name_array[position]);
        mac_address.setText(mac_address_array[position]);
        phone_number.setText(phone_number_array[position]);
        detection_fq.setText(detection_frequency_array[position]);
        cumulative_time.setText(cumulative_time_array[position]);
        last_time_detection.setText(last_time_detected_array[position]);
        last_time_range.setText(last_detection_duration_array[position]);

        return rowView;
    }
}
