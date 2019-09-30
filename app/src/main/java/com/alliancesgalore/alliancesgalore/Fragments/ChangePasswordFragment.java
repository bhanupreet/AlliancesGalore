package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.Activities.ChangePasswordActivity;
import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.Activities.MainActivityold;
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

import kotlin.Function;


public class ChangePasswordFragment extends Fragment {
    private TextInputLayout mPasswordOld, mPasswordnew1, mPasswordnew2;
    private Button mChangePasswordbtn;
    private ProgressBar mProgress;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        FindIds(view);
        FragFunctions.setToolBarTitle("Change Password", view);
        ChangePasswordClick();
        return view;
    }

    private void ChangePasswordClick() {
        mChangePasswordbtn.setOnClickListener(ChangePasswordOnClick);
    }

    private void FindIds(View view) {
        mPasswordOld = view.findViewById(R.id.changepasswrd_passwordold);
        mPasswordnew1 = view.findViewById(R.id.changepasswrd_new1);
        mPasswordnew2 = view.findViewById(R.id.changepasswrd_new2);
        mChangePasswordbtn = view.findViewById(R.id.changepasswrd_changepasswordbtn);
        mProgress = view.findViewById(R.id.changepassword_prog);
    }


    private View.OnClickListener ChangePasswordOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (TextUtils.isEmpty(Functions.TextOf(mPasswordOld)) || TextUtils.isEmpty(Functions.TextOf(mPasswordnew1)) || TextUtils.isEmpty(Functions.TextOf(mPasswordnew2)))
                Toast.makeText(getContext(), "Field cannot be left blank", Toast.LENGTH_SHORT).show();
            else {
                mProgress.setVisibility(View.VISIBLE);
                String paswrd = Functions.TextOf(mPasswordOld);
                final AuthCredential credential = EmailAuthProvider.getCredential(Global.myProfile.getEmail(), paswrd);
                user.reauthenticate(credential).addOnCompleteListener(reauthenticateOnComplete);
            }
        }
    };


    private OnCompleteListener reauthenticateOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (!task.isSuccessful())
                Functions.toast(task);
            if (Functions.TextOf(mPasswordnew1).equals(Functions.TextOf(mPasswordnew2)))
                user.updatePassword(Functions.TextOf(mPasswordnew2)).addOnCompleteListener(updateOnComplete);
            else
                Toast.makeText(getContext(), "Both Fields must be same", Toast.LENGTH_SHORT).show();

        }
    };


    private OnCompleteListener updateOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful())
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(user.getUid()).child("password")
                        .setValue(Functions.encrypt(Functions.TextOf(mPasswordnew2)))
                        .addOnCompleteListener(updateDataBaseOnComplete);
            else
                Functions.toast(task);
        }

    };

    private OnCompleteListener updateDataBaseOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                sendToMain();
                Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
            } else
                Functions.toast(task);
            mProgress.setVisibility(View.INVISIBLE);
        }
    };

    private void sendToMain() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
}
