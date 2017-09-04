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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters.BuildingSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceSpinnerAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/19/2017.
 */

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

    public DeviceCreateFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mSpaceId = bundle != null ? bundle.getString(ArgumentsKeys.SPACE_ID, "") : "";
        mBuildingId = bundle != null ? bundle.getString(ArgumentsKeys.BUILDING_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_creation_fragment, container, false);

        iniVariables(view);
        initListeners();
        if (!mSpaceId.isEmpty()) {
            getSpace(mAPI.getClient());
        } else {
            getBuilding(mAPI.getClient());
        }



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

        if(mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1){
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        mAPI =  new API();
        name = (EditText) view.findViewById(R.id.device_create_name);
        ipAddress = (EditText) view.findViewById(R.id.device_create_ip);
        displayName = (TextView) view.findViewById(R.id.device_create_display_name);
        deviceBuilding = (TextView) view.findViewById(R.id.device_create_building);
        deviceSpace = (TextView) view.findViewById(R.id.device_create_space);
        saveDevice = (Button) view.findViewById(R.id.device_create_save_button);
        mSpaceSpinner = (Spinner) view.findViewById(R.id.device_create_space_spinner);
    }

    private void initListeners() {
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
                if(!Utils.isEditTextEmpty(name) && mDevice != null){
                    mDevice.setName(name.getText().toString());
                    mDevice.setIp_address(ipAddress.getText().toString());
                    mDevice.setStatus(false);

                    createDevice(mAPI.getClient(), mDevice.deviceToString());
                    mMainActivity.onBackPressed();
                } else {
                    Toast.makeText(getActivity(), "Please, name your Space.", Toast.LENGTH_SHORT).show();
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
        Log.e("QUERY", ArgumentsKeys.DEVICE_QUERY);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.DEVICE_QUERY)
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
                    Log.e("ERROR", response.body().string());
                } else {
                    try{
                        JSONObject deviceData = new JSONObject(response.body().string());
                        mDeviceId = deviceData.getString("_id");
                        addSpaceToDevice(mAPI.getClient());
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }
            }
        });
    }

    private void addSpaceToDevice(OkHttpClient client) {
        Space selectedSpace = (Space) mSpaceSpinner.getSelectedItem();
        String query;
        if (!mSpaceId.isEmpty()) {
            query =  ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId + "/space/" + mSpaceId;
        } else {
            query =  ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId + "/space/" + selectedSpace.get_id();
        }

        Log.e("QUERY", query);
        RequestBody body = RequestBody.create(null, new byte[]{});
        Request request = new Request.Builder()
                .url(query)
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
                    Log.e("ERROR", response.body().string());
                } else {
                    addBuildingToDevice(mAPI.getClient());
                }
            }
        });
    }

    private void addBuildingToDevice(OkHttpClient client) {
        Space selectedSpace = (Space) mSpaceSpinner.getSelectedItem();
        String query;
        if (!mSpaceId.isEmpty()) {
            query =  ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId + "/building/" + mSpace.getBuilding().get_id();
        } else {
            query =  ArgumentsKeys.DEVICE_QUERY + "/" + mDeviceId + "/building/" + mBuildingId;
        }
        Log.e("QUERY", query);
        RequestBody body = RequestBody.create(null, new byte[]{});
        Request request = new Request.Builder()
                .url(query)
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
                    Log.e("ERROR", response.body().string());
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.onBackPressed();
                        }
                    });
                }
            }
        });
    }

    private void getSpace(OkHttpClient client) {
        Log.e("QUERY", ArgumentsKeys.SPACE_QUERY + "/" + mSpaceId);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.SPACE_QUERY + "/" + mSpaceId)
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
                    mSpace = mAPI.getSpace(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });
                }
            }
        });
    }

    public void setUpSpacesSpinner(List<Space> items) {
        mSpaceSpinnerAdapter = new SpaceSpinnerAdapter(getContext(), R.layout.spaces_item_spinner, items);
        mSpaceSpinner.setAdapter(mSpaceSpinnerAdapter);
    }

    private void getBuilding(OkHttpClient client) {
        Log.e("QUERY", ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId)
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
                    mBuilding = mAPI.getBuilding(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });
                }
            }
        });
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
