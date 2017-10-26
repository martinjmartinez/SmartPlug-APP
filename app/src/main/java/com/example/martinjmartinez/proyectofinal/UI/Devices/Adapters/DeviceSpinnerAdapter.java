package com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;

import java.util.List;

public class DeviceSpinnerAdapter extends ArrayAdapter<Device> {

    public DeviceSpinnerAdapter(Context context, int resource, List<Device> items) {
        super(context, resource, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.devices_item_spinner, null);
        }

        Device device = getItem(position);

        if (device != null) {
            TextView name = (TextView) view.findViewById(R.id.device_name_spinner_item);
            name.setText(device.getName());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.devices_item_spinner, null);
        }

        Device device = getItem(position);

        if (device != null) {
            TextView name = (TextView) view.findViewById(R.id.device_name_spinner_item);
            name.setText(device.getName());
        }
        return view;
    }
}
