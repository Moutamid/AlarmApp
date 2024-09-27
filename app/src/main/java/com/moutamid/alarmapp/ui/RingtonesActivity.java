package com.moutamid.alarmapp.ui;

import android.content.Intent;
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

    }
}