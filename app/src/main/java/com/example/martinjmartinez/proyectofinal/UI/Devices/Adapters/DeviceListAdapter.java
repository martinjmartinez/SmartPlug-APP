package com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceDetailFragment;
import com.example.martinjmartinez.proyectofinal.UI.Home.HomeFragment;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<Device> deviceList;
    public API mAPI;
    private Handler updatePowerHandler;
    private Runnable updatePowerRunnable;
    private DeviceService deviceService;
    private HistorialService historialService;
    private Activity activity;
    private HomeFragment homeFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mName, mPower, mAverage;
        Switch mStatus;
        LinearLayout itemLayout;


        ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.device_name);
            mPower = (TextView) view.findViewById(R.id.device_power);
            mStatus = (Switch) view.findViewById(R.id.device_status);
            itemLayout = (LinearLayout) view.findViewById(R.id.device_item);
            mAverage = (TextView) view.findViewById(R.id.device_power_average);
        }
    }

    public DeviceListAdapter(List<Device> deviceList, Activity activity, Fragment fragment) {
        this.deviceList = deviceList;
        this.activity = activity;

        if (fragment instanceof HomeFragment) {
            homeFragment = (HomeFragment) fragment;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.device_list_item;
        if (homeFragment != null) {
            layout = R.layout.device_list_item_home;
        }

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        mAPI = new API();
        deviceService = new DeviceService(Realm.getDefaultInstance());
        historialService = new HistorialService(Realm.getDefaultInstance());

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Device device = deviceService.getDeviceById(deviceList.get(position).get_id());
        // refreshPower(device, holder);
        holder.mName.setText(device.getName());
        holder.mAverage.setText(Utils.decimalFormat.format(device.getAverageConsumption()) + " W");
        getDeviceInfo(mAPI.getClient(), device);

        holder.mStatus.setChecked(device.isStatus());
        if (holder.mStatus.isChecked()) {
            holder.mPower.setText(Utils.decimalFormat.format(device.getPower()) + "W");
            refreshPower(device, holder);
        }

        holder.mStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshPower(device, holder);
                    changeStatus(new API().getClient(), ArgumentsKeys.ON_QUERY, device.get_id());
                } else {
                    if (updatePowerHandler != null) {
                        holder.mPower.setText("0 W");
                        updatePowerHandler.removeCallbacks(updatePowerRunnable);
                    }
                    changeStatus(new API().getClient(), ArgumentsKeys.OFF_QUERY, device.get_id());
                }
            }
        });

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceDetailFragment deviceDetailFragment = new DeviceDetailFragment();
                Bundle bundle = new Bundle();

                bundle.putString(ArgumentsKeys.DEVICE_ID, device.get_id());
                deviceDetailFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                if (activity.getSupportFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT) != null) {
                    Utils.loadContentFragment(activity.getSupportFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT), deviceDetailFragment, FragmentKeys.DEVICE_DETAIL_FRAGMENT, true);
                } else {
                    Utils.loadContentFragment(activity.getSupportFragmentManager().findFragmentByTag(FragmentKeys.HOME_FRAGMENT), deviceDetailFragment, FragmentKeys.DEVICE_DETAIL_FRAGMENT, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void changeStatus(final OkHttpClient client, final String action, final String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);
        Log.e("changeStatus", "http://" + device.getIp_address() + action);
        Request requestAction = new Request.Builder()
                .url("http://" + device.getIp_address() + action)
                .build();

        client.newCall(requestAction).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeStatus(client, action, deviceId);
                    }
                });

                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code at changeStatus" + response);
                } else {
                    updateStatus(new API().getClient(), action, deviceId);
                }
            }
        });
    }

    private void createHistory(OkHttpClient client, final String deviceId) {
        Log.e("createHistory", ArgumentsKeys.HISTORY_QUERY + "/device/" + deviceId);
        String data = "{ \"startDate\": \"" + new Date().getTime() + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.HISTORY_QUERY + "/device/" + deviceId)
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
                        final String historyId = historyData.getString("_id");

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAPI.getHistorialFromCloud(historyData);
                                deviceService.updateDeviceLastHistoryId(deviceId, historyId);
                                sendHistoryIdToArduino(mAPI.getClient(), historyId, deviceId);
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("createHistory", e.getMessage());
                    }
                }
            }
        });
    }

    public void sendHistoryIdToArduino(final OkHttpClient client, String historyId, String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);
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

    private void closeHistory(OkHttpClient client, final String deviceId) {
        Device device = deviceService.getDeviceById(deviceId);
        Log.e("closeHistory", ArgumentsKeys.HISTORY_QUERY + "/" + device.getLastHistoryId());
        String data = "{ \"endDate\": \"" + new Date().getTime() + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        final Request request = new Request.Builder()
                .url(ArgumentsKeys.HISTORY_QUERY + "/" + device.getLastHistoryId())
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAPI.getHistorialFromCloudEnd(jsonObject, deviceId);
                                notifyDataSetChanged();
                                if (homeFragment != null) {
                                    homeFragment.getBuilding();
                                }
                            }
                        });
                    } catch (JSONException | IOException e) {
                        Log.e("closeHistory", e.getMessage());
                    }
                }
            }
        });
    }

    public void updateStatus(final OkHttpClient client, final String action, final String deviceId) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        boolean status = action.equals(ArgumentsKeys.ON_QUERY) ? true : false;
        RequestBody body = RequestBody.create(JSON, "{" + "\"status\":" + status + '}');

        Log.e("updateStatus", ArgumentsKeys.DEVICE_QUERY + "/" + deviceId);
        Log.e("JSON", "{" + "\"status\":" + status + '}');

        final Request requestUpdate = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY + "/" + deviceId)
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceService.updateDeviceStatus(deviceId, action.equals(ArgumentsKeys.ON_QUERY) ? true : false);
                            if (action.equals(ArgumentsKeys.ON_QUERY)) {
                                createHistory(mAPI.getClient(), deviceId);
                            } else {

                                closeHistory(mAPI.getClient(), deviceId);
                            }
                        }
                    });
                }
            }
        });
    }

    private void getDeviceInfo(OkHttpClient client, final Device mDevice) {
        Log.e("getArduinoInfoAdapter", mDevice.getIp_address());
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

                    activity.runOnUiThread(new Runnable() {
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

    private void refreshPower(final Device device, final ViewHolder holder) {
        updatePowerHandler = new Handler();
        updatePowerRunnable = new Runnable() {
            @Override
            public void run() {
                getDeviceInfo(mAPI.getClient(), device);
                String power = String.format("%.1f", device.getPower());
                holder.mPower.setText(power + " W");
                holder.mStatus.setChecked(device.isStatus());
                notifyDataSetChanged();
            }
        };

        updatePowerHandler.postDelayed(updatePowerRunnable, 7000);
    }
}
