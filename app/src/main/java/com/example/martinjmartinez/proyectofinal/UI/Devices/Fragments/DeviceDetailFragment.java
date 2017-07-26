package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/14/2017.
 */

public class DeviceDetailFragment extends Fragment {

    private Device mDevice;
    private API mAPI;
    private Activity mActivity;
    private String mDeviceId;
    private TextView name;
    private TextView ip_address;
    private TextView space ;
    private TextView building;
    private Switch status;
    private TextView power;


    public DeviceDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mDeviceId = bundle.getString(ArgumentsKeys.DEVICE_ID, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);

        iniVariables(view);
        getDevice(mAPI.getClient(), view);
        initListeners();
        return view;
    }

    private void iniVariables(View view) {
        mDevice = new Device();
        mActivity = getActivity();
        mAPI =  new API();
        name = (TextView) view.findViewById(R.id.device_detail_name);
        ip_address = (TextView) view.findViewById(R.id.device_detail_ip);
        space = (TextView) view.findViewById(R.id.device_detail_space);
        building = (TextView) view.findViewById(R.id.device_detail_building);
        power = (TextView) view.findViewById(R.id.device_detail_power);
        status = (Switch) view.findViewById(R.id.device_detail_status);
    }

    private void initListeners() {

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeStatus(new API().getClient(), ArgumentsKeys.ON_QUERY);
                } else {
                    changeStatus(new API().getClient(), ArgumentsKeys.OFF_QUERY);
                }

            }
        });
    }

    public void changeStatus(final OkHttpClient client, final String action) {
        Request requestAction = new Request.Builder()
                .url("http://" + mDevice.getIp_address() + action)
                .build();

        client.newCall(requestAction).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("me1", "cago en seuta");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    updateStatus(new API().getClient(), action);
                }
            }
        });
    }

    public void updateStatus(final OkHttpClient client, String action) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Device d = new Device();
        d.setStatus(action.equals(ArgumentsKeys.ON_QUERY) ? true : false);
        RequestBody body = RequestBody.create(JSON, d.statusToString());


        final Request requestUpdate = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY + "/" + mDevice.get_id())
                .patch(body)
                .build();

        client.newCall(requestUpdate).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("me2", "cago en seuta");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                }
            }
        });
    }

    private void getDevice(OkHttpClient client, final View view) {
        Log.e("QUERY", ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId)
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

    private void getPower(OkHttpClient client, final View view) {
        Log.e("QUERY", mDevice.getIp_address());
        Request request = new Request.Builder()
                .url("http://" + mDevice.getIp_address() + "?format=json")
                .addHeader("Connection", "close")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                Log.e("Error", "ghvhvj", ex);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final double powerd = mAPI.getPower(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDevice.setPower(powerd);
                            power.setText(Double.toString(mDevice.getPower()) + " W");
                        }
                    });
                }
            }
        });
    }

    private void refreshPower(Device device, final View view) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getPower(mAPI.getClient(), view);
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void initDeviceView( Device device, View view) {

        name.setText(device.getName());
        ip_address.setText(device.getIp_address());
        status.setChecked(device.isStatus());
        if (device.getSpace() != null) {
            space.setText(device.getSpace().getName());

            if (device.getSpace().getBuilding() != null) {
                building.setText(device.getSpace().getBuilding().getName());
            }
        }

        refreshPower(device, view);
    }
}
