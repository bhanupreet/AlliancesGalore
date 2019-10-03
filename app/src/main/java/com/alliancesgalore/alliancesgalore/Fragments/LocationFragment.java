package com.alliancesgalore.alliancesgalore.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Services.LocationService;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.alliancesgalore.alliancesgalore.Utils.SwipeToRefresh;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.core.content.ContextCompat.getSystemService;


public class LocationFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    private static final int PERMISSIONS_REQUEST = 100;
    MapView mMapView;
    private GoogleMap googleMap;
    private SwipeToRefresh mMapsRefresh;
    private LatLng MyLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        FindIds(view, savedInstanceState);
        mMapsRefresh.setOnRefreshListener(MapRefrshListener);
        return view;
    }

    private void FindIds(View view, Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapsRefresh = view.findViewById(R.id.mapsrefresh);

    }

    private void setLocation(final LatLng MyLocation) {

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);
                if (Global.myProfile.getLastUpdated() != null) {
                    googleMap = mMap;
                    googleMap.setMyLocationEnabled(true);
                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
                    String time = formatter.format(new Date(Long.parseLong(Global.myProfile.getLastUpdated().toString())));
//                    marker.setPosition(MyLocation);
//                    marker.setTitle("Location");
//                    marker.setSnippet("Last Updated: " + time);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(MyLocation).zoom(14).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMapsRefresh.setRefreshing(false);
                }
            }
        });
    }

    private SwipeRefreshLayout.OnRefreshListener MapRefrshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (Global.myProfile != null && Global.myProfile.getLastUpdated() != null) {
                MyLocation = new LatLng(Global.myProfile.getLatitude(), Global.myProfile.getLongitude());
                setLocation(MyLocation);
            } else {
                mMapsRefresh.setRefreshing(false);
            }

        }
    };

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
