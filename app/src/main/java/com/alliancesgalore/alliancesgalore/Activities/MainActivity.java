package com.alliancesgalore.alliancesgalore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Fragments.CRMfragment;
import com.alliancesgalore.alliancesgalore.Fragments.LocationFragment;
import com.alliancesgalore.alliancesgalore.Adapters.MainActivityAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Fragments.RemindersFragment;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kotlin.Function;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private MainActivityAdapter adapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        FindIds();
        SetToolBar();
        Tabadapter();

    }

    private void FindIds() {
        mViewPager = findViewById(R.id.main_viewpager);
        mTabLayout = findViewById(R.id.main_tablayout);
        mToolbar = findViewById(R.id.main_app_bar);
    }

    private void SetToolBar() {
        setmToolbar(mToolbar, " Alliances Galore", R.mipmap.ic_launcher);
    }

    private void Tabadapter() {
        adapter = new MainActivityAdapter(getSupportFragmentManager());
        adapter.addFragment(new CRMfragment(), "CRM");
        adapter.addFragment(new LocationFragment(), "Location");
        adapter.addFragment(new RemindersFragment(), "Reminders");

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
    }

    private void setmToolbar(Toolbar mToolbar, String title, int Resid) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setLogo(Resid);
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void logout() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(SignOutonComplete);
    }

    private void settings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(settingsIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            sendToStart();
        } else {
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(valueEventListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.main_logout_btn:
                logout();
                break;
            case R.id.main_settings_btn:
                settings();
                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private OnCompleteListener SignOutonComplete = new OnCompleteListener<Void>() {
        public void onComplete(@NonNull Task<Void> task) {
            sendToStart();
        }
    };

    public ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Functions.myProfile = dataSnapshot.getValue(UserProfile.class);
                Toast.makeText(getApplicationContext(), Functions.myProfile.getEmail(), Toast.LENGTH_LONG).show();
            } else {
                sendToStart();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
