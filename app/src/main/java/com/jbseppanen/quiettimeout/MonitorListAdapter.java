package com.jbseppanen.quiettimeout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MonitorListAdapter extends RecyclerView.Adapter<MonitorListAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView monitorDuration, monitorThreshold;
        ViewGroup parentView;
        int lastPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lastPosition = -1;
            monitorDuration = itemView.findViewById(R.id.element_duration);
            monitorThreshold = itemView.findViewById(R.id.element_threshold);
            parentView = itemView.findViewById(R.id.monitor_element_parent_layout);
        }
    }

    private ArrayList<Monitor> dataList;
    private Context context;
    private Activity activity;

    MonitorListAdapter(ArrayList<Monitor> dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
    }

    public void replaceList(ArrayList<Monitor> newData) {
        this.dataList.clear();
        this.dataList.addAll(newData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(
                viewGroup.getContext())
                .inflate(
                        R.layout.monitor_element_layout,
                        viewGroup,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Monitor data = dataList.get(i);

        viewHolder.monitorDuration.setText(String.valueOf(data.getDuration()));
        viewHolder.monitorThreshold.setText(String.valueOf(data.getThreshold()));
        viewHolder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RunMonitorActivity.class);
                intent.putExtra(RunMonitorActivity.RUN_MONITOR_KEY, data);
                activity.startActivity(intent);
            }
        });

        viewHolder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               MainActivity.viewModel.deleteMonitor(data);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
