package com.alliancesgalore.alliancesgalore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Adapters.MapInfoAdapter;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.DividerItemDecorator;
import com.alliancesgalore.alliancesgalore.Utils.SwipeToRefresh;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.alliancesgalore.alliancesgalore.Utils.Functions.getCircularBitmap;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> mMapSelectionList;
    MapView mMapView;
    private GoogleMap googleMap;
    private SwipeToRefresh mMapsRefresh;
    private LatLng MyLocation;
    private ArrayList<Marker> markers = new ArrayList<>();
    private int pos;
    private Boolean isMultiselect = false;
    private Marker marker2;
    private HashMap<UserProfile, LatLng> map;
    private MapInfoAdapter mapInfoAdapter;


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
            for (UserProfile profile : mMapSelectionList) {
                LatLng temp = new LatLng(profile.getLatitude(), profile.getLongitude());
                setdefault(profile, temp);
            }
        } else
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
//        adapter.swap(pos, 0);
        adapter.notifyDataSetChanged();
    }

    private void setdefault(UserProfile obj, LatLng location) {
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            if (!isMultiselect)
                googleMap.clear();


//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(location)
//                    .snippet("Last Updated: " + time)
//                    .title(obj.getDisplay_name()));
            mMap.setInfoWindowAdapter(new MapInfoAdapter(MapActivity.this, obj.getImage()));
            mMap.setOnMarkerClickListener(this);
            loadMarkerIcon(obj, mMap, location);
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

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (context != null) {
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(displayMetrics);
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels,
                    displayMetrics.heightPixels);
            view.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                    view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
        return null;
    }

    private void loadMarkerIcon(UserProfile obj, GoogleMap mMap, LatLng location) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
        String time = formatter.format(new Date(Long.parseLong(obj.getLastUpdated().toString())));
        Picasso.get().load(obj.getImage()).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).resize(100, 100).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap mBitmap = getCircularBitmap(bitmap);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mBitmap);
                Marker marker2 = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .snippet("Last Updated: " + time)
                        .title(obj.getDisplay_name()));
//                        .icon(icon));
                marker2.setTag(obj);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Marker marker2 = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .snippet("Last Updated: " + time)
                        .title(obj.getDisplay_name()));
                marker2.setTag(obj);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                marker2 = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .snippet("Last Updated: " + time)
                        .title(obj.getDisplay_name()));
                marker2.setTag(obj.getEmail());
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            marker.showInfoWindow();
        }
        return true;
    }
}
