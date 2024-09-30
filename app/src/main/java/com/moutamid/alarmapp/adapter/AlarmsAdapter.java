package com.moutamid.alarmapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.moutamid.alarmapp.R;
import com.moutamid.alarmapp.models.AlarmModel;

import java.util.ArrayList;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.AlarmVH> {
    Context context;
    ArrayList<AlarmModel> list;

    public AlarmsAdapter(Context context, ArrayList<AlarmModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AlarmVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlarmVH(LayoutInflater.from(context).inflate(R.layout.alarm, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmVH holder, int position) {
        AlarmModel model = list.get(holder.getAdapterPosition());
        holder.title.setText(model.title);
        holder.description.setText(model.description);
        if (model.state == 0) {
            holder.main.setCardBackgroundColor(context.getResources().getColor(R.color.green_light));
        } else if (model.state == 1) {
            holder.main.setCardBackgroundColor(context.getResources().getColor(R.color.red_light));
        } else if (model.state == 2) {
            holder.main.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
        } else {
            holder.main.setCardBackgroundColor(context.getResources().getColor(R.color.grey));
        }
        if (model.enabled) {
            holder.enable.setImageResource(R.drawable.bell);
        } else {
            holder.enable.setImageResource(R.drawable.bell_slash);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AlarmVH extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView enable;
        CardView isEnable;
        MaterialCardView main;

        public AlarmVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            enable = itemView.findViewById(R.id.enable);
            isEnable = itemView.findViewById(R.id.isEnable);
            main = itemView.findViewById(R.id.main);
        }
    }

}
