package com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Models.WifiConnection;
import com.example.martinjmartinez.proyectofinal.R;

import java.util.ArrayList;

public class WifiListAdapter extends ArrayAdapter<WifiConnection> {

    public WifiListAdapter(Context context, ArrayList<WifiConnection> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WifiConnection wifiConnection = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_item_list, parent, false);
        }
        // Lookup view for data population
        TextView ssid =  convertView.findViewById(R.id.wifi_name);
        TextView signal = convertView.findViewById(R.id.wifi_signal);
        // Populate the data into the template view using the data object
        ssid.setText(wifiConnection.getSSID());
        signal.setText(wifiConnection.getSignal() + "  ");
        // Return the completed view to render on screen
        return convertView;
    }
}
