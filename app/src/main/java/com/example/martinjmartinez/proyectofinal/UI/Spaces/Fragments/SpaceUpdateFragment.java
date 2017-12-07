package com.example.martinjmartinez.proyectofinal.UI.Spaces.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.martinjmartinez.proyectofinal.Entities.Space;
import com.example.martinjmartinez.proyectofinal.Models.SpaceFB;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.SpaceService;
import com.example.martinjmartinez.proyectofinal.Services.SpacesLimitsService;
import com.example.martinjmartinez.proyectofinal.UI.MainActivity.MainActivity;
import com.example.martinjmartinez.proyectofinal.Utils.Constants;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import io.realm.Realm;

public class SpaceUpdateFragment extends Fragment {

    private Space mSpace;
    private Activity mActivity;
    private EditText name, limitTextView;
    private TextView displayName;
    private Button saveSpace;
    private String mSpaceId;
    private MainActivity mMainActivity;
    private SpaceService spaceService;
    private SpacesLimitsService spacesLimitsService;

    public SpaceUpdateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mSpaceId = bundle != null ? bundle.getString(Constants.SPACE_ID, "") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.space_creation_fragment, container, false);

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

        mMainActivity.getSupportActionBar().setTitle("Space Edit");
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
        spacesLimitsService = new SpacesLimitsService(Realm.getDefaultInstance());
        name =  view.findViewById(R.id.space_create_name);
        displayName = view.findViewById(R.id.space_create_display_name);
        saveSpace = view.findViewById(R.id.space_create_save_button);
        limitTextView = view.findViewById(R.id.space_create_limit);
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
                if (!Utils.isEditTextEmpty(name)) {
                    SpaceFB spaceFB = new SpaceFB();
                    spaceFB.set_id(mSpaceId);
                    if (!name.getText().toString().equals(mSpace.getName()) || !limitTextView.getText().equals(mSpace.getMonthlyLimit())) {
                        if (Utils.isEditTextEmpty(limitTextView)) {
                            spaceService.updateSpaceLimit(mSpaceId, 0.0);
                            spaceFB.setMonthlyLimit(0.0);

                        } else {
                            spaceService.updateSpaceLimit(mSpaceId, Double.parseDouble(limitTextView.getText().toString()));
                            spaceFB.setMonthlyLimit(Double.parseDouble(limitTextView.getText().toString()));
                        }
                        spaceService.updateSpaceName(mSpaceId, name.getText().toString());
                        spaceFB.setName(name.getText().toString());
                        spacesLimitsService.updateOrCreateCloud(spaceFB);
                        mActivity.onBackPressed();
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

    private void initView(Space space) {
        name.setText(space.getName());
        displayName.setText(name.getText());
        limitTextView.setText("" +space.getMonthlyLimit());
    }

    private void getSpace() {
        mSpace = spaceService.getSpaceById(mSpaceId);
        initView(mSpace);
    }
}
