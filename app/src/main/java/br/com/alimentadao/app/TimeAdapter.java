package br.com.alimentadao.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {

    private final List<String> timeCache;
    private final OnTimeRemoveListener onTimeRemoveListener;

    public TimeAdapter(List<String> timeCache, OnTimeRemoveListener onTimeRemoveListener) {
        this.timeCache = timeCache;
        this.onTimeRemoveListener = onTimeRemoveListener;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.list_item_time,
                        parent,
                        false
                );

        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        String time = timeCache.get(position);

        holder.textViewTime.setText(time);
        holder.buttonRemove.setOnClickListener(view -> onTimeRemoveListener.onTimeRemoved(time));
    }

    @Override
    public int getItemCount() {
        return timeCache.size();
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTime;
        ImageButton buttonRemove;

        TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }

    public interface OnTimeRemoveListener {
        void onTimeRemoved(String time);
    }
}



