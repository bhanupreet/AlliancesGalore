package com.alliancesgalore.alliancesgalore.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestLocationUpdates();
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void requestLocationUpdates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            LocationRequest request = new LocationRequest();
            request.setInterval(5000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED)
                client.requestLocationUpdates(request, locationCallback, null);
        }
    }


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Calendar calendar = Calendar.getInstance();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);

            Calendar endTime = Calendar.getInstance();
            endTime.set(mYear, mMonth, mDay, 18, 30, 0);

            Calendar startTime = Calendar.getInstance();
            startTime.set(mYear, mMonth, mDay, 10, 1, 0);

            Location location = locationResult.getLastLocation();

            long cal = System.currentTimeMillis();

            if (location != null && cal > startTime.getTimeInMillis() && cal < endTime.getTimeInMillis()) {
                Functions.toast("during work hours", getApplicationContext());
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("Latitude", location.getLatitude());
                userMap.put("Longitude", location.getLongitude());
                userMap.put("LastUpdated", ServerValue.TIMESTAMP);
                String uid = FirebaseAuth.getInstance().getUid();
                if (uid != null)
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(userMap);


            } else {
                Functions.toast("not during work hours", getApplicationContext());
                stopSelf();
            }
        }
    };
}
