package com.zoan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListSensorAdapter extends RecyclerView.Adapter<ListSensorAdapter.viewHolder> {

    private ArrayList<Sensor> listSensor;

    public ListSensorAdapter(ArrayList<Sensor> listSensor) {
        this.listSensor = listSensor;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_sensor,parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Sensor sensor = listSensor.get(position);


    }

    @Override
    public int getItemCount() {
        return listSensor.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvValue;
        TextView tvType;
        TextView tvDes;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_item_photo);
            tvValue = itemView.findViewById(R.id.tv_item_value);
            tvType = itemView.findViewById(R.id.tv_item_name);
            tvDes = itemView.findViewById(R.id.tv_item_des);
        }
    }
}
