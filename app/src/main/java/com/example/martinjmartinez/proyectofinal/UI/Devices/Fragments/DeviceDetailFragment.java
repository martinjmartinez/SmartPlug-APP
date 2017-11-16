package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.Models.HistorialFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.HistorialService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics.DeviceStatisticsDetailsFragment;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;

import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.realm.Realm;

public class DeviceDetailFragment extends Fragment {

    private Device mDevice;
    private Activity mActivity;
    private String mDeviceId, userId;
    private TextView name;
    private TextView space;
    private TextView averagePower;
    private TextView building;
    private TextView lastTimeUsed;
    private Switch status;
    private TextView power;
    private MainActivity mMainActivity;
    private DeviceService deviceService;
    private HistorialService historialService;
    private Button mEditButton;
    //private Button mDeleteButton;
    private LinearLayout statisticsButton;
    private DatabaseReference databaseReference;
    private CompoundButton.OnCheckedChangeListener listener;
    private ValueEventListener deviceListener;

    public DeviceDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mDeviceId = bundle.getString(Constants.DEVICE_ID, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);

        iniVariables(view);
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
        name = view.findViewById(R.id.device_detail_name);
        space = view.findViewById(R.id.device_detail_space);
        building = view.findViewById(R.id.device_detail_building);
        power =  view.findViewById(R.id.device_detail_power);
        lastTimeUsed =  view.findViewById(R.id.device_detail_last_turn_on);
        status = view.findViewById(R.id.device_detail_status);
        mEditButton =  view.findViewById(R.id.device_detail_update);
       // mDeleteButton = view.findViewById(R.id.device_detail_delete);
        averagePower =  view.findViewById(R.id.device_detail_average);
        statisticsButton =  view.findViewById(R.id.device_statistics_button);
        userId = mActivity.getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/"+ userId + "/Devices");

        listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deviceService.updateDeviceStatus(mDeviceId, true);
                    String historyid = historialService.startHistorial(new Date(), mDeviceId);
                    deviceService.updateDeviceLastHistoryId(mDeviceId, historyid);

                } else {
                    deviceService.updateDeviceStatus(mDeviceId, false);
                    HistorialFB historialFB = historialService.castToHistorialFB(historialService.getHistorialById(mDevice.getLastHistoryId()), new Date());
                    Log.e("DeviceDetailAdapter", "getHistoryLogs");
                    historialService.closeHistory(historialFB);
                    deviceService.updateDevicePower(mDevice.get_id(), 0);
                    averagePower.setText(Utils.decimalFormat.format(mDevice.getAverageConsumption()) + " W");
                }

            }
        };
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

//        mDeleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Delete Device", "Do you want to delete this Device?");
//
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deviceService.deleteDevice(mDeviceId);
//                        mActivity.onBackPressed();
//                    }
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                // Create the AlertDialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//            }
//        });
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceStatisticsDetailsFragment deviceStatisticsDetailsFragment = new DeviceStatisticsDetailsFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.DEVICE_ID, mDeviceId);
                deviceStatisticsDetailsFragment.setArguments(bundle);

                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_DETAIL_FRAGMENT),
                        deviceStatisticsDetailsFragment, FragmentKeys.DEVICE_STATISTICS_FRAGMENT, true);
            }
        });



        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        deviceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DeviceFB deviceFB = dataSnapshot.getValue(DeviceFB.class);
                deviceService.updateDeviceLocal(deviceFB);
                mDevice = deviceService.getDeviceById(mDeviceId);
                initDeviceView(mDevice);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child(mDeviceId).addValueEventListener(deviceListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        databaseReference.child(mDeviceId).removeEventListener(deviceListener);
    }

    private void initDeviceView(Device device) {
        name.setText(device.getName());
        status.setOnCheckedChangeListener(null);
        status.setChecked(device.isStatus());
        status.setOnCheckedChangeListener(listener);
        averagePower.setText(Utils.decimalFormat.format(device.getAverageConsumption()) + " W");

        if (mDevice.getLastTimeUsed().getTime() != 0) {
            lastTimeUsed.setText(Utils.formatDefaultDate(mDevice.getLastTimeUsed()));
        } else {
            lastTimeUsed.setText("Never Used");
        }

        space.setText(device.getSpace() == null ? "" : device.getSpace().getName());

        building.setText(device.getBuilding().getName());

        if (device.isStatus()) {
            power.setText(Utils.decimalFormat.format(mDevice.getPower()) + " W");
        }
    }
}
