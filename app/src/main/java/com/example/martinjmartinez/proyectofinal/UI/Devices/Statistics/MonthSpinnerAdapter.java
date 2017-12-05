package com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Models.MonthConsumed;
import com.example.martinjmartinez.proyectofinal.R;

import java.util.List;

public class MonthSpinnerAdapter extends ArrayAdapter<String> {

    public MonthSpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.months_spinner, null);
        }

        String month = getItem(position);

        if (month != null) {
            TextView name =  view.findViewById(R.id.month_name_spinner_item);
            name.setText(month);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.months_spinner, null);
        }

        String month = getItem(position);

        if (month != null) {
            TextView name = view.findViewById(R.id.month_name_spinner_item);
            name.setText(month);
        }
        return view;
    }
}

