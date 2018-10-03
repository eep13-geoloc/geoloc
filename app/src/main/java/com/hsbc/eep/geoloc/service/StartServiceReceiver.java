package com.hsbc.eep.geoloc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsbc.eep.geoloc.util.Util;

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StartServiceReceiver", "Got intent" + intent.getAction());
        Util.scheduleJob(context);
    }
}
