package com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Entities.Device;
import com.example.martinjmartinez.proyectofinal.Models.DeviceFB;
import com.example.martinjmartinez.proyectofinal.Models.WifiConnection;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.DeviceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Adapters.WifiListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DevicePairFragment extends Fragment {

    private String mDeviceId, mBuildingId, mSpaceId;
    private Activity mActivity;
    private MainActivity mMainActivity;
    private DeviceService deviceService;
    private DeviceFB mDeviceFB;
    private Realm realm;
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private String deviceSSID;
    private ListView wifiListView;
    private boolean connecting = false;
    private boolean isNewDevice = false;
    private WifiConnection newConnection;
    private WifiConnection actualConnection;
    private WifiListAdapter wifiListAdapter;
    private ArrayList<WifiConnection> connections;
    private Button connectButton;
    private Button pairButton;
    private TextView wifiNameTextView;
    private TextView listStatusTextView;
    private TextView actionTextView;
    private Context context;
    private Device mDevice;
    private ProgressBar progressBar;
    private TextView emtyList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String currentUserId;
    private Handler handler;
    private Handler handler2;
    private TextInputEditText wifiPassword;
    private TextInputLayout etPasswordLayout;
    private ValueEventListener deviceListener;

    public DevicePairFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mDeviceId = bundle != null ? bundle.getString(Constants.DEVICE_ID, "") : "";
        mBuildingId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
        mSpaceId = bundle != null ? bundle.getString(Constants.SPACE_ID, "") : "";
        isNewDevice = bundle != null ? bundle.getBoolean(Constants.IS_NEW_DEVICE, false) : false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_pair_fargment, container, false);

        iniVariables(view);
        initListeners();
        initView();
        isWifiOn();
        initListAdapter(connections);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;
    }

    @Override
    public void onPause() {
        super.onPause();

        databaseReference.removeEventListener(deviceListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainActivity.getSupportActionBar().setTitle("Pairing Device");
        databaseReference.addValueEventListener(deviceListener);
        mActivity.registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }
    }

    private void iniVariables(View view) {
        connectButton = view.findViewById(R.id.connect_to_device_buttom);
        wifiNameTextView = view.findViewById(R.id.wifi_ssid);
        emtyList = view.findViewById(R.id.empty_wifi_list_text);
        actionTextView = view.findViewById(R.id.network_action);
        listStatusTextView = view.findViewById(R.id.list_status);
        wifiPassword = view.findViewById(R.id.wifi_password_editview);
        pairButton = view.findViewById(R.id.pair_to_device_buttom);
        progressBar = view.findViewById(R.id.loading_wifi_list);
        wifiListView = view.findViewById(R.id.wifiListView);
        etPasswordLayout = view.findViewById(R.id.etPasswordLayout);

        realm = Realm.getDefaultInstance();
        deviceService = new DeviceService(realm);
        mainWifi = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        connections = new ArrayList<>();
        context = getContext();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUserId + "/Devices");
        actualConnection = new WifiConnection(mainWifi.getConnectionInfo().getSSID(), 0, "");

        if (!mDeviceId.isEmpty()) {
            mDevice = deviceService.getDeviceById(mDeviceId);
        }
    }

    private void initView() {
        setLoadingState(true);
        listStatusTextView.setText(R.string.searching_devices);
        pairButton.setEnabled(false);
        pairButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.disabled));
    }

    private void isWifiOn() {
        mActivity.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (!mainWifi.isWifiEnabled()) {
            mainWifi.setWifiEnabled(true);
        }

        doInback();
    }

    private void setSetUpView() {
        setLoadingState(false);
        wifiNameTextView.setVisibility(View.VISIBLE);
        actionTextView.setVisibility(View.VISIBLE);
        listStatusTextView.setVisibility(View.VISIBLE);
        wifiListView.setVisibility(View.VISIBLE);
        actionTextView.setText(R.string.pair_to);
        pairButton.setVisibility(View.VISIBLE);
        listStatusTextView.setText(R.string.devices_found);
        wifiListView.setEmptyView(emtyList);
    }

    private void initListAdapter(ArrayList<WifiConnection> wifis) {
        wifiListAdapter = new WifiListAdapter(context, wifis);
        wifiListView.setAdapter(wifiListAdapter);
    }

    public void doInback() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                int permissionCheck = ContextCompat.checkSelfPermission(mMainActivity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mMainActivity,
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }

                mainWifi = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mainWifi.startScan();

                doInback();
            }
        }, 1000);
    }

    private void setLoadingState(boolean state) {
        if (state) {
            wifiListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            wifiListView.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        deviceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeviceFB deviceFB = dataSnapshot1.getValue(DeviceFB.class);

                    if (deviceFB.get_id().equals(mDeviceId)) {
                        mDeviceFB = deviceFB;
                        if (mDeviceFB.isConnected()) {
                            mActivity.unregisterReceiver(receiverWifi);
                            deviceService.updateDeviceLocal(deviceFB);
                            connecting = false;
                            Bundle bundle = new Bundle();

                            bundle.putString(Constants.BUILDING_ID, mBuildingId);
                            bundle.putString(Constants.SPACE_ID, mSpaceId);
                            bundle.putString(Constants.DEVICE_ID, mDeviceId);

                            handler.removeCallbacksAndMessages(null);

                            if (isNewDevice) {
                                DeviceCreateFragment deviceCreateFragment = new DeviceCreateFragment();
                                deviceCreateFragment.setArguments(bundle);
                                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.DEVICE_PAIR_FRAGMENT), deviceCreateFragment, FragmentKeys.DEVICE_CREATION_FRAGMENT, false);
                            } else {
                                mActivity.onBackPressed();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", databaseError.toString() + "   gg");
            }
        };

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                newConnection = connections.get(i);
                wifiNameTextView.setText(newConnection.getSSID());
                if (newConnection.getType().equals("WPA") || newConnection.getType().equals("WEP")) {
                    wifiPassword.setVisibility(View.VISIBLE);
                    etPasswordLayout.setVisibility(View.VISIBLE);
                } else {
                    wifiPassword.setVisibility(View.GONE);
                    etPasswordLayout.setVisibility(View.GONE);
                }

                pairButton.setEnabled(true);
                connectButton.setEnabled(true);
                pairButton.setBackgroundColor(ContextCompat.getColor(context, R.color.color4));
                connectButton.setBackgroundColor(ContextCompat.getColor(context, R.color.color4));
            }
        });

        pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newConnection != null) {
                    if (!isNewDevice) {
                        deviceService.updateDeviceReset(mDeviceId, false);
                    }
                    setLoadingState(true);
                    pairButton.setEnabled(false);
                    actionTextView.setText("");
                    pairButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled));
                    connectToDevice();
                }
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newConnection != null) {
                    if (!wifiPassword.getText().toString().isEmpty()) {
                        if (isNewDevice) {
                            DeviceFB deviceFB = new DeviceFB(false, false, true, mBuildingId, mSpaceId, deviceSSID, false);
                            mDeviceId = deviceService.createDeviceCloud(deviceFB);
                        }else {
                            deviceService.updateDeviceReset(mDeviceId, false);
                        }

                        sendCredentialsToArduino(new OkHttpClient(), newConnection.getSSID(), wifiPassword.getText().toString());
                    } else {
                        Toast.makeText(mActivity.getApplicationContext(), "Please, enter the password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void sendCredentialsToArduino(final OkHttpClient client, String ssid, String password) {
        Request requestAction = new Request.Builder()
                .url("http://" + mainWifi.getConnectionInfo().getIpAddress() + "/wifisave?s=" + ssid + "&p=" + password + "&device=" + mDeviceId + "&user=" + currentUserId)
                .build();

        client.newCall(requestAction).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity.getApplicationContext(), "Couldn't connect to the device, Reset it and try again", Toast.LENGTH_LONG).show();
                    }
                });

                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity.getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                    throw new IOException("sendHistoryIdToArduino" + response);
                } else {
                    Log.e("sendCredentials", "id sent to arduino");

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connecting = true;
                            setLoadingState(true);
                            listStatusTextView.setText(R.string.device_is_connectiong);
                            connectButton.setVisibility(View.GONE);
                            wifiNameTextView.setVisibility(View.GONE);
                            actionTextView.setVisibility(View.GONE);
                            wifiListView.setVisibility(View.GONE);
                            wifiPassword.setVisibility(View.GONE);
                            etPasswordLayout.setVisibility(View.GONE);

                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mainWifi.getConnectionInfo().getSSID().contains(deviceSSID)) {
                                        Toast.makeText(mActivity.getApplicationContext(), "Couldn't connect device to wifi", Toast.LENGTH_LONG).show();
                                        connecting = false;
                                    } else {
                                        if (deviceSSID == null) {
                                            deviceSSID = mainWifi.getConnectionInfo().getSSID();
                                        }
                                        for (WifiConnection wifiConnection : connections) {
                                            if (wifiConnection.getSSID().contains(deviceSSID)) {
                                                Toast.makeText(mActivity.getApplicationContext(), "Couldn't connect device to wifi", Toast.LENGTH_LONG).show();
                                                connecting = false;
                                            }
                                        }
                                    }
                                }
                            }, 30000);
                        }
                    });
                }
            }

        });
    }

    private void connectToDevice() {
        WifiConfiguration conf = new WifiConfiguration();
        deviceSSID = newConnection.getSSID();
        conf.SSID = "\"" + newConnection.getSSID() + "\"";

        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + newConnection.getSSID() + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
        newConnection = null;
        wifiNameTextView.setText("");
        connectButton.setEnabled(false);
        connectButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled));
    }

    private void setWifiSetUp() {
        wifiListView.setVisibility(View.VISIBLE);
        actionTextView.setVisibility(View.VISIBLE);
        listStatusTextView.setVisibility(View.VISIBLE);
        connectButton.setVisibility(View.VISIBLE);


        pairButton.setVisibility(View.GONE);
        wifiListView.setVisibility(View.VISIBLE);
        wifiNameTextView.setVisibility(View.VISIBLE);
        actionTextView.setText(R.string.connect_to);
        listStatusTextView.setText("Networks found");
        connectButton.setVisibility(View.VISIBLE);
        setLoadingState(false);
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            String currentSSID = mainWifi.getConnectionInfo().getSSID();
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();

            List<ScanResult> wifiList;
            connections.clear();
            wifiList = mainWifi.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {
                if (currentSSID.contains("SmartPlug")) {
                    if (!wifiList.get(i).SSID.contains("SmartPlug-")) {
                        connections.add(new WifiConnection(wifiList.get(i).SSID, wifiList.get(i).level, wifiList.get(i).capabilities));
                    }
                } else {
                    if (isNewDevice) {
                        if (wifiList.get(i).SSID.contains("SmartPlug-")) {
                            connections.add(new WifiConnection(wifiList.get(i).SSID, wifiList.get(i).level, wifiList.get(i).capabilities));
                        }
                    } else {
                        if (wifiList.get(i).SSID.contains(mDevice.getSsid())) {
                            connections.add(new WifiConnection(wifiList.get(i).SSID, wifiList.get(i).level, wifiList.get(i).capabilities));
                        }
                    }

                }
            }

            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (!connecting) {
                    if (currentSSID.contains("SmartPlug")) {
                        setWifiSetUp();
                    } else {
                        if (!isNewDevice && wifiList.isEmpty()) {
                            handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setSetUpView();
                                    handler2.removeCallbacksAndMessages(null);
                                }
                            }, 20000);
                        } else {
                            setSetUpView();
                        }
                    }
                    wifiListAdapter.notifyDataSetChanged();
                }
            } else

            {
                listStatusTextView.setText(R.string.searching_wifi);
                setLoadingState(true);
            }
        }
    }
}

