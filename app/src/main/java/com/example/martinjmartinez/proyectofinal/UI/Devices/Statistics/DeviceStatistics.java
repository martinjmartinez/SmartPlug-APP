package com.example.martinjmartinez.proyectofinal.UI.Devices.Statistics;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;

public class DeviceStatistics extends Fragment {

    private MainActivity mMainActivity;
    private Activity mActivity;
    private ViewPager fragmentsViewpager;
    private String objectId;
    private TabLayout detailsTabLayout;
    private DeviceStatisticsViewPagerAdapter deviceStatisticsViewPagerAdapter;

    public DeviceStatistics() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        objectId = bundle != null ? bundle.getString(Constants.DEVICE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.object_statistics, container, false);

        iniVariables(view);
        initListeners();
        setupViewPager(fragmentsViewpager);
        return view;
    }

    private void iniVariables(View view) {
        fragmentsViewpager = view.findViewById(R.id.fragments);
        detailsTabLayout = view.findViewById(R.id.fragmentsTabs);
    }

    private void initListeners() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;

    }

    private void setupViewPager(ViewPager fragmentsViewpager) {
        deviceStatisticsViewPagerAdapter = new DeviceStatisticsViewPagerAdapter(getChildFragmentManager(), getContext(), objectId);
        detailsTabLayout.setupWithViewPager(fragmentsViewpager, true);

        fragmentsViewpager.setAdapter(deviceStatisticsViewPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle(R.string.statistics);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }
}
