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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceDetailFragment;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/12/2017.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<Device> deviceList;
    public API mAPI;
    private Handler updatePowerHandler;
    private Runnable updatePowerRunnable;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName, mPower;
        public Switch mStatus;
        public RelativeLayout itemLayout;

        public ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.device_name);
            mPower = (TextView) view.findViewById(R.id.device_power);
            mStatus = (Switch) view.findViewById(R.id.device_status);
            itemLayout = (RelativeLayout) view.findViewById(R.id.device_item);
        }
    }

    public DeviceListAdapter(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);

        mAPI = new API();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Device device = deviceList.get(position);
        holder.mName.setText(device.getName());
        holder.mStatus.setChecked(device.isStatus());

        if (device.isStatus()) {
            refreshPower(device, holder);
        }

        holder.mStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshPower(device, holder);
                    changeStatus(new API().getClient(), ArgumentsKeys.ON_QUERY, device);
                } else {
                    if(updatePowerHandler != null) {
                        holder.mPower.setText("0 W");
                        updatePowerHandler.removeCallbacks(updatePowerRunnable);
                    }
                    changeStatus(new API().getClient(), ArgumentsKeys.OFF_QUERY, device);
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

    public void changeStatus(final OkHttpClient client, final String action, final Device device) {
        Request requestAction = new Request.Builder()
                .url("http://" + device.getIp_address() + action)
                .build();

        client.newCall(requestAction).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    updateStatus(new API().getClient(), action, device);
                    device.setStatus(action.equals(ArgumentsKeys.ON_QUERY) ? true : false);
                }
            }
        });
    }

    public void updateStatus(final OkHttpClient client, String action, Device device) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Device d = new Device();
        d.setStatus(action.equals(ArgumentsKeys.ON_QUERY) ? true : false);
        RequestBody body = RequestBody.create(JSON, d.statusToString());


        final Request requestUpdate = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY + "/" + device.get_id())
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
                    throw new IOException("Unexpected code " + response);
                } else {

                }
            }
        });
    }

    private void getPower(OkHttpClient client, final Device mDevice) {
        Log.e("QUERY", mDevice.getIp_address());
        Request request = new Request.Builder()
                .url("http://" + mDevice.getIp_address() + "?format=json")
                .addHeader("Connection", "close")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                Log.e("Error", "No se puso conectar al dispositivo", ex);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final double powerd = mAPI.getPower(response);

                    mDevice.setPower(powerd);
                }
            }
        });
    }

    private void refreshPower(final Device device, final ViewHolder holder) {
        updatePowerHandler = new Handler();
        updatePowerRunnable = new Runnable() {
            @Override
            public void run() {
                getPower(mAPI.getClient(), device);
                String power = String.format("%.1f", device.getPower());
                holder.mPower.setText(power + " W");
                notifyDataSetChanged();
            }
        };

        updatePowerHandler.postDelayed(updatePowerRunnable, 3000);
    }
}
