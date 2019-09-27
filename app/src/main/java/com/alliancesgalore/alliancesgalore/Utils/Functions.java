package com.alliancesgalore.alliancesgalore.Utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alliancesgalore.alliancesgalore.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public final class Functions {

    public static UserProfile myProfile;

    @SuppressLint("RestrictedApi")


    public static void toast(Task task) {
        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
    }

    public static String encrypt(String decrypted) {
        String encrypted = "";
        try {
            encrypted = AESUtils.encrypt(decrypted);
            Log.d("TEST", "encrypted:" + encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public static String decrypt(String encrypted) {
        String decrypted = "";
        try {
            decrypted = AESUtils.decrypt(encrypted);
            Log.d("TEST", "decrypted:" + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public static String TextOf(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString();
    }


}
