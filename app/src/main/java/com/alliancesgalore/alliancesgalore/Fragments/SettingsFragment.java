package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alliancesgalore.alliancesgalore.Activities.SettingsActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;


public class SettingsFragment extends Fragment {

    private TextView mDisplayName;
    private TextView mDesignation;
    private ImageView mProfileImage;
    private ConstraintLayout mView;
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
        FragFunctions.setToolBarTitle("Settings",view);
        LoadImage();
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

    private void FindIds(View view){
        mView = view.findViewById(R.id.settings_view);
        mDisplayName = view.findViewById(R.id.settings_displayname);
        mProfileImage = view.findViewById(R.id.settings_profile_image);
        mDesignation = view.findViewById(R.id.settings_designation);
    }

    private void viewClick(){
        mView.setOnClickListener(viewClickListener);
    }

    private void LoadImage(){
        Glide.with(getContext())
                .load(R.drawable.defaultprofile)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileImage);
    }

    private View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.settings_container, new ProfileFragment()).addToBackStack("settings")
            .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
            ft.commit();
        }
    };

}
