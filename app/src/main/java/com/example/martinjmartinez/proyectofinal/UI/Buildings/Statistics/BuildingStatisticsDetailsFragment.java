package com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.API;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;

import io.realm.Realm;

public class BuildingStatisticsDetailsFragment extends Fragment {

    private API mAPI;
    private Activity mActivity;
    private MainActivity mMainActivity;
    private Realm realm;
    private String mBuildingId;

    public BuildingStatisticsDetailsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //todo add name of the building
        mMainActivity.getSupportActionBar().setTitle("Bui");
    }

    public void getArgumentsBundle() {
        Bundle bundle = this.getArguments();
        mBuildingId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        return view;
    }

    private void initListeners() {

    }

    private void iniVariables(View view) {
        realm = Realm.getDefaultInstance();
        mAPI = new API();

    }
}
