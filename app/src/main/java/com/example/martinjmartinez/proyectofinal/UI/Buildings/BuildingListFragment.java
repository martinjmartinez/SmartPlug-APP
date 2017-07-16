package com.example.martinjmartinez.proyectofinal.UI.Buildings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Devices.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.SpaceListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.SpaceListFragment;
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
 * Created by MartinJMartinez on 7/15/2017.
 */

public class BuildingListFragment extends Fragment {

    private List<Building> mBuildingList;
    private GridView mGridView;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;

    public BuildingListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_list_fragment, container, false);

        initVariables(view);
        getBuildings(mAPI.getClient());
        initListeners();

        return view;
    }

    private void initVariables(View view) {
        mBuildingList = new ArrayList<>();
        mActivity = getActivity();
        mGridView = (GridView) view.findViewById(R.id.building_grid);
        mAPI =  new API();
    }

    private void initListeners() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!mBuildingList.isEmpty()) {
                    Building buildingSelected = mBuildingList.get(position);
                    if (buildingSelected.getSpaces() != null && !buildingSelected.getSpaces().isEmpty()) {
                        SpaceListFragment spaceListFragment = new SpaceListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("QUERY", "http://192.168.1.17:3000/buildings/" + buildingSelected.get_id() + "/spaces");
                        spaceListFragment.setArguments(bundle);
                        Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT), spaceListFragment, FragmentKeys.SPACE_LIST_FRAGMENT);
                    }
                }
            }
        });
    }

    public void getBuildings(OkHttpClient client) {
        Request request = new Request.Builder()
                .url("http://192.168.1.17:3000/buildings")
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
                            mBuildingList = mAPI.getBuildingList(response);
                            initSpacesList(mBuildingList);
                        }
                    });
                }
            }
        });
    }

    void initSpacesList(List<Building> buildingsList) {
        BuildingListAdapter buildingListAdapter = new BuildingListAdapter(getContext(), R.layout.building_list_item, buildingsList);
        mGridView.setAdapter(buildingListAdapter);
    }
}
