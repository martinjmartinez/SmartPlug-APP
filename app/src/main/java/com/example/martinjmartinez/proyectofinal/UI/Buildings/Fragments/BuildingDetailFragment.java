package com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/15/2017.
 */

public class BuildingDetailFragment extends Fragment {

    private Building mBuilding;
    private API mAPI;
    private Activity mActivity;
    private String mBuildingId;
    private TextView mNameTextView;
    private TextView mSpacesTextView;
    private TextView mDevicesTextView;
    private Button mEditButton;
    private Button mDeleteButton;
    private TextView mPowerTextView;
    private TextView mInfoTextView;
    private MainActivity mMainActivity;
    private BuildingService buildingService;


    public BuildingDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString(ArgumentsKeys.BUILDING_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_fragment, container, false);

        iniVariables(view);
        getBuilding();
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

        mMainActivity.getSupportActionBar().setTitle("Building Details");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        buildingService = new BuildingService(Realm.getDefaultInstance());
        mBuilding = new Building();
        mAPI = new API();
        mNameTextView = (TextView) view.findViewById(R.id.building_detail_name);
        mSpacesTextView = (TextView) view.findViewById(R.id.building_detail_spaces);
        mEditButton = (Button) view.findViewById(R.id.building_detail_update);
        mDeleteButton = (Button) view.findViewById(R.id.building_detail_delete);
        mPowerTextView = (TextView) view.findViewById(R.id.building_detail_power);
        mDevicesTextView = (TextView) view.findViewById(R.id.building_detail_devices);
        mInfoTextView = (TextView) view.findViewById(R.id.building_detail_delete_info);
    }

    private void initListeners() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildingUpdateFragment buildingUpdateFragment = new BuildingUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentsKeys.BUILDING_ID, mBuilding.get_id());
                buildingUpdateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_DETAIL_FRAGMENT),
                        buildingUpdateFragment, FragmentKeys.BUILDING_UPDATE_FRAGMENT, true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Delete Building", "Do you want to delete this Building?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBuilding(mAPI.getClient());
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

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

    }

    private void deleteBuilding(OkHttpClient client) {
        Log.e("QUERY", ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId)
                .delete()
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

    private void getBuilding() {
        mBuilding = buildingService.getBuildingById(mBuildingId);

        initView(mBuilding);
    }

    private void initView(Building building) {

        mNameTextView.setText(building.getName());

        if (building.getSpaces() != null) {
            mSpacesTextView.setText(building.getSpaces().size() + "");
            int devices = getBuildingDevices();
            mDevicesTextView.setText(devices + "");
            if (devices > 0) {
                mDeleteButton.setClickable(false);
                mInfoTextView.setVisibility(View.VISIBLE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.disabled));
            } else {
                mDeleteButton.setClickable(true);
                mInfoTextView.setVisibility(View.GONE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.alert));
            }
        }
    }

    private int getBuildingDevices() {
        int total = 0;
        for (Space space : mBuilding.getSpaces()) {
            total = space.getDevices().size() + total;
        }
        return total;
    }
}
