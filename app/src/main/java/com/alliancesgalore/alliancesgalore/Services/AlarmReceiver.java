package com.alliancesgalore.alliancesgalore.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            //Toast.makeText(context, context.getString(R.string.Alertnotifty) + intent.getStringExtra("title") , Toast.LENGTH_LONG).show();
            String Title = intent.getStringExtra(context.getString(R.string.title));
            Intent x = new Intent(context, MainActivity.class);
            x.putExtra(context.getString(R.string.title), Title);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(x);
        }
    }
}