package com.alliancesgalore.alliancesgalore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.AESUtils;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout pwd1, pwd2;
    private String pwd1string, pwd2string;
    private ProgressBar mProgressbar;
    private Button changepassworn_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        pwd1 = findViewById(R.id.changepasswrd_pwd1);
        pwd2 = findViewById(R.id.changepasswrd_pwd2);
        Toolbar mToolbar = findViewById(R.id.changepassword_toolbar);
//        changepassworn_btn = findViewById(R.id.changepassword_changepasswordbtn);
        mProgressbar = findViewById(R.id.changepassword_progress);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ChangePasswordActivity.this, MainActivityold.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });

        dialogbox();

// Prompt the user to re-provide their sign-in credentials


        changepassworn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressbar.setVisibility(View.VISIBLE);
                pwd1string = pwd1.getEditText().getText().toString();
                pwd2string = pwd2.getEditText().getText().toString();

                if ((pwd1string.equals(pwd2string))) {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String newPassword = pwd1string;

                    if (!TextUtils.isEmpty(newPassword)) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Functions.toast(task);
                                        } else {
                                            String encrypted = encrypt(newPassword);
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("password").setValue(encrypted).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_LONG).show();
                                                        Intent mainIntent = new Intent(ChangePasswordActivity.this, MainActivityold.class);
                                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(mainIntent);
                                                    } else {
                                                        dialogbox();
                                                        Functions.toast(task);                                                    }
//                                                    mProgressbar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Fields cannot be left blank", Toast.LENGTH_LONG).show();
//                        mProgressbar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Both of the fields must be same", Toast.LENGTH_LONG).show();

                }
            }

        });
        mProgressbar.setVisibility(View.INVISIBLE);
    }

    private void dialogbox() {


        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        final TextInputLayout edittext = dialogView.findViewById(R.id.alert_password);
        TextInputLayout email = dialogView.findViewById(R.id.alert_Email);
        email.setEnabled(false);
        Button button1 = dialogView.findViewById(R.id.alert_login_button);
        Button button2 = dialogView.findViewById(R.id.alert_cancelbtn);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        email.getEditText().setText(user.getEmail());

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressbar.setVisibility(View.VISIBLE);
                String paswrd = edittext.getEditText().getText().toString();

                final AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), paswrd);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    dialogBuilder.dismiss();
                                } else {
                                    Functions.toast(task);
                                }
                                mProgressbar.setVisibility(View.INVISIBLE);

                            }
                        });
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                Intent mainIntent = new Intent(ChangePasswordActivity.this, MainActivityold.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCanceledOnTouchOutside(false);
        dialogBuilder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private String encrypt(String decrypted) {
        String encrypted = "";
        String sourceStr = decrypted;
        try {
            encrypted = AESUtils.encrypt(sourceStr);
            Log.d("TEST", "encrypted:" + encrypted);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return encrypted;
    }
}
