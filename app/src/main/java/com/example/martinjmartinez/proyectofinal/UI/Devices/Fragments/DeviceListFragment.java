package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.DeviceListAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.API;
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

    private List<Device> mDevicesList;
    private GridView mGridView;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;

    public DeviceListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mQuery = bundle.getString("QUERY", "http://192.168.1.17:3000/devices");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list_fragment, container, false);


        iniVariables(view);
        getDevices(mAPI.getClient());
        initListeners();

        return view;
    }

    private void iniVariables(View view) {
        mDevicesList =  new ArrayList<>();
        mGridView = (GridView) view.findViewById(R.id.devices_grid);
        mActivity = getActivity();
        mAPI =  new API();
    }

    private void initListeners() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mDevicesList.isEmpty()) {
                    Device deviceSelected;
                    deviceSelected = mDevicesList.get(position);
                    DeviceDetailFragment deviceDetailFragment = new DeviceDetailFragment();
                    Bundle bundle =  new Bundle();
                    bundle.putString("QUERY", "http://192.168.1.17:3000/devices/" + deviceSelected.get_id());
                    deviceDetailFragment.setArguments(bundle);
                    Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_LIST_FRAGMENT), deviceDetailFragment, FragmentKeys.DEVICE_DETAIL_FRAGMENT, true);
                }
            }
        });
    }
    private void getDevices(OkHttpClient client) {
        //Log.e("QUERY", mQuery);
        if(mQuery == null) {
            mQuery = "http://192.168.1.17:3000/devices";
        }
        Request request = new Request.Builder()
                .url(mQuery)
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
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDevicesList = mAPI.getDeviceList(response);
                            initDevicesList(mDevicesList);
                        }
                    });
                }
            }
        });
    }

    private void initDevicesList(List<Device> devicesList) {
        DeviceListAdapter deviceListAdapter = new DeviceListAdapter(getContext(), R.layout.device_list_item, devicesList);
        mGridView.setAdapter(deviceListAdapter);
    }
}
