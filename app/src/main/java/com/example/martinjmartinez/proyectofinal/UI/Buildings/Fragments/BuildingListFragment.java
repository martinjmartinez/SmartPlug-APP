package com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters.BuildingListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceListFragment;
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
 * Created by MartinJMartinez on 7/15/2017.
 */

public class BuildingListFragment extends Fragment {

    private List<Building> mBuildingList;
    private GridView mGridView;
    private API mAPI;
    private Activity mActivity;
    private FloatingActionButton mAddBuildingButton;
    private LinearLayout mEmptyBuildingListLayout;

    public BuildingListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.building_list_fragment, container, false);

        Utils.setActionBarIcon(getActivity(), true);
        initVariables(view);
        initListeners();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity)getActivity()).toggleDrawerIcon(0, null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBuildings(mAPI.getClient());
    }

    private void initVariables(View view) {
        mBuildingList = new ArrayList<>();
        mActivity = getActivity();
        mGridView = (GridView) view.findViewById(R.id.building_grid);
        mAPI =  new API();
        mEmptyBuildingListLayout = (LinearLayout) view.findViewById(R.id.empty_building_list_layout);
        mAddBuildingButton = (FloatingActionButton) view.findViewById(R.id.add_building_button);

    }

    private void initListeners() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mBuildingList.isEmpty()) {
                    Building buildingSelected = mBuildingList.get(position);

                    SpaceListFragment spaceListFragment = new SpaceListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ArgumentsKeys.BUILDING_ID, buildingSelected.get_id());
                    spaceListFragment.setArguments(bundle);
                    Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT), spaceListFragment, FragmentKeys.SPACE_LIST_FRAGMENT, true);

                }
            }
        });

        mAddBuildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildingCreateFragment buildingCreateFragment = new BuildingCreateFragment();
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT), buildingCreateFragment, FragmentKeys.BUILDING_CREATION_FRAGMENT, true);

            }
        });
    }

    public void getBuildings(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(ArgumentsKeys.BUILDING_QUERY)
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
                    mBuildingList = mAPI.getBuildingList(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mBuildingList.isEmpty()) {
                                initSpacesList(mBuildingList);
                                mEmptyBuildingListLayout.setVisibility(View.GONE);
                            } else {
                                mGridView.setVisibility(View.GONE);
                                mEmptyBuildingListLayout.setVisibility(View.VISIBLE);
                            }
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
