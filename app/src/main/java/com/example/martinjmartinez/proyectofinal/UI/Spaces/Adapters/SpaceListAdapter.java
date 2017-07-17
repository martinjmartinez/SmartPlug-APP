package com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceDetailFragment;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;


import java.util.List;

/**
 * Created by MartinJMartinez on 7/13/2017.
 */

public class SpaceListAdapter extends ArrayAdapter<Space> {

    public SpaceListAdapter(Context context, int resource, List<Space> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.space_list_item, null);
        }

        Space space = getItem(position);

        if (space != null) {
            ImageView detailsImage = (ImageView) view.findViewById(R.id.space_details);
            TextView name = (TextView) view.findViewById(R.id.space_name);
            TextView devices = (TextView) view.findViewById(R.id.space_devices);

            initListener(detailsImage, space);

            name.setText(space.getName());
            if (space.getDevices() != null) {
                devices.setText(space.getDevices().size() + "");
            }
        }

        return view;
    }

    private void initListener(ImageView imageView, final Space space) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpaceDetailFragment spaceDetailFragment =  new SpaceDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("QUERY", "http://192.168.1.17:3000/spaces/" + space.get_id());
                spaceDetailFragment.setArguments(bundle);
                Utils.loadContentFragment(((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT), spaceDetailFragment, FragmentKeys.SPACE_DETAIL_FRAGMENT, true);
            }
        });
    }

}