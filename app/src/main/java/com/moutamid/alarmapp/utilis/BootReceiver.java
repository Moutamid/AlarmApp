package com.moutamid.alarmapp.utilis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Enqueue the periodic work again after the reboot
            PeriodicWorkRequest alarmSyncWork = new PeriodicWorkRequest.Builder(AlarmSyncWorker.class, 30, TimeUnit.SECONDS).build();
            WorkManager.getInstance(context).enqueue(alarmSyncWork);
            Log.d("BootReceiver", "WorkManager re-enqueued after boot.");
        }
    }
}

