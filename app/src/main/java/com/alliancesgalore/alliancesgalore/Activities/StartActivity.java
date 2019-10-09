package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        RegBtnClick();
        LoginBtnClick();
    }

    private void FindIds() {
        mRegBtn = findViewById(R.id.start_reg_button);
        mLoginbtn = findViewById(R.id.start_already_btn);
    }

    private void RegBtnClick() {
        mRegBtn.setOnClickListener(v -> regIntent());
    }

    private void regIntent() {
        Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(reg_intent);
    }

    private void LoginBtnClick() {
        mLoginbtn.setOnClickListener(v -> loginIntent());
    }

    private void loginIntent() {
        Intent login_intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(login_intent);
    }
}
