package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;


import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceCreateFragment extends Fragment {

    private Device mDevice;
    private API mAPI;
    private Activity mActivity;
    private String mBuildingId;
    private EditText name;
    private EditText ipAddress;
    private TextView displayName;
    private TextView deviceBuilding;
    private TextView deviceSpace;
    private Button saveDevice;
    private String mSpaceId, mDeviceId;
    private Space mSpace;
    private Building mBuilding;
    private Spinner mSpaceSpinner;
    private SpaceSpinnerAdapter mSpaceSpinnerAdapter;
    private MainActivity mMainActivity;
    private DeviceService deviceService;
    private SpaceService spaceService;
    private BuildingService buildingService;

    public DeviceCreateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mSpaceId = bundle != null ? bundle.getString(Constants.SPACE_ID, "") : "";
        mBuildingId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_creation_fragment, container, false);

        iniVariables(view);
        initListeners();
        if (!mSpaceId.isEmpty()) {
            mSpace = spaceService.getSpaceById(mSpaceId);
            mBuilding = mSpace.getBuilding();
        } else {
            mBuilding = buildingService.getBuildingById(mBuildingId);
        }

        initView();

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

        mMainActivity.getSupportActionBar().setTitle("New Device");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        mAPI = new API();
        deviceService = new DeviceService(Realm.getDefaultInstance());
        buildingService = new BuildingService(Realm.getDefaultInstance());
        spaceService = new SpaceService(Realm.getDefaultInstance());
        name = (EditText) view.findViewById(R.id.device_create_name);
        ipAddress = (EditText) view.findViewById(R.id.device_create_ip);
        displayName = (TextView) view.findViewById(R.id.device_create_display_name);
        deviceBuilding = (TextView) view.findViewById(R.id.device_create_building);
        deviceSpace = (TextView) view.findViewById(R.id.device_create_space);
        saveDevice = (Button) view.findViewById(R.id.device_create_save_button);
        mSpaceSpinner = (Spinner) view.findViewById(R.id.device_create_space_spinner);
    }

    private void initListeners() {
        mSpaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpace = mBuilding.getSpaces().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpace = mBuilding.getSpaces().get(0);
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
                mDevice = new Device();
                if (!Utils.isEditTextEmpty(name) && mDevice != null && !Utils.isEditTextEmpty(ipAddress)) {
                    mDevice.setName(name.getText().toString());
                    mDevice.setIp_address(ipAddress.getText().toString());
                    mDevice.setStatus(false);
                    mDevice.setBuilding(mBuilding);
                    Log.e("BUILDING", mBuilding.get_id());
                    mDevice.setSpace(mSpace);

                    createDevice(mAPI.getClient(), mDevice.deviceToString());
                } else {
                    Toast.makeText(getActivity(), "Please, fill all the fields.", Toast.LENGTH_SHORT).show();
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

    private void createDevice(OkHttpClient client, String data) {
        Log.e("QUERY", Constants.DEVICE_QUERY);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(Constants.DEVICE_QUERY)
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
                    Log.e("ERROR1", response.body().string());
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSpace != null) {
                                mAPI.getDeviceFromCloud(response, mSpace.get_id(), mBuilding.get_id());
                            } else {
                                mAPI.getDeviceFromCloud(response, "", mBuilding.get_id());
                            }

                            mMainActivity.onBackPressed();
                        }
                    });
                }
            }
        });
    }


    public void setUpSpacesSpinner(List<Space> items) {
        if (items.size() != 0) {
            mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
            mSpaceSpinner.setAdapter(mSpaceSpinnerAdapter);
        } else {
            mSpaceSpinner.setEnabled(false);
        }

    }

    private void initView() {
        if (mSpace != null) {
            mSpaceSpinner.setVisibility(View.GONE);
            deviceSpace.setVisibility(View.VISIBLE);
            deviceBuilding.setText(mSpace.getBuilding().getName());
            deviceSpace.setText(mSpace.getName());

        } else {
            mSpaceSpinner.setVisibility(View.VISIBLE);
            deviceSpace.setVisibility(View.GONE);
            setUpSpacesSpinner(mBuilding.getSpaces());
            deviceBuilding.setText(mBuilding.getName());
        }
    }
}
