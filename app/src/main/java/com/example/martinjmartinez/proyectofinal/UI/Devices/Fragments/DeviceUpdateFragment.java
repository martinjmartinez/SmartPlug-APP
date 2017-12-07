package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.DevicesLimitService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.Services.SpacesLimitsService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.List;

import io.realm.Realm;


public class DeviceUpdateFragment extends Fragment {

    private Device mDevice;
    private Activity mActivity;
    private EditText name;
    private EditText monthlyLimit;
    private TextView displayName;
    private TextView deviceBuilding;
    private TextView deviceSpace;
    private TextView spaceWarning;
    private Button saveDevice;
    private String mDeviceId;
    private Space mSpace, lastSpace;
    private Button pairButton;
    private Spinner mSpaceSpinner;
    private SpaceSpinnerAdapter mSpaceSpinnerAdapter;
    private MainActivity mMainActivity;
    private DeviceService deviceService;
    private SpaceService spaceService;
    private SpacesLimitsService spacesLimitsService;
    private DevicesLimitService devicesLimitService;
    private BuildingService buildingService;

    public DeviceUpdateFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mDeviceId = bundle != null ? bundle.getString(Constants.DEVICE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_creation_fragment, container, false);

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

        mMainActivity.getSupportActionBar().setTitle("Device Edit");
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
        buildingService = new BuildingService(Realm.getDefaultInstance());
        devicesLimitService = new DevicesLimitService(Realm.getDefaultInstance());
        spacesLimitsService = new SpacesLimitsService(Realm.getDefaultInstance());
        spaceService = new SpaceService(Realm.getDefaultInstance());
        name = view.findViewById(R.id.device_create_name);
        monthlyLimit = view.findViewById(R.id.device_create_limit);
        displayName = view.findViewById(R.id.device_create_display_name);
        deviceBuilding = view.findViewById(R.id.device_create_building);
        deviceSpace = view.findViewById(R.id.device_create_space);
        saveDevice = view.findViewById(R.id.device_create_save_button);
        mSpaceSpinner = view.findViewById(R.id.device_create_space_spinner);
        spaceWarning = view.findViewById(R.id.space_warning);
        pairButton = view.findViewById(R.id.pair_device);
        pairButton.setVisibility(View.VISIBLE);
    }

    private void initListeners() {
        pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Change network", "Do you want to connect to another wifi network?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeviceFB deviceFB = deviceService.castToDeviceFB(mDevice);

                        deviceService.updateDeviceConnected(deviceFB.get_id(), false);
                        deviceService.updateDeviceReset(deviceFB.get_id(), true);

                        DevicePairFragment devicePairFragment = new DevicePairFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.DEVICE_ID, mDeviceId);
                        bundle.putBoolean(Constants.IS_NEW_DEVICE, false);
                        devicePairFragment.setArguments(bundle);
                        Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_UPDATE_FRAGMENT), devicePairFragment, FragmentKeys.DEVICE_PAIR_FRAGMENT, true);
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

        mSpaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpace = spaceService.allActiveSpacesByBuilding(mDevice.getBuilding().get_id()).get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpace = mDevice.getSpace();
                mSpaceSpinner.setSelection(spaceService.allActiveSpacesByBuilding(mDevice.getBuilding().get_id()).indexOf(mSpace));

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayName.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayName.setText(s.toString());
            }
        });

        saveDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEditTextEmpty(name)) {
                    if (mSpace != null) {
                        DeviceFB deviceFB = deviceService.castToDeviceFB(mDevice);

                        spacesLimitsService.updateDevices(deviceFB.getSpaceId(), mSpace.get_id(), deviceFB.get_id());
                        deviceFB.setSpaceId(mSpace.get_id());
                        deviceFB.setName(name.getText().toString());
                        deviceFB.setMonthlyLimit(Utils.isEditTextEmpty(monthlyLimit) ? 0 : Double.parseDouble(monthlyLimit.getText().toString()));
                        Log.e("limit", deviceFB.getMonthlyLimit() + "ppp");
                        devicesLimitService.updateOrCreateCloud(deviceFB);
                        deviceService.updateDeviceCloud(deviceFB);
                        spaceService.updateSpacePowerAverageConsumption(mSpace.get_id());
                        if (lastSpace != null) {
                            spaceService.updateSpacePowerAverageConsumption(lastSpace.get_id());
                        }
                    } else {
                        DeviceFB deviceFB = deviceService.castToDeviceFB(mDevice);
                        deviceFB.setSpaceId("");
                        deviceFB.setMonthlyLimit(Utils.isEditTextEmpty(monthlyLimit) ? 0 : Double.parseDouble(monthlyLimit.getText().toString()));
                        deviceFB.setName(name.getText().toString());
                        Log.e("limit", deviceFB.getMonthlyLimit() + "pppp");
                        devicesLimitService.updateOrCreateCloud(deviceFB);
                        deviceService.updateDeviceCloud(deviceFB);
                    }
                    mActivity.onBackPressed();
                } else {
                    Toast.makeText(getActivity(), "Please, name your device.", Toast.LENGTH_SHORT).show();
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

    private void getDevice() {
        mDevice = deviceService.getDeviceById(mDeviceId);

        name.setText(mDevice.getName());
        displayName.setText(mDevice.getName());
        deviceBuilding.setText(mDevice.getBuilding().getName());
        mSpaceSpinner.setVisibility(View.VISIBLE);
        deviceSpace.setVisibility(View.GONE);
        monthlyLimit.setText(String.valueOf(mDevice.getMonthlyLimit()));

        setUpSpacesSpinner(spaceService.allActiveSpacesByBuilding(mDevice.getBuilding().get_id()));
    }

    public void setUpSpacesSpinner(List<Space> items) {
        if (items.size() != 0 && !mDevice.isStatus()) {
            spaceWarning.setVisibility(View.GONE);
            mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
            mSpaceSpinner.setAdapter(mSpaceSpinnerAdapter);
            lastSpace = mDevice.getSpace();
            mSpaceSpinner.setSelection(spaceService.allActiveSpacesByBuilding(mDevice.getBuilding().get_id()).indexOf(lastSpace));

        } else {
            if(mDevice.isStatus()){
                spaceWarning.setVisibility(View.VISIBLE);
            }
            mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
            mSpaceSpinner.setAdapter(mSpaceSpinnerAdapter);
            mSpaceSpinner.setEnabled(false);
        }

    }
}
