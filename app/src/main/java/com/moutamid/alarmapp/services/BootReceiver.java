package com.moutamid.alarmapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(new Intent(context, AlarmService.class));
            }else {
                context.startService(new Intent(context, AlarmService.class));
            }
        }
    }
}

