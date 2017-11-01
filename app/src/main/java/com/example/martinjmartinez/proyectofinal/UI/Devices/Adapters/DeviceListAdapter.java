package com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<Device> deviceList;
    private DeviceService deviceService;
    private HistorialService historialService;
    private HomeFragment homeFragment;
    private CompoundButton.OnCheckedChangeListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

        deviceService = new DeviceService(Realm.getDefaultInstance());
        historialService = new HistorialService(Realm.getDefaultInstance());

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Device device = deviceService.getDeviceById(deviceList.get(position).get_id());

        listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deviceService.updateDeviceStatus(device.get_id(), true);
                    String historyid = historialService.startHistorial(new Date(), device.get_id());
                    deviceService.updateDeviceLastHistoryId(device.get_id(), historyid);
                } else {
                    deviceService.updateDeviceStatus(device.get_id(), false);
                    historialService.updateHistorialEndDate(device.getLastHistoryId(), new Date());
                    deviceService.updateDevicePowerAverageConsumption(device.get_id());
                    holder.mAverage.setText(Utils.decimalFormat.format(device.getAverageConsumption()) + " W");
                }
            }
        };

        holder.mName.setText(device.getName());
        holder.mAverage.setText(Utils.decimalFormat.format(device.getAverageConsumption()) + " W");
        holder.mStatus.setOnCheckedChangeListener(null);
        holder.mStatus.setChecked(device.isStatus());
        holder.mStatus.setOnCheckedChangeListener(listener);

        if (holder.mStatus.isChecked()) {
            holder.mPower.setText(Utils.decimalFormat.format(device.getPower()) + " W");
        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceDetailFragment deviceDetailFragment = new DeviceDetailFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.DEVICE_ID, device.get_id());
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

}
