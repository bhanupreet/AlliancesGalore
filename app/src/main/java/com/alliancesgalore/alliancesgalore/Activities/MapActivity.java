package com.alliancesgalore.alliancesgalore.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alliancesgalore.alliancesgalore.Adapters.MapInfoAdapter;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
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

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.from;

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
    private Boolean isMultiSelect = false;
    private boolean initial = true;
    private LatLngBounds bounds;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapSelectionList = new ArrayList<>();
        mMapSelectionList = objectListIntent();

        FindIds(savedInstanceState);
        setMapRefreshListener();

        UserProfile obj = objectIntent();
        pos = mMapSelectionList.indexOf(obj);
        MyLocation = setLatLong(obj);
        LatLng location = MyLocation;

        isMultiSelect = getIntent().getBooleanExtra("ismultiselect", false);

        if (isMultiSelect) {
            setMultiSelectMarkers();

        } else
            setDefault(obj, location);

//        setLocation(location);
        setToolbar();
        setAdapter();
        recyclerClick();
        setBottomSheetBehavior();
        mRecycler.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });

    }

    private void setMultiSelectMarkers() {
        builder = new LatLngBounds.Builder();
        for (UserProfile profile : mMapSelectionList) {
            LatLng temp = new LatLng(profile.getLatitude(), profile.getLongitude());
            setDefault(profile, temp);
            builder.include(temp);
        }
        initial = true;
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
//        mRecycler.addItemDecoration(new DividerItemDecorator(this));
//        adapter.swap(pos, 0);
    }

    private void setDefault(UserProfile obj, LatLng location) {
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            if (isMultiSelect && initial) {
                bounds = builder.build();
//                mMap.setLatLngBoundsForCameraTarget(bounds);
                mMap.setPadding(150, 150, 10, 200);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                mMap.animateCamera(cu);
//                mMap.animateCamera(CameraUpdateFactory.zoomBy(-2));

            } else if (!isMultiSelect) {
                googleMap.clear();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            mMap.setInfoWindowAdapter(new MapInfoAdapter(MapActivity.this));
            loadMarkerIcon(obj, mMap, location);


        });
    }

    private void setToolbar() {
        Toolbar mToolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void recyclerClick() {
        adapter.swap(0, pos);
        adapter.addItemClickListener(pos -> {
            bottomSheetBehavior.setState(STATE_COLLAPSED);
            UserProfile obj = mMapSelectionList.get(pos);
            LatLng Location = setLatLong(obj);
//            Toast.makeText(MapActivity.this, mMapSelectionList.get(pos).getDisplay_name(), Toast.LENGTH_SHORT).show();
            setLocation(Location);

            if (!isMultiSelect)
                setDefault(obj, Location);
            initial = false;

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
        bottomSheetBehavior = from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {
                //DONT TOUCH IT, TOUCHING IT BREAKS THE SCROLL ON BOTTOM SHEET
                switch (newState) {
                    case STATE_HALF_EXPANDED:

                    case STATE_SETTLING:
                    case STATE_HIDDEN:
                    case STATE_COLLAPSED:
                        mRecycler.smoothScrollToPosition(0);
                        mRecycler.setNestedScrollingEnabled(false);
                        mRecycler.setEnabled(false);
                        mRecycler.setClickable(false);
                        adapter.notifyDataSetChanged();
                        mRecycler.setLayoutFrozen(true);

                        break;
                    case STATE_EXPANDED:
                        mRecycler.setClickable(true);
//                        mRecycler.set
                        mRecycler.setNestedScrollingEnabled(true);
                        mRecycler.setEnabled(true);
                        adapter.notifyDataSetChanged();
                        mRecycler.setLayoutFrozen(false);
                        break;
                    case STATE_DRAGGING:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + newState);
                }

            }

            @Override
            public void onSlide(@NotNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private UserProfile objectIntent() {
        return getIntent().getParcelableExtra("object");
    }

    private LatLng setLatLong(UserProfile obj) {
        LatLng returnThis = new LatLng(obj.getLatitude(), obj.getLongitude());
        MyLocation = returnThis;
        return returnThis;
    }

    private ArrayList<UserProfile> objectListIntent() {
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
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return false;
    }

    private void loadMarkerIcon(UserProfile obj, GoogleMap mMap, LatLng location) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mma");
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
//        Functions.toast(marker.getTitle(), MapActivity.this);
        marker.showInfoWindow();
        UserProfile profile = (UserProfile) marker.getTag();
        int pos = mMapSelectionList.indexOf(profile);
        assert profile != null;
        LatLng Location = setLatLong(profile);
        setLocation(Location);
        adapter.swap(0, pos);
        adapter.notifyDataSetChanged();
        return true;
    }

}
