package com.alliancesgalore.alliancesgalore.Utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Alliancesgalore extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private DatabaseReference mUserDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();
    }


    public static Context getAppContext() {
        return context;
    }
}

