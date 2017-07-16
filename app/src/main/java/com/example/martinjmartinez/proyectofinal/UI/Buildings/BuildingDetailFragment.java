package com.example.martinjmartinez.proyectofinal.UI.Buildings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.API;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/15/2017.
 */

public class BuildingDetailFragment extends Fragment {

    private Building mBuilding;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;

    public BuildingDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mQuery = bundle.getString("QUERY", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_fragment, container, false);

        iniVariables();
        getBuilding(mAPI.getClient(), view);

        return view;
    }

    private void iniVariables() {
        mBuilding = new Building();
        mActivity = getActivity();
        mAPI =  new API();
    }

    private void initListeners() {

    }

    private void getBuilding(OkHttpClient client, final View view) {
        Log.e("QUERY", mQuery);
        Request request = new Request.Builder()
                .url(mQuery)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBuilding = mAPI.getBuilding(response);
                            initDeviceView(mBuilding, view);
                        }
                    });
                }
            }
        });
    }

    private void initDeviceView( Building building, View view) {

        TextView name = (TextView) view.findViewById(R.id.building_detail_name);
        TextView spaces = (TextView) view.findViewById(R.id.building_detail_spaces);
        //TextView building = (TextView) view.findViewById(R.id.space_detail_building);
        TextView power = (TextView) view.findViewById(R.id.building_detail_power);

        name.setText(building.getName());

        if (building.getSpaces() != null) {
            spaces.setText(building.getSpaces().size() + "");
        }
        //power.setText();
    }
}
