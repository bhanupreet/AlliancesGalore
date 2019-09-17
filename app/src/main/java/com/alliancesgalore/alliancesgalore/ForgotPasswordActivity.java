package com.alliancesgalore.alliancesgalore;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        //setting ids
        mprogressBar = findViewById(R.id.forgotpasswordprogress);
        mEmail = findViewById(R.id.forgotpswrdemail);
        mToolBar = findViewById(R.id.forgotpasswordappbar);
        mResetButton = findViewById(R.id.resetPasswordbtn);

        //toolbar
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Forgot password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();

                String emailAddress = mEmail.getEditText().getText().toString();
                if (!TextUtils.isEmpty(emailAddress)) {
                    mprogressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Email sent!", Toast.LENGTH_LONG).show();
                                        Log.d("123", "Email sent.");
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    mprogressBar.setVisibility(View.INVISIBLE);
                                }
                            });

                } else {
                    mprogressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotPasswordActivity.this, "Email cannot be left blank.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}