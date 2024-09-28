package com.moutamid.alarmapp.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.fxn.stash.Stash;
import com.moutamid.alarmapp.R;
import com.moutamid.alarmapp.adapter.AlarmsAdapter;
import com.moutamid.alarmapp.databinding.ActivityAlarmBinding;
import com.moutamid.alarmapp.models.AlarmModel;
import com.moutamid.alarmapp.models.RingtoneModel;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS);
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO);
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        PeriodicWorkRequest alarmSyncWork = new PeriodicWorkRequest.Builder(AlarmSyncWorker.class, 30, TimeUnit.SECONDS).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(alarmSyncWork);

//        RingtoneModel tone5 = (RingtoneModel) Stash.getObject(Constants.System, RingtoneModel.class);
//        testRingtone(Uri.parse(tone5.tone));
    }

    public void testRingtone(Uri ringtoneUri) {
        Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        if (ringtone != null) {
            ringtone.play();
        } else {
            Log.d(TAG, "Failed to get ringtone from URI");
        }
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
        LocalBroadcastManager.getInstance(this).registerReceiver(alarmUpdateReceiver, new IntentFilter("com.moutamid.alarmapp.ACTION_UPDATE_ALARMS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(alarmUpdateReceiver);
    }

}