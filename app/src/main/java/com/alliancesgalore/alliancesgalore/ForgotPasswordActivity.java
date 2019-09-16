package com.alliancesgalore.alliancesgalore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout mEmail;
    private Button mResetButton;
    private Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mEmail = findViewById(R.id.forgotpswrdemail);
        mToolBar = findViewById(R.id.forgotpasswordappbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Forgot password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mResetButton = findViewById(R.id.resetPasswordbtn);

        //  final
        //final

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetButton.setEnabled(false);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = mEmail.getEditText().getText().toString();
                Toast.makeText(ForgotPasswordActivity.this,emailAddress,Toast.LENGTH_LONG).show();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this,"email sent",Toast.LENGTH_LONG).show();
                                    Log.d("123", "Email sent.");
                                }
                                if(!task.isSuccessful()){
                                    Toast.makeText(ForgotPasswordActivity.this,"error",Toast.LENGTH_LONG).show();
                                }
                                mResetButton.setEnabled(true);
                            }
                        });
            }
        });
    }
}