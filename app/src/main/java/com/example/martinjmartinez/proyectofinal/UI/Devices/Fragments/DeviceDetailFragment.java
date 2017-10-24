package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import io.realm.Realm;
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
    private TextView space;
    private TextView averagePower;
    private TextView building;
    private TextView lastTimeUsed;
    private Switch status;
    private TextView power;
    private MainActivity mMainActivity;
    private Handler updatePowerHandler;
    private Runnable updatePowerRunnableDetails;
    private DeviceService deviceService;
    private HistorialService historialService;
    private Button mEditButton;
    private Button mDeleteButton;

    public DeviceDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mDeviceId = bundle.getString(Constants.DEVICE_ID, "");
        }

        mAPI = new API();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);

        iniVariables(view);
        getDevice();
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

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        deviceService = new DeviceService(Realm.getDefaultInstance());
        historialService = new HistorialService(Realm.getDefaultInstance());
        name = (TextView) view.findViewById(R.id.device_detail_name);
        ip_address = (TextView) view.findViewById(R.id.device_detail_ip);
        space = (TextView) view.findViewById(R.id.device_detail_space);
        building = (TextView) view.findViewById(R.id.device_detail_building);
        power = (TextView) view.findViewById(R.id.device_detail_power);
        lastTimeUsed = (TextView) view.findViewById(R.id.device_detail_last_turn_on);
        status = (Switch) view.findViewById(R.id.device_detail_status);
        mEditButton = (Button) view.findViewById(R.id.device_detail_update);
        mDeleteButton = (Button) view.findViewById(R.id.device_detail_delete);
        averagePower = (TextView) view.findViewById(R.id.device_detail_average);
    }

    private void initListeners() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUpdateFragment deviceUpdateFragment = new DeviceUpdateFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.DEVICE_ID, mDeviceId);
                deviceUpdateFragment.setArguments(bundle);

                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_DETAIL_FRAGMENT),
                        deviceUpdateFragment, FragmentKeys.DEVICE_UPDATE_FRAGMENT, true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Delete Device", "Do you want to delete this Device?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSpace(mAPI.getClient());
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshPower();
                    changeStatus(new API().getClient(), Constants.ON_QUERY);
                } else {
                    if (updatePowerHandler != null) {
                        power.setText("0 W");
                        updatePowerHandler.removeCallbacksAndMessages(null);
                    }
                    changeStatus(new API().getClient(), Constants.OFF_QUERY);
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

    private void deleteSpace(OkHttpClient client) {
        Log.e("QUERY", Constants.DEVICE_QUERY);
        Device newDevice = new Device();
        newDevice.setActive(false);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, newDevice.toIsActiveString());
        Request request = new Request.Builder()
                .url(Constants.DEVICE_QUERY + "/" + mDeviceId)
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("ERROR", response.body().string());
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceService.deleteDevice(mDeviceId);
                            changeStatus(mAPI.getClient(), Constants.OFF_QUERY);
                            mActivity.onBackPressed();
                        }
                    });
                }
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
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateStatus(new API().getClient(), action);
                        }
                    });
                }
            }
        });
    }

    public void updateStatus(final OkHttpClient client, final String action) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Device d = new Device();
        d.setStatus(action.equals(Constants.ON_QUERY) ? true : false);
        RequestBody body = RequestBody.create(JSON, d.statusToString());
        Log.e("updateStatus", Constants.DEVICE_QUERY + "/" + mDevice.get_id());
        Log.e("JSON", d.statusToString());

        final Request requestUpdate = new Request.Builder()
                .url(Constants.DEVICE_QUERY + "/" + mDevice.get_id())
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
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceService.updateDeviceStatus(mDeviceId, action.equals(Constants.ON_QUERY) ? true : false);
                            if (action.equals(Constants.ON_QUERY)) {
                                createHistory(mAPI.getClient(), mDevice);
                            } else {
                                closeHistory(mAPI.getClient(), mDevice);
                            }
                        }
                    });
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
        Log.e("createHistory", Constants.HISTORY_QUERY + "/device/" + device.get_id());

        String data = "{ \"startDate\": \"" + new Date().getTime() + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(Constants.HISTORY_QUERY + "/device/" + device.get_id())
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
                        final JSONObject historyData = new JSONObject(response.body().string());
                        final String historyid = historyData.getString("_id");

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAPI.getHistorialFromCloud(historyData);
                                deviceService.updateDeviceLastHistoryId(mDeviceId, historyid);
                                sendHistoryIdToArduino(mAPI.getClient(), historyid, device);
                            }
                        });


                    } catch (JSONException e) {
                        Log.e("createHistory", e.getMessage());
                    }
                }
            }
        });
    }

    private void closeHistory(OkHttpClient client, final Device device) {
        Log.e("closeHistory", Constants.HISTORY_QUERY + "/" + device.getLastHistoryId());
        String data = "{ \"endDate\": \"" + new Date().getTime() + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(Constants.HISTORY_QUERY + "/" + device.getLastHistoryId())
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
                    try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                mAPI.getHistorialFromCloudEnd(jsonObject, mDeviceId);
                                lastTimeUsed.setText(Utils.formatDefaultDate(mDevice.getHistorials().last().getEndDate()));
                                updateDevice(mAPI.getClient(), device);
                        }
                    });

                    } catch (JSONException |IOException e) {
                        Log.e("closeHistory", e.getMessage());
                    }
                }
            }
        });
    }

    private void updateDevice(OkHttpClient client, Device device) {

        Log.e("updateDevice", Constants.DEVICE_QUERY);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, device.averageToString());
        Log.e("JSON", device.averageToString());
        Request request = new Request.Builder()
                .url(Constants.DEVICE_QUERY + "/" + device.get_id())
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("updateDevice", response.body().string());
                } else {
                    Log.e("updateDevice", response.body().string());
                }
            }
        });
    }

    private void getDevice() {
        mDevice = deviceService.getDeviceById(mDeviceId);
        Log.e("DEVICE", mDevice.get_id() + "");
        initDeviceView(mDevice);
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
                    final String[] data = mAPI.getArduinoInfo(response);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data[0] != null) {
                                final double power = Double.parseDouble(data[0]);
                                deviceService.updateDevicePower(mDevice.get_id(), power);
                                final boolean status = data[1].equals("1");
                                deviceService.updateDeviceStatus(mDevice.get_id(), status);
                            }
                        }
                    });
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
                power.setText(Utils.decimalFormat.format(mDevice.getPower()) + " W");
                status.setChecked(mDevice.isStatus());
                updatePowerHandler.postDelayed(this, 7000);
            }
        };
        updatePowerHandler.postDelayed(updatePowerRunnableDetails, 7000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (updatePowerHandler != null) {
            updatePowerHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initDeviceView(Device device) {
        name.setText(device.getName());
        ip_address.setText(device.getIp_address());
        averagePower.setText(Utils.decimalFormat.format(device.getAverageConsumption()) + " W");
        status.setChecked(device.isStatus());
        if (mDevice.getHistorials().size() >= 1 && mDevice.getHistorials().size() != 1) {
            if(mDevice.isStatus()){
                lastTimeUsed.setText(Utils.formatDefaultDate(mDevice.getHistorials().get(mDevice.getHistorials().size()-1).getEndDate()));
            }else {
                lastTimeUsed.setText(Utils.formatDefaultDate(mDevice.getHistorials().last().getEndDate()));
            }
        } else {
            lastTimeUsed.setText("Never Used");
        }

        space.setText(device.getSpace() == null ? "" : device.getSpace().getName());

        building.setText(device.getBuilding().getName());

        if (device.isStatus()) {
            power.setText(Utils.decimalFormat.format(mDevice.getPower()) + " W");
            refreshPower();
        }
    }
}
