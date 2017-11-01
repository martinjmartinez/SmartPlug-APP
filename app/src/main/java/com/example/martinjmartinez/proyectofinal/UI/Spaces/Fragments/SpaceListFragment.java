package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

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

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceListAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SpaceListFragment extends Fragment {

    private List<Space> mSpacesList;
    private GridView mGridView;
    private LinearLayout mEmptySpaceListLayout;
    private FloatingActionButton mAddSpaceButton;
    private Activity mActivity;
    private String mBuildingId;
    private MainActivity mMainActivity;
    private SpaceService spaceService;
    private DatabaseReference databaseReference;
    private SpaceListAdapter spaceListAdapter;

    public SpaceListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getArgumentsBundle();
    }

    public void getArgumentsBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mBuildingId = bundle.getString(Constants.BUILDING_ID, "");
        } else {
            mBuildingId = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_list_fragment, container, false);

        initVariables(view);
        getSpaces();
        initListeners();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArgumentsBundle();

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
        mMainActivity.getSupportActionBar().setTitle("Spaces");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void initVariables(View view) {
        spaceService = new SpaceService(Realm.getDefaultInstance());
        mGridView = (GridView) view.findViewById(R.id.spaces_grid);
        mEmptySpaceListLayout = (LinearLayout) view.findViewById(R.id.empty_space_list_layout);
        mAddSpaceButton = (FloatingActionButton) view.findViewById(R.id.add_space_button);
        databaseReference = FirebaseDatabase.getInstance().getReference("Spaces");
        mSpacesList = new ArrayList<>();
    }

    private void initListeners() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mSpacesList.isEmpty()) {
                    Space spaceSelected = mSpacesList.get(position);

                    DeviceListFragment deviceListFragment = new DeviceListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.SPACE_ID, spaceSelected.get_id());
                    deviceListFragment.setArguments(bundle);
                    Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT), deviceListFragment, FragmentKeys.DEVICE_LIST_FRAGMENT, true);
                }
            }
        });

        mAddSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpaceCreateFragment spaceCreateFragment = new SpaceCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUILDING_ID, mBuildingId);
                spaceCreateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT), spaceCreateFragment, FragmentKeys.SPACE_CREATION_FRAGMENT, true);
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        databaseReference.orderByChild("buildingId").equalTo(mBuildingId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SpaceFB spaceFB = dataSnapshot.getValue(SpaceFB.class);

                spaceService.updateSpace(spaceFB);
                if (spaceListAdapter != null) {
                    spaceListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                SpaceFB spaceFB = dataSnapshot.getValue(SpaceFB.class);

                spaceService.updateSpace(spaceFB);
                if (spaceListAdapter != null) {
                    spaceListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSpaces() {
        mSpacesList = spaceService.allActiveSpacesByBuilding(mBuildingId);

        shouldEmptyMessageShow(mSpacesList);
    }
    private void shouldEmptyMessageShow(List<Space> spaceList) {
        if (!spaceList.isEmpty()) {
            mEmptySpaceListLayout.setVisibility(View.GONE);
            initSpacesList(spaceList);
        } else {
            mGridView.setVisibility(View.GONE);
            mEmptySpaceListLayout.setVisibility(View.VISIBLE);
            spaceListAdapter = new SpaceListAdapter(getContext(), R.layout.space_list_item, spaceList);
        }
    }

    void initSpacesList(List<Space> spacesList) {
        spaceListAdapter = new SpaceListAdapter(getContext(), R.layout.space_list_item, spacesList);
        mGridView.setAdapter(spaceListAdapter);
    }
}
