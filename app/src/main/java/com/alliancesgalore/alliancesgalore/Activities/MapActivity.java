package com.alliancesgalore.alliancesgalore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Adapters.MapInfoAdapter;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.DividerItemDecorator;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.SwipeToRefresh;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> mMapSelectionList;
    private MapView mMapView;
    private GoogleMap googleMap;
    private SwipeToRefresh mMapsRefresh;
    private LatLng MyLocation;
    private int pos;
    private LatLngBounds.Builder builder;
    private Boolean isMultiselect = false;
    private boolean isinitial = true;
    private LatLngBounds bounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapSelectionList = new ArrayList<>();
        mMapSelectionList = ObjectListIntent();

        FindIds(savedInstanceState);
        setMapRefreshListener();

        UserProfile obj = ObjectIntent();
        pos = mMapSelectionList.indexOf(obj);
        MyLocation = setLatLong(obj);
        LatLng location = MyLocation;

        isMultiselect = getIntent().getBooleanExtra("ismultiselect", false);

        if (isMultiselect) {
            setMultiselectMarkers();

        } else
            setdefault(obj, location);

//        setLocation(location);
        setmToolbar();
        setAdapter();
        RecyclerClick();
        setBottomSheetBehavior();
    }

    private void setMultiselectMarkers() {
        builder = new LatLngBounds.Builder();
        for (UserProfile profile : mMapSelectionList) {
            LatLng temp = new LatLng(profile.getLatitude(), profile.getLongitude());
            setdefault(profile, temp);
            builder.include(temp);
        }

        isinitial = true;

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
//        adapter.swap(pos, 0);
        adapter.notifyDataSetChanged();
    }

    private void setdefault(UserProfile obj, LatLng location) {
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            if (isMultiselect && isinitial) {
                bounds = builder.build();
//                mMap.setLatLngBoundsForCameraTarget(bounds);
                mMap.setPadding(150, 150, 150, 150);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                mMap.animateCamera(cu);
//                mMap.animateCamera(CameraUpdateFactory.zoomBy(-2));

            } else if (!isMultiselect) {
                googleMap.clear();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            mMap.setInfoWindowAdapter(new MapInfoAdapter(MapActivity.this));
            loadMarkerIcon(obj, mMap, location);


        });
    }

    private void setmToolbar() {
        Toolbar mToolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void RecyclerClick() {
        adapter.swap(0, pos);
        adapter.addItemClickListener(pos -> {
            bottomSheetBehavior.setState(STATE_COLLAPSED);
            UserProfile obj = mMapSelectionList.get(pos);
            LatLng Location = setLatLong(obj);
            Toast.makeText(MapActivity.this, mMapSelectionList.get(pos).getDisplay_name(), Toast.LENGTH_SHORT).show();
            setLocation(Location);

            if (!isMultiselect)
                setdefault(obj, Location);
            isinitial = false;

            adapter.swap(0, pos);
            Objects.requireNonNull(mRecycler.getLayoutManager()).scrollToPosition(0);
            sort(mMapSelectionList.subList(1, mMapSelectionList.size()));
            adapter.notifyDataSetChanged();
        });
    }

    private void sort(List<UserProfile> subordinatesList) {
        Collections.sort(subordinatesList, (t1, t2) -> t1.getDisplay_name().toLowerCase().compareTo(t2.getDisplay_name().toLowerCase()));
        Collections.sort(subordinatesList, (t1, t2) -> t1.getLevel() - (t2.getLevel()));
    }

    private void setLocation(LatLng location) {

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMapsRefresh.setRefreshing(false);
        });
    }

    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                switch (newState) {
                    case STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        adapter.notifyDataSetChanged();
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

    private SwipeRefreshLayout.OnRefreshListener MapRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            setLocation(MyLocation);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void loadMarkerIcon(UserProfile obj, GoogleMap mMap, LatLng location) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
        String time = formatter.format(new Date(Long.parseLong(obj.getLastUpdated().toString())));
        Marker marker2 = mMap.addMarker(new MarkerOptions()
                .position(location)
                .snippet("Last Updated: " + time)
                .title(obj.getDisplay_name()));
        marker2.setTag(obj);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Functions.toast(marker.getTitle(), MapActivity.this);

        marker.showInfoWindow();
        UserProfile profile = (UserProfile) marker.getTag();
        int pos = mMapSelectionList.indexOf(profile);
        LatLng Location = setLatLong(profile);
        setLocation(Location);
        adapter.swap(0, pos);
        adapter.notifyDataSetChanged();
        return true;
    }
}
