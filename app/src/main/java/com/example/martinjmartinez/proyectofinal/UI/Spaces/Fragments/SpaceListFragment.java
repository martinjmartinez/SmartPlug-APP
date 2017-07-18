package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

import android.app.Activity;
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

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Devices.Fragments.DeviceListFragment;
import com.example.martinjmartinez.proyectofinal.UI.Spaces.Adapters.SpaceListAdapter;
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
 * Created by MartinJMartinez on 7/13/2017.
 */

public class SpaceListFragment extends Fragment {

    private List<Space> mSpacesList;
    private GridView mGridView;
    private LinearLayout mEmptySpaceListLayout;
    private FloatingActionButton mAddSpaceButton;
    private API mAPI;
    private Activity mActivity;
    private String mBuildingId;

    public SpaceListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       getArgumentsBundle();
    }

    public void getArgumentsBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mBuildingId = bundle.getString(ArgumentsKeys.BUILDING_ID, "");
        } else {
            mBuildingId = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_list_fragment, container, false);

        initVariables(view);
        initListeners();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArgumentsBundle();
        getSpaces(mAPI.getClient());
    }

    private void initVariables(View view) {
        mSpacesList = new ArrayList<>();
        mActivity = getActivity();
        mGridView = (GridView) view.findViewById(R.id.spaces_grid);
        mAPI =  new API();
        mEmptySpaceListLayout = (LinearLayout) view.findViewById(R.id.empty_space_list_layout);
        mAddSpaceButton = (FloatingActionButton) view.findViewById(R.id.add_space_button);
    }

    private void initListeners() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!mSpacesList.isEmpty()) {
                    Space spaceSelected = mSpacesList.get(position);
                    if (spaceSelected.getDevices() != null && !spaceSelected.getDevices().isEmpty()) {
                        DeviceListFragment deviceListFragment = new DeviceListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentsKeys.QUERY, ArgumentsKeys.SPACE_QUERY +"/" + spaceSelected.get_id() + "/devices");
                        deviceListFragment.setArguments(bundle);
                        Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT), deviceListFragment, FragmentKeys.DEVICE_LIST_FRAGMENT, true);
                    }
                }
            }
        });

        mAddSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpaceCreateFragment spaceCreateFragment = new SpaceCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentsKeys.BUILDING_ID, mBuildingId);
                spaceCreateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT), spaceCreateFragment, FragmentKeys.SPACE_CREATION_FRAGMENT, true);
            }
        });
    }

    public void getSpaces(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(ArgumentsKeys.BUILDING_QUERY + "/" + mBuildingId + "/spaces")
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
                    mSpacesList = mAPI.getSpaceList(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mSpacesList.isEmpty()) {
                                mEmptySpaceListLayout.setVisibility(View.GONE);
                                initSpacesList(mSpacesList);
                            } else {
                                mGridView.setVisibility(View.GONE);
                                mEmptySpaceListLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    void initSpacesList(List<Space> spacesList) {
        SpaceListAdapter spaceListAdapter = new SpaceListAdapter(getContext(), R.layout.space_list_item, spacesList);
        mGridView.setAdapter(spaceListAdapter);
    }
}
