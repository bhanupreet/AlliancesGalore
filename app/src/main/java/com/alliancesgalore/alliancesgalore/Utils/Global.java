package com.alliancesgalore.alliancesgalore.Utils;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alliancesgalore.alliancesgalore.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Global extends Application {
    public static UserProfile myProfile;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in

        }
    }

    public ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                myProfile = dataSnapshot.getValue(UserProfile.class);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


}
