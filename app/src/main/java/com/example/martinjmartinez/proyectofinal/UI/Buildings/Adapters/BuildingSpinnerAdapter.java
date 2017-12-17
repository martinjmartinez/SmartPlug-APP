package com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments.BuildingDetailFragment;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.List;


public class BuildingSpinnerAdapter extends ArrayAdapter<Building> {

    public BuildingSpinnerAdapter(Context context, int resource, List<Building> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.building_item_spinner, null);
        }

        Building building = getItem(position);

        if (building != null) {
            TextView name =  view.findViewById(R.id.building_name_spinner_item);
            name.setText(building.getName());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.building_item_spinner, null);
        }

        Building building = getItem(position);

        if (building != null) {
            TextView name =  view.findViewById(R.id.building_name_spinner_item);
            name.setText(building.getName());
        }
        return view;
    }
}
