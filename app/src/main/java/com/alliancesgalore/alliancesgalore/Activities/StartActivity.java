package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.alliancesgalore.alliancesgalore.R;

public class StartActivity extends AppCompatActivity {

    private Button mLoginbtn, mRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FindIds();
        regBtnClick();
        loginBtnClick();
    }

    private void FindIds() {
        mRegBtn = findViewById(R.id.start_reg_button);
        mLoginbtn = findViewById(R.id.start_already_btn);
    }

    private void regIntent() {
        Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(reg_intent);
    }

    private void loginIntent() {
        Intent login_intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(login_intent);
    }

    private void regBtnClick() {
        mRegBtn.setOnClickListener(v -> regIntent());
    }

    private void loginBtnClick() {
        mLoginbtn.setOnClickListener(v -> loginIntent());
    }
}
