package com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;


import java.util.List;

/**
 * Created by MartinJMartinez on 7/26/2017.
 */

public class SpaceSpinnerAdapter extends ArrayAdapter<Space> {

    public SpaceSpinnerAdapter(Context context, int resource, List<Space> items) {
        super(context, resource, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.spaces_item_spinner, null);
        }

        Space space = getItem(position);

        if (space != null) {
            TextView name = (TextView) view.findViewById(R.id.space_name_spinner_item);
            name.setText(space.getName());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.spaces_item_spinner, null);
        }

        Space space = getItem(position);

        if (space != null) {
            TextView name = (TextView) view.findViewById(R.id.space_name_spinner_item);
            name.setText(space.getName());
        }
        return view;
    }
}
