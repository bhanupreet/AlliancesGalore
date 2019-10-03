package com.alliancesgalore.alliancesgalore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Fragments.CRMfragment;
import com.alliancesgalore.alliancesgalore.Fragments.LocationFragment;
import com.alliancesgalore.alliancesgalore.Adapters.MainActivityAdapter;
import com.alliancesgalore.alliancesgalore.Fragments.LocationListFragment;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Fragments.RemindersFragment;
import com.alliancesgalore.alliancesgalore.Services.LocationService;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kotlin.Function;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 100;
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
        LocationService();
        Tabadapter();
    }

    private void LocationService() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    private void FindIds() {
        mViewPager = findViewById(R.id.main_viewpager);
        mTabLayout = findViewById(R.id.main_tablayout);
        mToolbar = findViewById(R.id.main_app_bar);
        CRMfragment.count = 0;
    }

    private void SetToolBar() {
        setmToolbar(mToolbar, " Alliances Galore", R.mipmap.ic_launcher);
    }

    private void Tabadapter() {
        adapter = new MainActivityAdapter(getSupportFragmentManager());
        adapter.addFragment(new CRMfragment(), "CRM");
        if (myProfile != null && myProfile.getLevel() == 30) {
            adapter.addFragment(new LocationFragment(), "Location");
        } else {
            adapter.addFragment(new LocationListFragment(), "Location");
        }
        adapter.addFragment(new RemindersFragment(), "Reminders");

        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(valueEventListener);
        } else
            sendToStart();
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
                myProfile = dataSnapshot.getValue(UserProfile.class);
                if (TextUtils.isEmpty(myProfile.getReportingTo())) {
                    sendToReport();
                }
//                Toast.makeText(getApplicationContext(),"got details of user",Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private void sendToReport() {
        Intent startIntent = new Intent(MainActivity.this, ReportingToActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTrackerService() {
        startService(new Intent(this, LocationService.class));
    }
}
