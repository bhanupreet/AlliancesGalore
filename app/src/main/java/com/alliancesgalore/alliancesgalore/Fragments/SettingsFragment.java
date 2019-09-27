package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alliancesgalore.alliancesgalore.Activities.SettingsActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import kotlin.Function;


public class SettingsFragment extends Fragment {

    private TextView mDisplayName;
    private TextView mDesignation;
    private ImageView mProfileImage;
    private ConstraintLayout mView;
    private String mDisplayNameString,mDesignationString,mProfileImageString;
    private UserProfile myProfile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        FindIds(view);
        viewClick();
        FragFunctions.setToolBarTitle("Settings", view);
        setDetails();
        return view;
    }

    @Override
    public void onAttach(Context context) {
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
    }

    private void viewClick() {
        mView.setOnClickListener(viewClickListener);
    }

    private void LoadImage(String url) {
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.defaultprofile)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileImage);
    }

    private void setAnimClick(){
        ProfileFragment profileFragment = new ProfileFragment();
        getFragmentManager()
                .beginTransaction()
                .addSharedElement(mProfileImage, ViewCompat.getTransitionName(mProfileImage))
                .addSharedElement(mDisplayName,ViewCompat.getTransitionName(mDisplayName))
                .addSharedElement(mDesignation,ViewCompat.getTransitionName(mDesignation))
                .addToBackStack("settings")
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.settings_container, profileFragment)
                .commit();
    }

    private View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          setAnimClick();
        }
    };



    private void setDetails(){
        LoadImage(Functions.myProfile.getImage());
        mDesignation.setText(Functions.myProfile.getRole());
        mDisplayName.setText(Functions.myProfile.getDisplay_name());
    }



}
