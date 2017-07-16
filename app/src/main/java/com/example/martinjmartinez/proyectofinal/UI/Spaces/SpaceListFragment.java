package com.example.martinjmartinez.proyectofinal.UI.Spaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.Devices.DeviceListFragment;
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
 * Created by MartinJMartinez on 7/13/2017.
 */

public class SpaceListFragment extends Fragment {

    private List<Space> mSpacesList;
    private GridView mGridView;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;

    public SpaceListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_list_fragment, container, false);

        initVariables(view);
        getSpaces(mAPI.getClient());
        initListeners();

        return view;
    }

    private void initVariables(View view) {
        mSpacesList = new ArrayList<>();
        mActivity = getActivity();
        mGridView = (GridView) view.findViewById(R.id.spaces_grid);
        mAPI =  new API();
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
                        bundle.putString("QUERY", "http://192.168.1.17:3000/spaces/" + spaceSelected.get_id() + "/devices");
                        deviceListFragment.setArguments(bundle);
                        Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_LIST_FRAGMENT), deviceListFragment, FragmentKeys.DEVICE_LIST_FRAGMENT);
                    }
                }
            }
        });
    }

    public void getSpaces(OkHttpClient client) {
        Request request = new Request.Builder()
                .url("http://192.168.1.17:3000/spaces")
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
                            mSpacesList = mAPI.getSpaceList(response);
                            initSpacesList(mSpacesList);
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
