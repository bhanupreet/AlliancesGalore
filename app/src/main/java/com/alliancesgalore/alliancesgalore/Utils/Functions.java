package com.alliancesgalore.alliancesgalore.Utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.util.Objects;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public final class Functions {
    @SuppressLint("RestrictedApi")
    public static void toast(Task task){
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
}
