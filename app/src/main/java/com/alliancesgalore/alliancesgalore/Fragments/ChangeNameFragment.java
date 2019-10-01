package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class ChangeNameFragment extends Fragment {
    private Button mChangeBtn;
    private TextInputLayout mchangeNameText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_name, container, false);
        FindIds(view);
        changebtnclick();
        FragFunctions.setToolBarTitle("Change Name", view);
        return view;
    }

    private void FindIds(View view) {
        mChangeBtn = view.findViewById(R.id.changeame_changenamebtn);
        mchangeNameText = view.findViewById(R.id.changename_name);
        mchangeNameText.getEditText().setText(Global.myProfile.getDisplay_name());
    }

    private void changebtnclick() {
        mChangeBtn.setOnClickListener(changebtnlistener);
    }

    private View.OnClickListener changebtnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Users")
                    .child(uid)
                    .child("display_name")
                    .setValue(Functions.TextOf(mchangeNameText))
                    .addOnCompleteListener(changenameOnComplete);
        }
    };


    private OnCompleteListener changenameOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Name changed Successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            } else {
                Functions.toast(task);
            }
        }
    };

}