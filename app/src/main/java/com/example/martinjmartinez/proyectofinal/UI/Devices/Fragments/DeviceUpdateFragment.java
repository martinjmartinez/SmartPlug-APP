package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

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
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.List;

import io.realm.Realm;



public class DeviceUpdateFragment extends Fragment {

    private Device mDevice;
    private Activity mActivity;
    private EditText name;
    private TextView displayName;
    private TextView deviceBuilding;
    private TextView deviceSpace;
    private TextView spaceWarning;
    private Button saveDevice;
    private String mDeviceId;
    private Space mSpace, lastSpace;

    private Spinner mSpaceSpinner;
    private SpaceSpinnerAdapter mSpaceSpinnerAdapter;
    private MainActivity mMainActivity;
    private DeviceService deviceService;
    private SpaceService spaceService;
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
        spaceService = new SpaceService(Realm.getDefaultInstance());
        name =  view.findViewById(R.id.device_create_name);
        displayName =  view.findViewById(R.id.device_create_display_name);
        deviceBuilding =  view.findViewById(R.id.device_create_building);
        deviceSpace =  view.findViewById(R.id.device_create_space);
        saveDevice =  view.findViewById(R.id.device_create_save_button);
        mSpaceSpinner =  view.findViewById(R.id.device_create_space_spinner);
        spaceWarning = view.findViewById(R.id.space_warning);
    }

    private void initListeners() {
        mSpaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpace = mDevice.getBuilding().getSpaces().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpace = mDevice.getSpace();
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
                        DeviceFB deviceFB = new DeviceFB(mDeviceId, name.getText().toString(), mDevice.isStatus(), mSpace.get_id(), mDevice.isActive(), mSpace.getBuilding().get_id(), mDevice.getAverageConsumption(), mDevice.getPower());
                        android.util.Log.e("UPDATE", "UPDATE  1");
                        deviceService.updateDeviceCloud(deviceFB);
                        spaceService.updateSpacePowerAverageConsumption(mSpace.get_id());
                        if (lastSpace != null) {
                            spaceService.updateSpacePowerAverageConsumption(lastSpace.get_id());
                        }
                    } else {
                        DeviceFB deviceFB = new DeviceFB(mDeviceId, name.getText().toString(), mDevice.isStatus(), "",  mDevice.isActive(), mDevice.getBuilding().get_id(), mDevice.getAverageConsumption(), mDevice.getPower());
                        android.util.Log.e("UPDATE", "UPDATE 1");
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

        setUpSpacesSpinner(spaceService.allActiveSpacesByBuilding(mDevice.getBuilding().get_id()));
    }

    public void setUpSpacesSpinner(List<Space> items) {
        if (items.size() != 0 && !mDevice.isStatus()) {
            spaceWarning.setVisibility(View.GONE);
            mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
            mSpaceSpinner.setAdapter(mSpaceSpinnerAdapter);
            lastSpace = mDevice.getSpace();
        } else {
            spaceWarning.setVisibility(View.VISIBLE);
            mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
            mSpaceSpinner.setAdapter(mSpaceSpinnerAdapter);
            mSpaceSpinner.setEnabled(false);
        }

    }
}
