package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;


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
        findIds(view);
        changeBtnClick();
        FragFunctions.setToolBarTitle("Change Name", view);
        return view;
    }

    private void findIds(View view) {
        mChangeBtn = view.findViewById(R.id.changeame_changenamebtn);
        mchangeNameText = view.findViewById(R.id.changename_name);
        Objects.requireNonNull(mchangeNameText.getEditText()).setText(myProfile.getDisplay_name());
    }

    private void changeBtnClick() {
        mChangeBtn.setOnClickListener(changeBtnListener);
    }

    private View.OnClickListener changeBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String uid = FirebaseAuth.getInstance().getUid();
            assert uid != null;
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Users")
                    .child(uid)
                    .child("display_name")
                    .setValue(Functions.TextOf(mchangeNameText))
                    .addOnCompleteListener(changeNameOnComplete);
        }
    };
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists())
                myProfile = dataSnapshot.getValue(UserProfile.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private OnCompleteListener changeNameOnComplete = task -> {
        if (task.isSuccessful()) {
            Toast.makeText(getContext(), "Name changed Successfully", Toast.LENGTH_SHORT).show();
            String uid = FirebaseAuth
                    .getInstance()
                    .getUid();

            assert uid != null;
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Users")
                    .child(uid)
                    .addValueEventListener(valueEventListener);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        } else
            Functions.toast(task);

    };


}
