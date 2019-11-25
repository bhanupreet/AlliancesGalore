package com.alliancesgalore.alliancesgalore.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.R;
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

    private static final long UPDATE_INTERVAL = 5000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildnotif();
        requestLocationUpdates();
//        buildNotification();
    }

    private void buildnotif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else {
            Intent notificationIntent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Alliances Galore")
                    .setContentText("App is running in background")
                    .setContentIntent(pendingIntent).build();
            startForeground(1337, notification);
        }

    }

    private void requestLocationUpdates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            LocationRequest request = new LocationRequest();
            request.setInterval(UPDATE_INTERVAL);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED)
                client.requestLocationUpdates(request, locationCallback, null);
        } else {
            stopSelf();
            stopForeground(true);
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
//                Functions.toast("during work hours", getApplicationContext());
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("Latitude", location.getLatitude());
                userMap.put("Longitude", location.getLongitude());
                userMap.put("LastUpdated", ServerValue.TIMESTAMP);
                String uid = FirebaseAuth.getInstance().getUid();
                if (uid != null)
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Users")
                            .child(uid)
                            .updateChildren(userMap);


            } else {
//                Functions.toast("not during work hours", getApplicationContext());
                stopSelf();
                stopForeground(true);

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(final Intent intent,
                              final int flags,
                              final int startId) {

        //your code
        buildnotif();
        requestLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
        buildnotif();
        requestLocationUpdates();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //create an intent that you want to start again.
        buildnotif();
        requestLocationUpdates();
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

}
