package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Services.LocationService;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {

    private TextView mDisplayName;
    private TextView mDesignation;
    private TextView mChangePassword;
    private CircleImageView mProfileImage;
    private ConstraintLayout mView;
    private ConstraintLayout mToggle;
    private SwitchCompat mToggleBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        FindIds(view);
        viewClick();
        FragFunctions.setToolBarTitle("Settings", view);
        setDetails();
        changepasswordclick();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void FindIds(View view) {
        mView = view.findViewById(R.id.settings_view);
        mDisplayName = view.findViewById(R.id.settings_displayname);
        mProfileImage = view.findViewById(R.id.settings_profile_image);
        mDesignation = view.findViewById(R.id.settings_designation);
        mChangePassword = view.findViewById(R.id.settings_changepaswrdtn);
        mToggle = view.findViewById(R.id.locationToggle);
        mToggleBtn = view.findViewById(R.id.locationTogglebtn);
        SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences("location", 0);
        String silent = settings.getString("locationservice", "on");
        assert silent != null;
        if (silent.equals("off"))
            mToggleBtn.setChecked(false);
        else
            mToggleBtn.setChecked(true);
        mToggleBtn.setOnClickListener(view1 -> {
            if (mToggleBtn.isChecked()) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("location", 0).edit();
                editor.putString("locationservice", "on");
                Functions.toast("on", getContext());
                StartTrackerService();
                editor.apply();
            } else {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("location", 0).edit();
                editor.putString("locationservice", "off");
                Functions.toast("off", getContext());
                StopTrackerService();
                editor.apply();
            }
        });

        mToggle.setVisibility(View.GONE);
    }

    private void viewClick() {
        mView.setOnClickListener(viewClickListener);
    }

    private void LoadImage() {
        if (Global.myProfile != null) {
            Picasso.get()
                    .load(Global.myProfile.getImage())
                    .placeholder(R.drawable.defaultprofile)
                    .into(mProfileImage);
        }
    }

    private void setAnimClick() {
        ProfileFragment profileFragment = new ProfileFragment();
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(mProfileImage, Objects.requireNonNull(ViewCompat.getTransitionName(mProfileImage)))
                .addSharedElement(mDisplayName, Objects.requireNonNull(ViewCompat.getTransitionName(mDisplayName)))
                .addSharedElement(mDesignation, Objects.requireNonNull(ViewCompat.getTransitionName(mDesignation)))
                .addToBackStack("settings")
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                .replace(R.id.settings_container_pref, new EmptyFragment())
                .replace(R.id.settings_container, profileFragment)
                .commit();
    }

    private void setDetails() {
        LoadImage();
        if
        (Global.myProfile != null) {
            mDesignation.setText(Global.myProfile.getRole());
            mDisplayName.setText(Global.myProfile.getDisplay_name());
        }
    }

    private void changepasswordclick() {
        mChangePassword.setOnClickListener(ChangePasswordListener);
    }

    private View.OnClickListener viewClickListener = view -> setAnimClick();

    private View.OnClickListener ChangePasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();

            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mChangePassword, Objects.requireNonNull(ViewCompat.getTransitionName(mChangePassword)))
                    .addToBackStack("settings")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.settings_container, changePasswordFragment)
//                    .replace(R.id.settings_container_pref, new EmptyFragment())
                    .commit();
        }
    };

    private void StopTrackerService() {

        Functions.toast("location service stopped", getContext());
        Objects.requireNonNull(getActivity()).stopService((new Intent(getContext(), LocationService.class)));

    }

    private void StartTrackerService() {

        Functions.toast("location service started", getContext());
        Objects.requireNonNull(getActivity()).startService((new Intent(getContext(), LocationService.class)));

    }
}
