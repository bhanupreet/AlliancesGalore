package com.alliancesgalore.alliancesgalore.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            Log.d(TAG, "received stop broadcast");
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void requestLocationUpdates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences settings = getSharedPreferences("location", 0);
        String silent = settings.getString("locationservice", "on");

        if (user != null && silent.equals("on")) {
            LocationRequest request = new LocationRequest();
            request.setInterval(5000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                client.requestLocationUpdates(request, locationCallback, null);
            }
        }
        if (silent.equals("off")) {
            stopSelf();
        }

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, 18);
            endTime.set(Calendar.MINUTE, 30);

            Calendar startTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, 10);
            endTime.set(Calendar.MINUTE, 1);


            Location location = locationResult.getLastLocation();
            SharedPreferences settings = getSharedPreferences("location", 0);
            String silent = settings.getString("locationservice", "on");
            DateFormat simple = new SimpleDateFormat("hh:mm a");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getTime().getHours());
            cal.set(Calendar.MINUTE, Calendar.getInstance().getTime().getMinutes());

            if (location != null
                    && (cal.getTimeInMillis() > endTime.getTimeInMillis()

                    || cal.getTimeInMillis() < startTime.getTimeInMillis())) {

                Functions.toast(simple.format(cal.getTimeInMillis()), getApplicationContext());
                Log.d(TAG, "location update " + location);
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("Latitude", location.getLatitude());
                userMap.put("Longitude", location.getLongitude());
                userMap.put("LastUpdated", ServerValue.TIMESTAMP);
                String uid = FirebaseAuth.getInstance().getUid();
                if (uid != null)
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(userMap);
            }
            if (location != null
                    && (cal.getTimeInMillis() < endTime.getTimeInMillis()
//
                    || cal.getTimeInMillis() > startTime.getTimeInMillis())) {
                stopSelf();
            }
        }
    };
}
