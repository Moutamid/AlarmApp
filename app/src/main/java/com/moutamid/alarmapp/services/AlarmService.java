package com.moutamid.alarmapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.moutamid.alarmapp.R;
import com.moutamid.alarmapp.models.AlarmModel;
import com.moutamid.alarmapp.models.ApiData;
import com.moutamid.alarmapp.notifications.NotificationHelper;
import com.moutamid.alarmapp.ui.AlarmActivity;
import com.moutamid.alarmapp.utilis.Constants;
import com.moutamid.alarmapp.utilis.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    RequestQueue requestQueue;
    private static final String TAG = "AlarmService";
    Context context;
    private Handler handler = new Handler();
    private Runnable apiCaller;
    private static final String CHANNEL_ID = "AlarmManagerChannel";
    private static final String CHANNEL_NAME = "Alarm Manager";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        startForeground(NOTIFICATION_ID, createNotification());
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        return START_STICKY;
    }

    private Notification createNotification() {
        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Running in background...")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build();
        } else {
            return new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Running in background...")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void callAPI() {
        ApiData data = (ApiData) Stash.getObject(Constants.API_DATA, ApiData.class);
        if (data != null) {
            if (data.link != null) {
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, data.link, null, response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        ArrayList<AlarmModel> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            AlarmModel model = new AlarmModel();
                            model._id = object.getString("_id");
                            model.title = object.getString("title");
                            model.source = object.getString("source");
                            model.description = object.getString("description");
                            model.shortDescription = object.getString("shortDescription");
                            model.alarmText = object.getString("alarmText");
                            model.enabled = object.getBoolean("enabled");
                            model.priority = object.getInt("priority");
                            model.state = object.getInt("state");
                            model.type = object.getInt("type");
                            model.notificationId = object.getInt("notificationId");
                            model.__v = object.getInt("__v");
                            list.add(model);

                            if (model.enabled)
                                new NotificationHelper(getApplicationContext()).sendHighPriorityNotification(model.title, model.shortDescription, AlarmActivity.class, model.priority, model.notificationId, model.state);
                        }
                        Stash.put(Constants.ALARM_LIST, list);
                        Intent intent = new Intent("com.moutamid.alarmapp.ACTION_UPDATE_ALARMS");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> params = new HashMap<>();
                        String creds = String.format("%s:%s", data.clientId, data.clientSecret);
                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                        params.put("Authorization", auth);
                        return params;
                    }
                };
                requestQueue.add(objectRequest);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startApiCallLoop();
    }

    private void startApiCallLoop() {
        apiCaller = new Runnable() {
            @Override
            public void run() {
                callAPI();
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(apiCaller);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(apiCaller);
        stopForeground(true);
    }

}
