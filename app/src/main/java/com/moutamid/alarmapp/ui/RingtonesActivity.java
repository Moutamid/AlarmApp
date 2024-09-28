package com.moutamid.alarmapp.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.alarmapp.R;
import com.moutamid.alarmapp.databinding.ActivityRingtonesBinding;
import com.moutamid.alarmapp.models.ApiData;
import com.moutamid.alarmapp.models.RingtoneModel;
import com.moutamid.alarmapp.utilis.Constants;

public class RingtonesActivity extends AppCompatActivity {
    ActivityRingtonesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRingtonesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ApiData data = (ApiData) Stash.getObject(Constants.API_DATA, ApiData.class);
        binding.clientID.setText("ClientID : " + data.clientId);

        binding.back.setOnClickListener(v -> finish());

        binding.logout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Stash.clear(Constants.API_DATA);
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    })
                    .show();
        });

        RingtoneModel tone1 = (RingtoneModel) Stash.getObject(Constants.Standard, RingtoneModel.class);
        RingtoneModel tone2 = (RingtoneModel) Stash.getObject(Constants.High, RingtoneModel.class);
        RingtoneModel tone3 = (RingtoneModel) Stash.getObject(Constants.Critical, RingtoneModel.class);
        RingtoneModel tone4 = (RingtoneModel) Stash.getObject(Constants.Emergency, RingtoneModel.class);
        RingtoneModel tone5 = (RingtoneModel) Stash.getObject(Constants.System, RingtoneModel.class);

        if (tone1 != null) {
            binding.ringtone1.setText("Ringtone 1: " + tone1.name);
        }
        if (tone2 != null) {
            binding.ringtone2.setText("Ringtone 2: " + tone2.name);
        }
        if (tone3 != null) {
            binding.ringtone3.setText("Ringtone 3: " + tone3.name);
        }
        if (tone4 != null) {
            binding.ringtone4.setText("Ringtone 4: " + tone4.name);
        }
        if (tone5 != null) {
            binding.ringtone5.setText("Ringtone 5: " + tone5.name);
        }


        binding.ringtone1.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, 1001);
        });

        binding.ringtone2.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, 1002);
        });

        binding.ringtone3.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, 1003);
        });

        binding.ringtone4.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, 1004);
        });

        binding.ringtone5.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, 1005);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                Stash.put(Constants.Standard, new RingtoneModel(ringtone.getTitle(this), ringtoneUri.toString()));
                ringtone.play();
                binding.ringtone1.setText(ringtone.getTitle(this));
            }
        } else if (requestCode == 1002 && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                Stash.put(Constants.High, new RingtoneModel(ringtone.getTitle(this), ringtoneUri.toString()));
                ringtone.play();
                binding.ringtone2.setText(ringtone.getTitle(this));
            }
        } else if (requestCode == 1003 && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                // Optionally, play the ringtone for preview
                Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                ringtone.play();
                Stash.put(Constants.Critical, new RingtoneModel(ringtone.getTitle(this), ringtoneUri.toString()));
                binding.ringtone3.setText(ringtone.getTitle(this));
            }
        } else if (requestCode == 1004 && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                // Optionally, play the ringtone for preview
                Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                ringtone.play();
                Stash.put(Constants.Emergency, new RingtoneModel(ringtone.getTitle(this), ringtoneUri.toString()));
                binding.ringtone4.setText(ringtone.getTitle(this));
            }
        } else if (requestCode == 1005 && resultCode == RESULT_OK) {
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtoneUri != null) {
                // Optionally, play the ringtone for preview
                Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
                ringtone.play();
                Stash.put(Constants.System, new RingtoneModel(ringtone.getTitle(this), ringtoneUri.toString()));
                binding.ringtone5.setText(ringtone.getTitle(this));
            }
        }
    }


}