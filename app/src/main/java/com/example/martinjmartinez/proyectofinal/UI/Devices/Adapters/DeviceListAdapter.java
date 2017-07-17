package com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;

import java.util.List;

/**
 * Created by MartinJMartinez on 7/12/2017.
 */

public class DeviceListAdapter extends ArrayAdapter<Device> {

    public DeviceListAdapter(Context context, int resource, List<Device> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.device_list_item, null);
        }

        Device device = getItem(position);

        if (device != null) {
            TextView name = (TextView) view.findViewById(R.id.device_name);
            TextView space = (TextView) view.findViewById(R.id.device_space);
            TextView power = (TextView) view.findViewById(R.id.device_power);
            Switch status = (Switch) view.findViewById(R.id.device_status);


            name.setText(device.getName());
            if (device.getSpace()!=null) {
                space.setText(device.getSpace().getName());
            }
            //power.setText(device.getDescription());
            status.setChecked(device.isStatus());
        }

        return view;
    }

}
