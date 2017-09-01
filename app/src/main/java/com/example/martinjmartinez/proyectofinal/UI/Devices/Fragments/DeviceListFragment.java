package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/14/2017.
 */

public class DeviceListFragment extends Fragment{

    private RecyclerView mGridView;
    private API mAPI;
    private Building mBuilding;
    private String mBuildingId, mSpaceId;
    private FloatingActionButton mAddDeviceButton;
    private Activity mActivity;
    private Space mSpace;
    private List<Device> mDevicesList;
    private MainActivity mMainActivity;

    public DeviceListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getArgumentsBundle();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1){
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    public void getArgumentsBundle() {
        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString(ArgumentsKeys.BUILDING_ID, "") : "";
        mSpaceId = bundle != null ? bundle.getString(ArgumentsKeys.SPACE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list_fragment, container, false);


        iniVariables(view);
        if (!mBuildingId.isEmpty() && mSpaceId.isEmpty()) {
            getDevicesByBuilding(mAPI.getClient());
        } else if (mBuildingId.isEmpty() && !mSpaceId.isEmpty()) {
            getDevicesBySpace(mAPI.getClient());
        }
        initListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Devices");
    }

    private void iniVariables(View view) {
        mDevicesList =  new ArrayList<>();
        mGridView = (RecyclerView) view.findViewById(R.id.devices_grid);
        mAPI =  new API();
        mAddDeviceButton = (FloatingActionButton) view.findViewById(R.id.add_device_button);
    }

    private void initListeners() {
        mAddDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceCreateFragment deviceCreateFragment = new DeviceCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentsKeys.BUILDING_ID, mBuildingId);
                bundle.putString(ArgumentsKeys.SPACE_ID, mSpaceId);
                deviceCreateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT), deviceCreateFragment, FragmentKeys.DEVICE_DETAIL_FRAGMENT, true);
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }
    private void getDevicesByBuilding(OkHttpClient client) {
        Log.e("QUERY", ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId + "/devices");
        Request request = new Request.Builder()
                .url(ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId + "/devices")
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
                    mDevicesList = mAPI.getDeviceList(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            initDevicesList(mDevicesList);
                        }
                    });
                }
            }
        });
    }

    private void getDevicesBySpace(OkHttpClient client) {
        Log.e("QUERY", ArgumentsKeys.SPACE_QUERY + "/" + mSpaceId +"/devices");
        Request request = new Request.Builder()
                .url(ArgumentsKeys.SPACE_QUERY + "/" + mSpaceId +"/devices")
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
                    mDevicesList = mAPI.getDeviceList(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            initDevicesList(mDevicesList);
                        }
                    });
                }
            }
        });
    }

    private void initDevicesList(List<Device> devicesList) {
        mGridView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        DeviceListAdapter mDevicesListAdapter = new DeviceListAdapter(devicesList);
        mGridView.setAdapter(mDevicesListAdapter);
    }
}
