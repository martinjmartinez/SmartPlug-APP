package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.API;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/14/2017.
 */

public class DeviceDetailFragment extends Fragment {

    private Device mDevice;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;

    public DeviceDetailFragment() {}

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
        View view = inflater.inflate(R.layout.device_fragment, container, false);

        iniVariables(view);
        getDevice(mAPI.getClient(), view);

        return view;
    }

    private void iniVariables(View view) {
        mDevice = new Device();
        mActivity = getActivity();
        mAPI =  new API();
    }

    private void initListeners() {

    }

    private void getDevice(OkHttpClient client, final View view) {
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
                            mDevice = mAPI.getDevice(response);
                            initDeviceView(mDevice, view);
                        }
                    });
                }
            }
        });
    }

    private void initDeviceView( Device device, View view) {

        TextView name = (TextView) view.findViewById(R.id.device_detail_name);
        TextView ip_address = (TextView) view.findViewById(R.id.device_detail_ip);
        TextView space = (TextView) view.findViewById(R.id.device_detail_space);
        TextView building = (TextView) view.findViewById(R.id.device_detail_building);
        TextView power = (TextView) view.findViewById(R.id.device_detail_power);

        name.setText(device.getName());
        ip_address.setText(device.getIp_address());
        if (device.getSpace() != null) {
            space.setText(device.getSpace().getName());

            if (device.getSpace().getBuilding() != null) {
                building.setText(device.getSpace().getBuilding().getName());
            }
        }
        //power.setText();
    }
}
