package com.example.martinjmartinez.proyectofinal.UI.Spaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.API;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/15/2017.
 */

public class SpaceDetailFragment extends Fragment{

    private Space mSpace;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;

    public SpaceDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mQuery = bundle.getString("QUERY", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_fragment, container, false);

        iniVariables();
        getSpace(mAPI.getClient(), view);

        return view;
    }

    private void iniVariables() {
        mSpace = new Space();
        mActivity = getActivity();
        mAPI =  new API();
    }

    private void initListeners() {

    }

    private void getSpace(OkHttpClient client, final View view) {
        Log.e("QUERY", mQuery);
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
                            mSpace = mAPI.getSpace(response);
                            initSpaceView(mSpace, view);
                        }
                    });
                }
            }
        });
    }

    private void initSpaceView( Space space, View view) {

        TextView name = (TextView) view.findViewById(R.id.space_detail_name);
        TextView devices = (TextView) view.findViewById(R.id.space_detail_devices);
        TextView building = (TextView) view.findViewById(R.id.space_detail_building);
        TextView power = (TextView) view.findViewById(R.id.space_detail_power);

        name.setText(space.getName());

        if (space.getDevices() != null) {
            devices.setText(space.getDevices().size() + "");

            if (space.getBuilding() != null) {
                building.setText(space.getBuilding().getName());
            }
        }
        //power.setText();
    }
}
