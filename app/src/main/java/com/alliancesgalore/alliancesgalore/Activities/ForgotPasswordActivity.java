package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
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

        findIds();
        setToolbar();
        resetclick();
    }

    private void setToolbar() {
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Forgot password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void findIds() {
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
               showProgressbar();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(resetClickOnComplete);

            } else {
                hideProgressbar();
                Functions.toast("Email cannot be left blank.", mCtx);
            }
        });

    }

    private OnCompleteListener resetClickOnComplete = (OnCompleteListener<Void>) task -> {

        if (task.isSuccessful())
            Functions.toast("Email sent", mCtx);
        else
            Functions.toast(task);
       hideProgressbar();
    };
    private void hideProgressbar() {
        mprogressBar.setVisibility(View.GONE);
     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void showProgressbar() {
        mprogressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }
}