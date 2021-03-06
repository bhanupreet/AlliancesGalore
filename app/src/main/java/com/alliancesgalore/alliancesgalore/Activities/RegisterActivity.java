package com.alliancesgalore.alliancesgalore.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
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
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayname;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private String Email;
    private String password;
    private String role;
    private String display_name;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mregProgress;
    private DatabaseReference mDatabase;
    private Spinner mSpinner;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findIds();
        setToolbar();
        createBtn();
        setSpinner();
    }

    private void findIds() {
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.reg_create_btn);
        mDisplayname = findViewById(R.id.reg_displayname);
        mSpinner = findViewById(R.id.reg_spinner);
        mToolbar = findViewById(R.id.login_toolbar);
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void createBtn() {
        mCreateBtn.setOnClickListener(CreateBtnClickListener);
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    private void registerUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, RegUserOnComplete);
    }

    private void updateDatabase(HashMap<String, Object> userMap) {
        mDatabase.setValue(userMap).addOnCompleteListener(UpdateDatabaseOnCompleteListener);
    }

    private void getRole() {
        role = mSpinner.getSelectedItem().toString();
        switch (role.toLowerCase()) {
            case "manager":
                level = 10;
                break;
            case "team leader":
                level = 20;
                break;
            case "executive":
                level = 30;
                break;
            default:
                level = 0;
        }
    }

    private void regProgress() {
        mregProgress = new ProgressDialog(this);
        mregProgress.setTitle("Registering user");
        mregProgress.setMessage("Please wait while we create your account");
        mregProgress.setCanceledOnTouchOutside(false);
        mregProgress.show();
    }

    private void updateMap(HashMap<String, Object> userMap, String token, String encrypted) {
        userMap.put("email", Email);
        userMap.put("password", encrypted);
        userMap.put("display_name", display_name);
        userMap.put("image", "default");
        userMap.put("role", role);
        userMap.put("level", level);
        userMap.put("TokenID", token);
        if (role.toLowerCase().equals("manager"))
            userMap.put("ReportingTo", "Admin");
    }

    private Boolean emptyFieldsCheck() {
        return TextUtils.isEmpty(Email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(display_name) || mSpinner.getSelectedItem().equals("Select One");
    }

    private View.OnClickListener CreateBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            display_name = Functions.TextOf(mDisplayname);
            Email = Functions.TextOf(mEmail);
            password = Functions.TextOf(mPassword);
            if (emptyFieldsCheck()) {
                Functions.toast("Field  cannot be left blank", RegisterActivity.this);
            } else {
                regProgress();
                registerUser(Email, password);
            }
        }
    };

    private OnCompleteListener<AuthResult> RegUserOnComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                if (current_user != null) {
                    String uid = current_user.getUid();
                    mDatabase = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Users")
                            .child(uid);

                    getRole();
                    FirebaseInstanceId
                            .getInstance()
                            .getInstanceId()
                            .addOnCompleteListener(TokenOnCompleteListener);
                }
            } else {
                mregProgress.hide();
                Functions.toast(task);
            }
        }
    };

    private OnCompleteListener<InstanceIdResult> TokenOnCompleteListener = new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
            if (!task.isSuccessful())
                Functions.toast(task);
            String token = Objects.requireNonNull(task.getResult()).getToken();
            String encrypted = Functions.encrypt(password);
            HashMap<String, Object> userMap = new HashMap<>();
            updateMap(userMap, token, encrypted);
            updateDatabase(userMap);
        }
    };

    private OnCompleteListener<Void> UpdateDatabaseOnCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                mregProgress.dismiss();
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                Functions.toast(task);
            }
        }
    };
}