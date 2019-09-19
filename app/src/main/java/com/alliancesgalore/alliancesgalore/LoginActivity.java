package com.alliancesgalore.alliancesgalore;

import android.content.Intent;
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

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private FirebaseAuth mAuth;
    private Button mLoginBtn;
    private String email, password;
    private Button mForgotPasswordbtn;
    private Toolbar mLoginToolbar;
    private ProgressBar mprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mprogressBar = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        mLoginEmail = findViewById(R.id.login_email);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_login_btn);
        mLoginToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mLoginToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mForgotPasswordbtn = findViewById(R.id.forgotpasswordbtn);

        final CredentialsClient mCredentialsClient;

// ...

        mCredentialsClient = Credentials.getClient(this);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mLoginEmail.getEditText().getText().toString();
                password = mLoginPassword.getEditText().getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Email or Password cannot be left blank", Toast.LENGTH_SHORT).show();
                } else {
                    mprogressBar.setVisibility(View.VISIBLE);

                    Credential credential = new Credential.Builder(email)
                            .setPassword(password).build();
                    mCredentialsClient.save(credential);
                    LogIn(email, password);

                }
            }
        });

        mForgotPasswordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(PasswordIntent);
            }
        });


    }

    private void LogIn(final String email, final String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w("tag", "getInstanceId failed", task.getException());
                                                return;
                                            }

                                            // Get new Instance ID token
                                            String token = task.getResult().getToken();
                                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(current_user.getUid()).child("TokenID").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                    mainIntent.putExtra("email", email);
                                                    mainIntent.putExtra("password", password);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                        } else {
                            mprogressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            //Log.w(, "signInWithEmail:failure", task.getException());

//                            Snackbar snackbar = Snackbar
//                                    .make(getWindow().getDecorView().getRootView(),
//                                            Objects.requireNonNull(task.getException()).getMessage(),
//                                            Snackbar.LENGTH_LONG);
//                            snackbar.show();
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}