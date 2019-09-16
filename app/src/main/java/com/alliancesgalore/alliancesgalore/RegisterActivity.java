package com.alliancesgalore.alliancesgalore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayname;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private String Email, password;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mregProgress;
    private DatabaseReference mDatabase;
    private CredentialsClient mCredentialsClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.reg_create_btn);

        mregProgress = new ProgressDialog(this);


        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String display_name = mDisplayname.getEditText().getText().toString();
                Email = mEmail.getEditText().getText().toString();
                password = mPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Field  cannot be left blank", Toast.LENGTH_SHORT).show();
                } else {
                    mregProgress.setTitle("Registering user");
                    mregProgress.setMessage("Please wait while we create your account");
                    mregProgress.setCanceledOnTouchOutside(false);
                    mregProgress.show();
                    registeruser(Email, password);
                }
            }
        });

    }

    private void registeruser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                            if (current_user != null) {

                                String uid = current_user.getUid();
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                final HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("email", Email);
                                String encrypted = "";
                                String sourceStr = password;
                                try {
                                    encrypted = AESUtils.encrypt(sourceStr);
                                    Log.d("TEST", "encrypted:" + encrypted);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                userMap.put("password",encrypted);
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.w("tag1", "getInstanceId failed", task.getException());
                                                    return;
                                                }

                                                // Get new Instance ID token
                                                String token = task.getResult().getToken();
                                                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                                userMap.put("TokenID", token);
                                                // Log and toast
                                                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mregProgress.dismiss();
                                                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(mainIntent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "There was an error while registering user.", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                               // String msg = getString(R.string.msg_token_fmt, token);
                                              //  Log.d("tag1", msg);
                                                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
//
                        } else {
                            mregProgress.hide();
                            Toast.makeText(RegisterActivity.this, "There was an error while registering user.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
