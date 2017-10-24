package com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments.BuildingDetailFragment;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.List;

import io.realm.Realm;

public class BuildingListAdapter extends ArrayAdapter<Building> {

    private SpaceService spaceService;

    public BuildingListAdapter(Context context, int resource, List<Building> items) {
        super(context, resource, items);

        spaceService = new SpaceService(Realm.getDefaultInstance());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.building_list_item, null);
        }

        Building building = getItem(position);

        if (building != null) {
            ImageView detailsImage = (ImageView) view.findViewById(R.id.building_details);
            TextView name = (TextView) view.findViewById(R.id.building_name);
            TextView spaces = (TextView) view.findViewById(R.id.building_spaces);

            initListener(detailsImage, building);

            name.setText(building.getName());
            spaces.setText(spaceService.allActiveSpacesByBuilding(building.get_id()).size() + "");
        }

        return view;
    }

    private void initListener(ImageView imageView, final Building building) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildingDetailFragment buildingDetailFragment =  new BuildingDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUILDING_ID, building.get_id());
                buildingDetailFragment.setArguments(bundle);
                Utils.loadContentFragment(((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT), buildingDetailFragment, FragmentKeys.BUILDING_DETAIL_FRAGMENT, true);
            }
        });
    }
}
