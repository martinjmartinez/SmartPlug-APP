package com.example.martinjmartinez.proyectofinal.UI.Routines.Fragments;

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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.martinjmartinez.proyectofinal.Entities.Routine;
import com.example.martinjmartinez.proyectofinal.Models.RoutineFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.RoutineService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.UI.Routines.Adapters.RoutinesListAdapter;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.FragmentKeys;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.realm.Realm;

public class RoutinesFragment extends Fragment {
    private Activity mActivity;
    private MainActivity mMainActivity;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FloatingActionButton mAddRoutineButton;
    private LinearLayout mEmptyRoutinesListLayout;
    private ListView routinesListView;
    private RoutinesListAdapter routinesListAdapter;
    private List<Routine> routines;
    private String buildingId;
    private RoutineService routineService;
    private ValueEventListener routineListener;
    private DatabaseReference routinesRef;

    public RoutinesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            buildingId = bundle.getString(Constants.BUILDING_ID, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.routines_fragment, container, false);

        iniVariables(view);
        initListeners();
        getRoutinesByBuilding();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;
        routineService = new RoutineService(Realm.getDefaultInstance());

    }

    @Override
    public void onPause() {
        super.onPause();

        routinesRef.removeEventListener(routineListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMainActivity.getSupportActionBar().setTitle(R.string.routines);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMainActivity.getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mMainActivity.toggleDrawerIcon(true, 0, null);
        }

    }

    private void iniVariables(View view) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        routinesRef = FirebaseDatabase.getInstance().getReference("Accounts/" + currentUser.getUid() + "/Routines/");
        mEmptyRoutinesListLayout =  view.findViewById(R.id.empty_routine_list_layout);
        mAddRoutineButton =  view.findViewById(R.id.add_routine_button);
        routinesListView = view.findViewById(R.id.routine_list);
    }

    private void initListeners() {
        mMainActivity.toggleDrawerIcon(false, R.drawable.ic_action_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.onBackPressed();
            }
        });

        mAddRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineCreationFragment routineCreationFragment = new RoutineCreationFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUILDING_ID, buildingId);

                routineCreationFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.ROUTINES_FRAGMENT), routineCreationFragment, FragmentKeys.ROUTINES_CREATION_FRAGMENT, true);
            }
        });

        routinesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        routineListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                        RoutineFB routineFB = snapshot.getValue(RoutineFB.class);
                        if(routineFB != null) {
                            routineService.updateOrCreateLocal(routineFB);
                        }
                    }
                }

                getRoutinesByBuilding();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        routinesRef.addValueEventListener(routineListener);

        routinesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Routine routine = routines.get(position);
                RoutineUpdateFragment routineUpdateFragment = new RoutineUpdateFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.ROUTINE_ID, routine.get_id());

                routineUpdateFragment.setArguments(bundle);
                Utils.loadContentFragment(getFragmentManager().findFragmentByTag(FragmentKeys.ROUTINES_FRAGMENT), routineUpdateFragment, FragmentKeys.ROUTINES_EDIT_FRAGMENT, true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    private void getRoutinesByBuilding() {
        routines = routineService.getRoutineByBuilding(buildingId);
        shouldEmptyMessageShow();
    }

    private void initRoutineList(List<Routine> routineList) {
        routinesListAdapter = new RoutinesListAdapter(routineList, getContext());
        routinesListView.setAdapter(routinesListAdapter);
    }

    private void shouldEmptyMessageShow() {
        if (!routines.isEmpty()) {
            mEmptyRoutinesListLayout.setVisibility(View.GONE);
            initRoutineList(routines);
        } else {
            routinesListView.setVisibility(View.GONE);
            mEmptyRoutinesListLayout.setVisibility(View.VISIBLE);
            routinesListAdapter = new RoutinesListAdapter(routines, getContext());
        }
    }
}
