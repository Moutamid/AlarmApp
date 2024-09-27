package com.moutamid.alarmapp.utilis;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.moutamid.alarmapp.models.AlarmModel;
import com.moutamid.alarmapp.models.ApiData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlarmSyncWorker extends Worker {

    public AlarmSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Perform your API call here using the same code from your activity class
        RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        ApiData data = (ApiData) Stash.getObject(Constants.API_DATA, ApiData.class);
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
                    model.__v = object.getInt("__v");
                    list.add(model);
                }

                // Save the list to a shared storage, database, or notify observers via LiveData
                // Use Stash or a local database like Room for this purpose
                Stash.put(Constants.ALARM_LIST, list);

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

        requestQueue.add(objectRequest);
        return Result.success();
    }
}

