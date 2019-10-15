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
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Fragments.CRMfragment;
import com.alliancesgalore.alliancesgalore.Adapters.MainActivityAdapter;
import com.alliancesgalore.alliancesgalore.Fragments.LocationListFragment;
import com.alliancesgalore.alliancesgalore.Fragments.testfragment;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Fragments.RemindersFragment;
import com.alliancesgalore.alliancesgalore.Services.LocationService;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 100;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    public Toolbar mToolbar;
    public FloatingActionButton fab;

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
        fab = findViewById(R.id.fab_locationlist);
        CRMfragment.count = 0;
    }

    private void SetToolBar() {
        setmToolbar(mToolbar, " Alliances Galore", R.mipmap.ic_launcher);
    }

    private void Tabadapter() {
        MainActivityAdapter adapter = new MainActivityAdapter(getSupportFragmentManager());
        adapter.addFragment(new CRMfragment(), "CRM");
        adapter.addFragment(new LocationListFragment(), "Location");
        adapter.addFragment(new testfragment(), "Reminders");

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

    private void selectedTabs(int tab) {
        fab.show();
        //a bit animation of popping up.
        fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        fab.startAnimation(animation);
        switch (tab) {
            case 0:
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_chat_white_24dp));
                break;
            case 1:
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_playlist_add_check_black_24dp));

                break;
            default:
                tab = 0;
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist_add_check_black_24dp, MainActivity.this.getTheme()));
        }
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

    private void fabanim() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int state = 0;
            private boolean isFloatButtonHidden = false;
            private int position = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (isFloatButtonHidden == false && state == 1 && positionOffset != 0.0) {
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
                this.position = position;
                switch (position) {
                    case 0:
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_white_24dp, MainActivity.this.getTheme()));
                        break;
                    case 1:
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist_add_check_black_24dp, MainActivity.this.getTheme()));
                        break;
                    default:
                        position = 1;
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist_add_check_black_24dp, MainActivity.this.getTheme()));

                }
                if (state == 2) {
                    //have end in selected tab
                    isFloatButtonHidden = false;
                    selectedTabs(position);

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
                    selectedTabs(position);
                }

            }
        });
    }

    private void sendToReport() {
        Intent reportIntent = new Intent(MainActivity.this, ReportingToActivity.class);
        startActivity(reportIntent);
        finish();
    }

    private void startTrackerService() {
        startService(new Intent(this, LocationService.class));
    }

    private OnCompleteListener SignOutonComplete = (OnCompleteListener<Void>) task -> sendToStart();

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

    @Override
    protected void onStart() {
        super.onStart();
        fabanim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(valueEventListener);
        } else
            sendToStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fabanim();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

}
