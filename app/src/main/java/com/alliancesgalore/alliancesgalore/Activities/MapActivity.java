package com.alliancesgalore.alliancesgalore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.DividerItemDecorator;
import com.alliancesgalore.alliancesgalore.Utils.SwipeToRefresh;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class MapActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> mMapSelectionList;
    MapView mMapView;
    private GoogleMap googleMap;
    private SwipeToRefresh mMapsRefresh;
    private LatLng MyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapSelectionList = new ArrayList<>();
        mMapSelectionList = ObjectListIntent();

        FindIds(savedInstanceState);
        setMapRefreshListener();

        UserProfile obj = ObjectIntent();
        MyLocation = setLatLong(obj);
        LatLng location = MyLocation;

        setdefault(obj, location);
        setLocation(location);
        setmToolbar();
        setAdapter();
        RecyclerClick();
        setBottomSheetBehavior();
    }

    private void setMapRefreshListener() {
        mMapsRefresh.setOnRefreshListener(MapRefreshListener);
    }

    private void FindIds(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapsRefresh = findViewById(R.id.mapsrefresh);
    }

    private void setAdapter() {
        mRecycler = findViewById(R.id.maplist_recyler);
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new UserProfileAdapter(this, mMapSelectionList);
        mRecycler.setAdapter(adapter);
        mRecycler.addItemDecoration(new DividerItemDecorator(this));
        adapter.notifyDataSetChanged();
    }

    private void setdefault(UserProfile obj, LatLng location) {
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.clear();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
            String time = formatter.format(new Date(Long.parseLong(obj.getLastUpdated().toString())));
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .snippet("Last Updated" + time)
                    .title(obj.getDisplay_name()));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });
    }

    private void setmToolbar() {
        Toolbar mToolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void RecyclerClick() {
        adapter.addItemClickListener(pos -> {
            bottomSheetBehavior.setState(STATE_COLLAPSED);
            UserProfile obj = mMapSelectionList.get(pos);
            LatLng Location = setLatLong(obj);
            Toast.makeText(MapActivity.this, mMapSelectionList.get(pos).getDisplay_name(), Toast.LENGTH_SHORT).show();
            setdefault(obj, Location);
            setLocation(Location);
            adapter.swap(0, pos);
            Objects.requireNonNull(mRecycler.getLayoutManager()).scrollToPosition(0);
            adapter.notifyDataSetChanged();
        });
    }

    private void setLocation(LatLng location) {
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMapsRefresh.setRefreshing(false);
        });
    }

    private SwipeRefreshLayout.OnRefreshListener MapRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            setLocation(MyLocation);
        }
    };

    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                switch (newState) {
                    case STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        adapter.notifyDataSetChanged();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
    }

    private UserProfile ObjectIntent() {
        return getIntent().getParcelableExtra("object");
    }

    private LatLng setLatLong(UserProfile obj) {
        LatLng returnthis = new LatLng(obj.getLatitude(), obj.getLongitude());
        MyLocation = returnthis;
        return returnthis;
    }

    private ArrayList<UserProfile> ObjectListIntent() {
        return getIntent().getParcelableArrayListExtra("objectlist");
    }
}
