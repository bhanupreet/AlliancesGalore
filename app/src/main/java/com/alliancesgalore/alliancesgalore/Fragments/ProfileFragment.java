package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alliancesgalore.alliancesgalore.Activities.SettingsActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import kotlin.Function;

import static com.alliancesgalore.alliancesgalore.R.menu.settings_menu;


public class ProfileFragment extends Fragment {

    private ImageView mProfileImage, mChangeNamebtn;
    private TextView mEmail, mDeesignation, mDisplayName;

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
        setHasOptionsMenu(true);
        FindIds(view);
        setDetails();
        editNameBtn();
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
                if (getFragmentManager().getBackStackEntryCount() != 0)
                    getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setDetails() {
        LoadImage();
        mDisplayName.setText(Global.myProfile.getDisplay_name());
        mEmail.setText(Global.myProfile.getEmail());
        mDeesignation.setText(Global.myProfile.getRole());
    }

    private void FindIds(View view) {
        mProfileImage = view.findViewById(R.id.profile_displayImage);
        mDisplayName = view.findViewById(R.id.profile_displayName);
        mDeesignation = view.findViewById(R.id.profile_display_Designation);
        mChangeNamebtn = view.findViewById(R.id.profile_editnamebtn);
        mEmail = view.findViewById(R.id.profile_display_Email);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void editNameBtn() {
        mChangeNamebtn.setOnClickListener(editnameOnClick);

    }

    private View.OnClickListener editnameOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ChangeNameFragment changeNameFragment = new ChangeNameFragment();
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mDisplayName, ViewCompat.getTransitionName(mDisplayName))
                    .addToBackStack("profile")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.settings_container, changeNameFragment)
                    .commit();
        }
    };
}
