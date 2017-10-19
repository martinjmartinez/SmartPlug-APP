package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

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

import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
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

public class SpaceDetailFragment extends Fragment {

    private Space mSpace;
    private API mAPI;
    private Activity mActivity;
    private String mSpaceId;
    private TextView mNameTextView;
    private TextView mDevicesTextView;
    private TextView mBuildingTextView;
    private TextView mPowerTextView;
    private TextView mInfoTextView;
    private Button mEditButton;
    private Button mDeleteButton;
    private MainActivity mMainActivity;
    private SpaceService spaceService;


    public SpaceDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mSpaceId = bundle != null ? bundle.getString(Constants.SPACE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_fragment, container, false);

        iniVariables(view);
        getSpace();
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

        mMainActivity.getSupportActionBar().setTitle("Space Details");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        spaceService = new SpaceService(Realm.getDefaultInstance());
        mSpace = new Space();
        mAPI = new API();
        mNameTextView = (TextView) view.findViewById(R.id.space_detail_name);
        mDevicesTextView = (TextView) view.findViewById(R.id.space_detail_devices);
        mBuildingTextView = (TextView) view.findViewById(R.id.space_detail_building);
        mPowerTextView = (TextView) view.findViewById(R.id.space_detail_power);
        mInfoTextView = (TextView) view.findViewById(R.id.space_detail_delete_info);
        mEditButton = (Button) view.findViewById(R.id.space_detail_update);
        mDeleteButton = (Button) view.findViewById(R.id.space_detail_delete);
    }

    private void initListeners() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpaceUpdateFragment spaceUpdateFragment = new SpaceUpdateFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.SPACE_ID, mSpaceId);
                spaceUpdateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.SPACE_DETAIL_FRAGMENT),
                        spaceUpdateFragment, FragmentKeys.SPACE_UPDATE_FRAGMENT, true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createDialog(mActivity, "Delete Space", "Do you want to delete this Space?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSpace(mAPI.getClient());
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

    private void deleteSpace(OkHttpClient client) {
        Log.e("QUERY", Constants.SPACE_QUERY);
        Request request = new Request.Builder()
                .url(Constants.SPACE_QUERY + "/" + mSpaceId)
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
                    mActivity.runOnUiThread(new Runnable( ) {
                        @Override
                        public void run() {
                            spaceService.deleteSpace(mSpaceId);
                            mActivity.onBackPressed();
                        }
                    });
                }
            }
        });
    }

    private void getSpace() {
        mSpace = spaceService.getSpaceById(mSpaceId);
        initSpaceView(mSpace);
    }

    private void initSpaceView(Space space) {
        mNameTextView.setText(space.getName());

        if (space.getDevices() != null) {
            mDevicesTextView.setText(space.getDevices().size() + "");
            if (!space.getDevices().isEmpty()) {
                mDeleteButton.setClickable(false);
                mInfoTextView.setVisibility(View.VISIBLE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.disabled));
            } else {
                mDeleteButton.setClickable(true);
                mInfoTextView.setVisibility(View.GONE);
                mDeleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.alert));
            }

            if (space.getBuilding() != null) {
                mBuildingTextView.setText(space.getBuilding().getName());
            }
        }
        //mPowerTextView.setText();
    }
}
