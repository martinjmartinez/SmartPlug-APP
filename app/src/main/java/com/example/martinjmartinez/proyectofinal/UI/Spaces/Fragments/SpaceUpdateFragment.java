package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

import android.app.Activity;
import android.content.Context;
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
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.ArgumentsKeys;
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
 * Created by MartinJMartinez on 7/17/2017.
 */

public class SpaceUpdateFragment extends Fragment {

    private Space mSpace;
    private API mAPI;
    private Activity mActivity;
    private EditText name;
    private TextView displayName;
    private TextView spaceBuilding;
    private Button saveSpace;
    private String mSpaceId;
    private Building mBuilding;
    private MainActivity mMainActivity;

    public SpaceUpdateFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mSpaceId = bundle != null ? bundle.getString(ArgumentsKeys.SPACE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_creation_fragment, container, false);

        iniVariables(view);
        getSpace(mAPI.getClient());
        initListeners();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1){
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        mActivity = getActivity();
        mAPI =  new API();
        name = (EditText) view.findViewById(R.id.space_create_name);
        displayName = (TextView) view.findViewById(R.id.space_create_display_name);
        spaceBuilding = (TextView) view.findViewById(R.id.space_create_building);
        saveSpace = (Button) view.findViewById(R.id.space_create_save_button);
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
                if(!Utils.isEditTextEmpty(name)) {
                    if (!name.getText().toString().equals(mSpace.getName())) {
                        Space space = new Space();
                        space.setName(name.getText().toString());
                        updateSpace(mAPI.getClient(),space.toString());
                    } else {
                        Toast.makeText(getActivity(), "Please, update something.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please, name your building.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });
    }

    private void updateSpace(OkHttpClient client, String data) {
        Log.e("QUERY", ArgumentsKeys.SPACE_QUERY);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Log.e("JSON", data);
        Request request = new Request.Builder()
                .url(ArgumentsKeys.SPACE_QUERY + "/" + mSpaceId)
                .patch(body)
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
                    Log.e("RESPONSE", response.body().string());
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

    private void initView(Space space) {
        name.setText(space.getName());
        displayName.setText(name.getText());
    }

    private void getSpace(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(ArgumentsKeys.SPACE_QUERY + "/" + mSpaceId)
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
                    mSpace = mAPI.getSpace(response);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView(mSpace);
                        }
                    });
                }
            }
        });
    }
}
