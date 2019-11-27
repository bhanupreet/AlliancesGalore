package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alliancesgalore.alliancesgalore.Activities.AddEventActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class AddDescriptionLocatonFragment extends Fragment {

    private TextInputLayout mDescLoc;
    private Button mSaveBtn;
    private String myStrings;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_description_location, container, false);

        getIntent();
        findIds(view);
        setHint();
        setSaveBtn();

        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fm = Objects
                    .requireNonNull(getActivity())
                    .getSupportFragmentManager();

            fm.popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSaveBtn() {
        mSaveBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(Functions.TextOf(mDescLoc))) {
                Functions.toast("Feild cannot be left blank", getContext());
            } else {

                if (myStrings.equals("desc")) {
                    AddEventActivity.setDescription(Functions.TextOf(mDescLoc));
                } else {
                    AddEventActivity.setLocation(Functions.TextOf(mDescLoc));
                }

                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                fm.popBackStack();
            }
        });
    }

    private void setHint() {
        if (myStrings.equals("desc")) {
            mDescLoc.setHint("Description");
            Objects.requireNonNull(mDescLoc.getEditText()).setText(AddEventActivity.getDescription());
        } else {
            mDescLoc.setHint("Location");
            Objects.requireNonNull(mDescLoc.getEditText()).setText(AddEventActivity.getLocation());
        }

    }

    private void getIntent() {
        Bundle bundle = getArguments();
        assert bundle != null;
        myStrings = bundle.getString("desc_loc");
    }

    private void findIds(View view) {
        mSaveBtn = view.findViewById(R.id.addEvent_desc_loc_savebtn);
        mDescLoc = view.findViewById(R.id.addEvent_desc_loc_input);
    }
}
