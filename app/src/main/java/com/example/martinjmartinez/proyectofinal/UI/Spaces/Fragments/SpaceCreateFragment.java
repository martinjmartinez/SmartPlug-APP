package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinjmartinez.proyectofinal.Entities.Building;
import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by MartinJMartinez on 7/16/2017.
 */

public class SpaceCreateFragment extends Fragment {

    private Space mSpace;
    private API mAPI;
    private Activity mActivity;
    private String mQuery;
    private EditText name;
    private TextView displayName;
    private TextView spaceBuilding;
    private Button saveSpace;
    private String mBuildingId, mSpaceId;
    private Building mBuilding;

    public SpaceCreateFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mQuery = bundle.getString("QUERY", "http://192.168.1.17:3000/spaces");
            mBuildingId = bundle.getString("BUILDING_ID", "");
        } else {
            mQuery = "http://192.168.1.17:3000/spaces";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_creation_fragment, container, false);

        iniVariables();
        initView(view);
        initListeners();

        return view;
    }

    private void iniVariables() {
        mSpace = new Space();
        mActivity = getActivity();
        mAPI =  new API();
    }

    private void initListeners() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayName.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayName.setText(s.toString());
            }
        });

        saveSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isEditTextEmpty(name) && mSpace != null){
                    mSpace.setName(name.getText().toString());
                    createSpace(mAPI.getClient(),mSpace.toString());
                } else {
                    Toast.makeText(getActivity(), "Please, name your Space.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createSpace(OkHttpClient client, String data) {
        Log.e("QUERY", mQuery);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(mQuery)
                .post(body)
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
                    try{
                        JSONObject spaceData = new JSONObject(response.body().string());
                        mSpaceId = spaceData.getString("_id");
                        addSpaceToBuilding(mAPI.getClient());
                    } catch (JSONException e) {
                        Log.e("ERRROR", e.getMessage());
                    }
                }
            }
        });
    }

    private void addSpaceToBuilding(OkHttpClient client) {
        Log.e("QUERY", "http://192.168.1.17:3000/buildings/" + mBuildingId + "/space/" + mSpaceId);
        RequestBody body = RequestBody.create(null, new byte[]{});
        Request request = new Request.Builder()
                .url("http://192.168.1.17:3000/buildings/" + mBuildingId + "/space/" + mSpaceId)
                .post(body)
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


    private void initView(View view) {
        name = (EditText) view.findViewById(R.id.space_create_name);
        displayName = (TextView) view.findViewById(R.id.space_create_display_name);
        spaceBuilding = (TextView) view.findViewById(R.id.space_create_building);
        saveSpace = (Button) view.findViewById(R.id.space_create_save_button);
        getBuilding(mAPI.getClient());
    }

    private void getBuilding(OkHttpClient client) {
        Request request = new Request.Builder()
                .url("http://192.168.1.17:3000/buildings/" + mBuildingId)
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
                    mBuilding = mAPI.getBuilding(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spaceBuilding.setText(mBuilding.getName());
                        }
                    });
                }
            }
        });
    }
}
