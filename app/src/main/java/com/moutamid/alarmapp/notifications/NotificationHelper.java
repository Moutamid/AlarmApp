package com.moutamid.alarmapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fxn.stash.Stash;
import com.moutamid.alarmapp.R;
import com.moutamid.alarmapp.models.RingtoneModel;
import com.moutamid.alarmapp.utilis.Constants;

import java.util.Random;

public class NotificationHelper extends ContextWrapper {
    private static final String TAG = "NotificationHelper";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    private final String CHANNEL_NAME = "Notifications";
    private final String CHANNEL_ID = "com.moutamid.alarmapp.notifications" + CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("This is used to show user important notifications about app");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public void sendHighPriorityNotification(String title, String body, Class activityName, int priority) {
        Intent intent = new Intent(this, activityName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_IMMUTABLE);//FLAG_UPDATE_CURRENT

        RingtoneModel tone1 = (RingtoneModel) Stash.getObject(Constants.Standard, RingtoneModel.class);
        RingtoneModel tone2 = (RingtoneModel) Stash.getObject(Constants.High, RingtoneModel.class);
        RingtoneModel tone3 = (RingtoneModel) Stash.getObject(Constants.Critical, RingtoneModel.class);
        RingtoneModel tone4 = (RingtoneModel) Stash.getObject(Constants.Emergency, RingtoneModel.class);
        RingtoneModel tone5 = (RingtoneModel) Stash.getObject(Constants.System, RingtoneModel.class);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        switch (priority) {
            case 1:
                if (tone1 != null) {
                    soundUri = Uri.parse(tone1.tone);
                }
                break;
            case 2: // High Priority Notification
                if (tone2 != null) {
                    soundUri = Uri.parse(tone2.tone);
                }
                break;
            case 3: // Critical Notification
                if (tone3 != null) {
                    soundUri = Uri.parse(tone3.tone);
                }
                break;
            case 4: // Emergency Notification
                if (tone4 != null) {
                    soundUri = Uri.parse(tone4.tone);
                }
                break;
            case 5: // System Notification
                if (tone5 != null) {
                    soundUri = Uri.parse(tone5.tone);
                    Log.d(TAG, "sendHighPriorityNotification: tone5  " + soundUri);
                }
                break;
            default:
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                break;
        }
        Log.d(TAG, "sendHighPriorityNotification: URI " + soundUri);

        if (priority != 0) {
            Ringtone ringtone = RingtoneManager.getRingtone(this, soundUri);
            if (ringtone != null) {
                ringtone.play();
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setContentText(body)
                .setSilent(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please allow notification permission!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);
    }
}