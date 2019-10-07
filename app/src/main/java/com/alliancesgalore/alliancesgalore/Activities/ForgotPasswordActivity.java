package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout mEmail;
    private Button mResetButton;
    private Toolbar mToolBar;
    private ProgressBar mprogressBar;
    private Context mCtx = ForgotPasswordActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        FindIds();
        setToolbar();
        resetclick();
    }

    private void setToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Forgot password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void FindIds() {
        mprogressBar = findViewById(R.id.forgotpasswordprogress);
        mEmail = findViewById(R.id.forgotpswrdemail);
        mToolBar = findViewById(R.id.forgotpasswordappbar);
        mResetButton = findViewById(R.id.resetPasswordbtn);
    }

    private void resetclick() {
        mResetButton.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = Functions.TextOf(mEmail);
            if (!TextUtils.isEmpty(emailAddress)) {
                mprogressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(resetClickOnComplete);

            } else {
                mprogressBar.setVisibility(View.INVISIBLE);
                Functions.toast("Email cannot be left blank.", mCtx);
            }
        });
    }

    private OnCompleteListener resetClickOnComplete = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful())
                Functions.toast("Email sent", mCtx);
            else
                Functions.toast(task);
            mprogressBar.setVisibility(View.INVISIBLE);
        }
    };
}