package com.example.martinjmartinez.proyectofinal.UI.Buildings.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Models.BuildingFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.BuildingService;
import com.example.martinjmartinez.proyectofinal.UI.Buildings.Adapters.BuildingListAdapter;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments.SpaceListFragment;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class BuildingListFragment extends Fragment {

    private List<Building> mBuildingList;
    private GridView mGridView;
    private Activity mActivity;
    private FloatingActionButton mAddBuildingButton;
    private LinearLayout mEmptyBuildingListLayout;
    private MainActivity mMainActivity;
    private BuildingService buildingService;
    private DatabaseReference databaseReference;
    private BuildingListAdapter buildingListAdapter;
    private ValueEventListener buildingsListener;

    public BuildingListFragment() {
    }

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

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;

    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Buildings");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initVariables(View view) {
        buildingService = new BuildingService(Realm.getDefaultInstance());
        mBuildingList = new ArrayList<>();
        mGridView = (GridView) view.findViewById(R.id.building_grid);
        mEmptyBuildingListLayout = (LinearLayout) view.findViewById(R.id.empty_building_list_layout);
        mAddBuildingButton = (FloatingActionButton) view.findViewById(R.id.add_building_button);
        databaseReference = FirebaseDatabase.getInstance().getReference("Buildings");
    }

    private void initListeners() {
        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mBuildingList.isEmpty()) {
                    Building buildingSelected = mBuildingList.get(position);

                    SpaceListFragment spaceListFragment = new SpaceListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUILDING_ID, buildingSelected.get_id());
                    spaceListFragment.setArguments(bundle);
                    Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT), spaceListFragment, FragmentKeys.SPACE_LIST_FRAGMENT, true);

                }
            }
        });

        mAddBuildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildingCreateFragment buildingCreateFragment = new BuildingCreateFragment();
                boolean addToBackStack = buildingService.allBuildings().size() == 0 ? false : true;
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.BUILDING_LIST_FRAGMENT), buildingCreateFragment, FragmentKeys.BUILDING_CREATION_FRAGMENT, addToBackStack);
            }
        });

        buildingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    BuildingFB buildingFB = dataSnapshot1.getValue(BuildingFB.class);

                    buildingService.updateOrCreate(buildingFB);
                }

                getBuildings();
                buildingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(buildingsListener);
    }

    public void getBuildings() {
        mBuildingList = buildingService.allActiveBuildings();

        shouldEmptyMessageShow(mBuildingList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        databaseReference.removeEventListener(buildingsListener);
    }

    private void shouldEmptyMessageShow(List<Building> buildingList) {
        if (!buildingList.isEmpty()) {
            mEmptyBuildingListLayout.setVisibility(View.GONE);
            initBuildingList(buildingList);
        } else {
            mEmptyBuildingListLayout.setVisibility(View.VISIBLE);
            buildingListAdapter = new BuildingListAdapter(getContext(), R.layout.building_list_item, buildingList);
            mGridView.setVisibility(View.GONE);
        }
    }

    void initBuildingList(List<Building> buildingsList) {
        buildingListAdapter = new BuildingListAdapter(getContext(), R.layout.building_list_item, buildingsList);
        mGridView.setAdapter(buildingListAdapter);
    }
}
