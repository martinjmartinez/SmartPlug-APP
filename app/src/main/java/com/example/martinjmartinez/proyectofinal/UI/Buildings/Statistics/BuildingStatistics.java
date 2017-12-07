package com.example.martinjmartinez.proyectofinal.UI.Buildings.Statistics;

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

public class BuildingStatistics extends Fragment {
    private MainActivity mMainActivity;
    private Activity mActivity;
    private ViewPager fragmentsViewpager;
    private String objectId;
    private TabLayout detailsTabLayout;
    private BuildingStatisticsViewPagerAdapter buildingChartsStatisticsViewPager;

    public BuildingStatistics() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        objectId = bundle != null ? bundle.getString(Constants.BUILDING_ID, "") : "";
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
        buildingChartsStatisticsViewPager = new BuildingStatisticsViewPagerAdapter(getChildFragmentManager(), getContext(), objectId);
        detailsTabLayout.setupWithViewPager(fragmentsViewpager, true);

        fragmentsViewpager.setAdapter(buildingChartsStatisticsViewPager);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle("Statistics");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }
}
