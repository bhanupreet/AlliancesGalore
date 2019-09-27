package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alliancesgalore.alliancesgalore.Activities.SettingsActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import kotlin.Function;


public class ProfileFragment extends Fragment {

    private ImageView mProfileImage;
    private TextView mDisplayName, mEmail, mDeesignation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FragFunctions.setToolBarTitle("Profile", view);
        FindIds(view);
        setDetails();
        return view;
    }

    private void LoadImage() {
        Glide.with(getContext())
                .load(R.drawable.defaultprofile)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileImage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDetails() {
        LoadImage();
        mDisplayName.setText(Functions.myProfile.getDisplay_name());
        mEmail.setText(Functions.myProfile.getEmail());
        mDeesignation.setText(Functions.myProfile.getRole());
    }

    private void FindIds(View view) {
        mProfileImage = view.findViewById(R.id.profile_displayImage);
        mDisplayName = view.findViewById(R.id.profile_displayName);
        mDeesignation = view.findViewById(R.id.profile_display_Designation);
        mEmail = view.findViewById(R.id.profile_display_Email);
    }
}
