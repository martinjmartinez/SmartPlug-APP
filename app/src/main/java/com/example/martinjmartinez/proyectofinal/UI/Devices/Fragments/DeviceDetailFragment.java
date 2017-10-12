package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
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
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
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
    private MainActivity mMainActivity;
    private Handler updatePowerHandler;
    private Runnable updatePowerRunnableDetails;


    public DeviceDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mDeviceId = bundle.getString(ArgumentsKeys.DEVICE_ID, "");
        }
        mDevice = new Device();
        mAPI =  new API();
        getDevice(mAPI.getClient());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);

        iniVariables(view);
        status.setEnabled(false);
        initListeners();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;

    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Device Details");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1){
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {

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
                    refreshPower();
                    changeStatus(new API().getClient(), ArgumentsKeys.ON_QUERY);
                } else {
                    if(updatePowerHandler != null) {
                        power.setText("0 W");
                        updatePowerHandler.removeCallbacksAndMessages(null);
                    }
                    changeStatus(new API().getClient(), ArgumentsKeys.OFF_QUERY);
                }

            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }

    public void changeStatus(final OkHttpClient client, final String action) {
        Log.e("changeStatus", "http://" + mDevice.getIp_address() + action);
        Request requestAction = new Request.Builder()
                .url("http://" + mDevice.getIp_address() + action)
                .build();

        client.newCall(requestAction).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at changeStatus" + response);
                } else {
                    updateStatus(new API().getClient(), action);
                }
            }
        });
    }

    public void updateStatus(final OkHttpClient client, final String action) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Device d = new Device();
        d.setStatus(action.equals(ArgumentsKeys.ON_QUERY) ? true : false);
        RequestBody body = RequestBody.create(JSON, d.statusToString());
        Log.e("updateStatus", ArgumentsKeys.DEVICE_QUERY + "/" + mDevice.get_id());
        Log.e("JSON", d.statusToString());

        final Request requestUpdate = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY + "/" + mDevice.get_id())
                .patch(body)
                .build();

        client.newCall(requestUpdate).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at updateStatus" + response);
                } else {
                    mDevice.setStatus(action.equals(ArgumentsKeys.ON_QUERY) ? true : false);
                    if (action.equals(ArgumentsKeys.ON_QUERY)) {
                        createHistory(mAPI.getClient(), mDevice);
                    } else {
                        closeHistory(mAPI.getClient(), mDevice.getLastHistoryId());
                    }
                }
            }
        });
    }


    public void sendHistoryIdToArduino(final OkHttpClient client, String historyId, Device device) {
        Log.e("sendHistoryIdToArduino", "http://" + device.getIp_address() + "/historyId?params=" + historyId);
        Request requestAction = new Request.Builder()
                .url("http://" + device.getIp_address() + "/historyId?params=" + historyId)
                .build();

        client.newCall(requestAction).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("sendHistoryIdToArduino" + response);
                } else {
                    Log.e("sendHistoryIdToArduino", "id sent to arduino");
                }
            }
        });
    }

    private void createHistory(OkHttpClient client, final Device device) {
        Log.e("createHistory", ArgumentsKeys.HISTORY_QUERY + "/device/" + device.get_id());
        String data = "{ \"startDate\": \"" + new Date().getTime() + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.HISTORY_QUERY + "/device/" + device.get_id())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("createHistory", response.body().string());
                } else {
                    try {
                        JSONObject historyData = new JSONObject(response.body().string());
                        String historyid = historyData.getString("_id");
                        device.setLastHistoryId(historyid);
                        sendHistoryIdToArduino(mAPI.getClient(), historyid, device);
                    } catch (JSONException e) {
                        Log.e("createHistory", e.getMessage());
                    }
                }
            }
        });
    }

    private void closeHistory(OkHttpClient client, String historyId) {
        Log.e("closeHistory", ArgumentsKeys.HISTORY_QUERY + "/" + historyId);
        String data = "{ \"endDate\": \"" + new Date().getTime() + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.HISTORY_QUERY + "/" + historyId)
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error at closeHistory", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("closeHistory", response.body().string());
                } else {

                }
            }
        });
    }

    private void getDevice(OkHttpClient client) {
        Log.e("getDevice", ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error at getDevice", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at getDevice" + response);
                } else {
                    mDevice = mAPI.getDevice(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initDeviceView(mDevice);
                        }
                    });
                }
            }
        });
    }

    private void getDeviceInfo(OkHttpClient client) {
        Log.e("getArduinoInfoDetails", mDevice.getIp_address());
        Request request = new Request.Builder()
                .url("http://" + mDevice.getIp_address() + "?format=json")
                .addHeader("Connection", "close")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                Log.e("Error at getArduinoInfo", "No se puso conectar al dispositivo", ex);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at getArduinoInfo" + response);
                } else {
                    String[] data = mAPI.getArduinoInfo(response);
                    if (data[0] != null) {
                        final double power = Double.parseDouble(data[0]);
                        mDevice.setPower(power);
                        final boolean status = data[1].equals("1");
                        mDevice.setStatus(status);
                    }

                }
            }
        });
    }

    private void refreshPower() {
        updatePowerHandler = new Handler();
        updatePowerRunnableDetails = new Runnable() {
            @Override
            public void run() {
                getDeviceInfo(mAPI.getClient());
                String powerS = String.format("%.1f", mDevice.getPower());
                power.setText(powerS + " W");
                //status.setChecked(mDevice.isStatus());
                updatePowerHandler.postDelayed(this, 7000);
            }
        };

        updatePowerHandler.postDelayed(updatePowerRunnableDetails, 7000);
    }

    private void initDeviceView( Device device) {
        name.setText(device.getName());
        ip_address.setText(device.getIp_address());
        status.setChecked(device.isStatus());
        space.setText(device.getSpace().getName());
        status.setEnabled(true);
        building.setText(device.getBuilding().getName());

        if (device.isStatus()) {
            refreshPower();
        }
    }
}
