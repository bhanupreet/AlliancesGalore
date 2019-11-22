package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class ChangePasswordFragment extends Fragment {
    private TextInputLayout mPasswordOld, mPasswordnew1, mPasswordnew2;
    private Button mChangePasswordbtn;
    private ProgressBar mProgress;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        findIds(view);
        FragFunctions.setToolBarTitle("Change Password", view);
        changePasswordClick();
        return view;
    }

    private void changePasswordClick() {
        mChangePasswordbtn.setOnClickListener(ChangePasswordOnClick);
    }

    private void findIds(View view) {
        mPasswordOld = view.findViewById(R.id.changepasswrd_passwordold);
        mPasswordnew1 = view.findViewById(R.id.changepasswrd_new1);
        mPasswordnew2 = view.findViewById(R.id.changepasswrd_new2);
        mChangePasswordbtn = view.findViewById(R.id.changepasswrd_changepasswordbtn);
        mProgress = view.findViewById(R.id.changepassword_prog);
    }

    private View.OnClickListener ChangePasswordOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String paswrd = Functions.TextOf(mPasswordOld);
            final AuthCredential credential = EmailAuthProvider.getCredential(Global.myProfile.getEmail(), paswrd);
            if (TextUtils.isEmpty(Functions.TextOf(mPasswordOld))
                    || TextUtils.isEmpty(Functions.TextOf(mPasswordnew1))
                    || TextUtils.isEmpty(Functions.TextOf(mPasswordnew2)))
                Functions.toast("Field cannot be left blank", getContext());

            else if (!Functions.TextOf(mPasswordnew2).equals(Functions.TextOf(mPasswordnew1)))
                Functions.toast("Both new passwords must be same", getContext());

            else if (Functions.TextOf(mPasswordOld).equals(Functions.TextOf(mPasswordnew1)))
                Functions.toast("New password cannot be same as old password", getContext());

            else {
                user.reauthenticate(credential).addOnCompleteListener(reauthenticateOnComplete);
                mProgress.setVisibility(View.VISIBLE);
            }
        }
    };

    private OnCompleteListener reauthenticateOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (!task.isSuccessful()) {
                Functions.toast(task);
                mProgress.setVisibility(View.INVISIBLE);
            } else
                user.updatePassword(Functions.TextOf(mPasswordnew2)).addOnCompleteListener(updateOnComplete);
        }
    };

    private OnCompleteListener updateOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful())
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Users")
                        .child(user.getUid()).child("password")
                        .setValue(Functions.encrypt(Functions.TextOf(mPasswordnew2)))
                        .addOnCompleteListener(updateDataBaseOnComplete);
            else {
                Functions.toast(task);
                mProgress.setVisibility(View.INVISIBLE);
            }
        }

    };

    private OnCompleteListener updateDataBaseOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                Objects.requireNonNull(getFragmentManager()).popBackStack();
                Functions.toast("Password updated successfully", getContext());
            } else
                Functions.toast(task);
            mProgress.setVisibility(View.INVISIBLE);
        }
    };
}
