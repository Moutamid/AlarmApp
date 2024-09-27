package com.moutamid.alarmapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.fxn.stash.Stash;
import com.moutamid.alarmapp.R;
import com.moutamid.alarmapp.adapter.AlarmsAdapter;
import com.moutamid.alarmapp.databinding.ActivityAlarmBinding;
import com.moutamid.alarmapp.models.AlarmModel;
import com.moutamid.alarmapp.utilis.AlarmSyncWorker;
import com.moutamid.alarmapp.utilis.Constants;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AlarmActivity extends AppCompatActivity {
    ActivityAlarmBinding binding;
    private static final String TAG = "AlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.setting.setOnClickListener(v -> {
            startActivity(new Intent(this, RingtonesActivity.class));
        });

        binding.alarmsRC.setLayoutManager(new LinearLayoutManager(this));
        binding.alarmsRC.setHasFixedSize(false);

        PeriodicWorkRequest alarmSyncWork =
                new PeriodicWorkRequest.Builder(AlarmSyncWorker.class, 30, TimeUnit.SECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(alarmSyncWork);

        // Broadcast the new data
        Intent intent = new Intent("com.moutamid.alarmapp.ACTION_UPDATE_ALARMS");
        sendBroadcast(intent);

/*
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        ApiData data = (ApiData) Stash.getObject(Constants.API_DATA, ApiData.class);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, data.link, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                Log.d(TAG, "onCreate: size " + jsonArray.length());
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
                    model.__v = object.getInt("__v");
                    list.add(model);
                }
                AlarmsAdapter adapter = new AlarmsAdapter(this, list);
                binding.alarmsRC.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
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
        requestQueue.add(objectRequest);*/
    }

    BroadcastReceiver alarmUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Fetch the latest data from Stash or database and update RecyclerView
            Log.d(TAG, "onReceive: UPDATED");
            ArrayList<AlarmModel> updatedList = Stash.getArrayList(Constants.ALARM_LIST, AlarmModel.class);
            AlarmsAdapter adapter = new AlarmsAdapter(AlarmActivity.this, updatedList);
            binding.alarmsRC.setAdapter(adapter);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(alarmUpdateReceiver, new IntentFilter("com.moutamid.alarmapp.ACTION_UPDATE_ALARMS"), Context.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(alarmUpdateReceiver);
    }

}