package com.alliancesgalore.alliancesgalore.Activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.alliancesgalore.alliancesgalore.Adapters.MainActivityAdapter;
import com.alliancesgalore.alliancesgalore.Fragments.CRMfragment;
import com.alliancesgalore.alliancesgalore.Fragments.LocationListFragment;
import com.alliancesgalore.alliancesgalore.Fragments.RemindersFragment;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Services.LocationService;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.WebViewPager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int YOUR_ENABLE_LOCATION_REQUEST_CODE = 100;
    private WebViewPager mViewPager;
    private TabLayout mTabLayout;
    public Toolbar mToolbar;
    int position;
    public CRMfragment crmFragment;
    public FloatingActionButton fab;
    AlertDialog alert;


    public static List<UserProfile> getmList() {
        return mList;
    }

    public static void setmList(List<UserProfile> mList) {
        MainActivity.mList = mList;
    }

    private static List<UserProfile> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        findIds();
        setToolBar();

        tabAdapter();
    }

    private void locationService() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert lm != null;

        boolean gps_enabled = false;
        boolean network_enabled = false;
        alert = new AlertDialog.Builder(MainActivity.this).setMessage("GPS not enabled")
                .setPositiveButton("Open Location Settings", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, YOUR_ENABLE_LOCATION_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }


        if (!gps_enabled && !network_enabled) {
            // notify user
            alert.show();

        } else {
            alert.dismiss();
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED)
                startTrackerService();

            else
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
        }
    }

    private void findIds() {
        mViewPager = findViewById(R.id.main_viewpager);
        mTabLayout = findViewById(R.id.main_tablayout);
        mToolbar = findViewById(R.id.main_app_bar);
        fab = findViewById(R.id.fab_locationlist);
        CRMfragment.count = 0;
    }

    private void setToolBar() {
        setmToolbar(mToolbar, " Alliances Galore", R.mipmap.ic_launcher);
    }

    private void tabAdapter() {
        MainActivityAdapter adapter = new MainActivityAdapter(getSupportFragmentManager());
        crmFragment = new CRMfragment();

        adapter.addFragment(crmFragment, "CRM");
        adapter.addFragment(new LocationListFragment(), "Location");
        adapter.addFragment(new RemindersFragment(), "Reminders");

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);

    }

    private void swappingAway() {

        fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_down);
        fab.startAnimation(animation);
    }

    private void selectedTabs() {
        //a bit animation of popping up.

        fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        fab.startAnimation(animation);
    }

    private void setmToolbar(Toolbar mToolbar, String title, int Resid) {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
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

    public void fabanim() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int state = 0;
            private boolean isFloatButtonHidden = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (!isFloatButtonHidden && state == 1 && positionOffset != 0.0) {
                    isFloatButtonHidden = true;
                    //hide floating action button
                    if (LocationListFragment.mActionmode != null) {
                        LocationListFragment.mActionmode.finish();
                        LocationListFragment.isMultiselect = false;
                    }
                    swappingAway();
//                    fab.hide();
                }
            }

            @Override
            public void onPageSelected(int position) {
                //reset floating
                setTab(position);

                if (state == 2) {
                    //have end in selected tab
                    isFloatButtonHidden = false;
                    selectedTabs();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //state 0 = nothing happen, state 1 = begining scrolling, state 2 = stop at selected tab.
                this.state = state;
                if (state == 0) {
                    isFloatButtonHidden = false;
                } else if (state == 2 && isFloatButtonHidden) {
                    //this only happen if user is swapping but swap back to current tab (cancel to change tab)
                    selectedTabs();
                }

            }
        });
    }

    private void setTab(int position) {
        if (LocationListFragment.mActionmode != null)
            LocationListFragment.mActionmode.finish();

        switch (position) {
            case 0:
            case 1:
                selectedTabs();
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_white_24dp, MainActivity.this.getTheme()));
                fab.setOnClickListener(v -> {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.thoughtcrime.securesms");
                    try {
                        assert launchIntent != null;
                        launchIntent
                                .setComponent(new ComponentName("org.thoughtcrime.securesms", "org.thoughtcrime.securesms.ConversationListActivity"));
                        startActivity(launchIntent);
                    } catch (Exception e) {
                        Functions.toast("AG-Chat Not Available", MainActivity.this);
                    }
                });

                break;
            case 2:
                selectedTabs();
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, MainActivity.this.getTheme()));
//                Functions.toast("set using onpageselected", MainActivity.this);
                fab.setOnClickListener(view -> {
                    Intent addIntent = new Intent(MainActivity.this, AddEventActivity.class);
                    startActivity(addIntent);
                });
                break;
        }
    }

    private void sendToReport() {

        Intent reportIntent = new Intent(MainActivity.this, ReportingToActivity.class);
        startActivity(reportIntent);
        finish();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        startTrackerService();
    }

    private void startTrackerService() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // only for newer versions
            Intent pushIntent = new Intent(this, LocationService.class);
            startForegroundService(pushIntent);
        } else {
            Intent pushIntent = new Intent(this, LocationService.class);
            startService(pushIntent);
        }
    }

    private OnCompleteListener SignOutonComplete = (OnCompleteListener<Void>) task -> sendToStart();

    public ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                myProfile = dataSnapshot.getValue(UserProfile.class);
                assert myProfile != null;
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

    @Override
    protected void onStart() {
        super.onStart();
        locationService();
        fabanim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = FirebaseAuth
                    .getInstance()
                    .getUid();

            assert uid != null;
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(valueEventListener);
        } else
            sendToStart();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fabanim();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

    @Override
    protected void onPause() {
        super.onPause();
        fabanim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationService();
        fabanim();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        alert.cancel();
        locationService();
        if (requestCode == YOUR_ENABLE_LOCATION_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user came back from the Enable Location Activity
                // Dismiss the Dialog
                alert.cancel();
                alert.dismiss();
            }
        }
    }

    public void getCurrentTabPosition() {
        if (mTabLayout != null) {
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    position = tab.getPosition();
                    setTab(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    position = tab.getPosition();
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        alert.dismiss();
        alert.cancel();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}