package br.com.alimentadao.app.time;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.alimentadao.app.R;

public class TimeAdapter extends RecyclerView.Adapter<TimeViewHolder> {

    private final List<TimeItem> timeCache;

    public TimeAdapter(List<TimeItem> times) {
        this.timeCache = times;
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
        TimeItem time = timeCache.get(position);

        holder.textViewTime.setText(time.getFormattedTime());
        holder.buttonRemove.setOnClickListener(view -> remove(time));
    }

    @Override
    public int getItemCount() {
        return timeCache.size();
    }

    public List<TimeItem> getTimeCache() {
        return timeCache;
    }

    public void add(TimeItem time) {
        if (timeCache.contains(time)) return;

        timeCache.add(time);
        notifyItemInserted(timeCache.size() - 1);
    }

    public void remove(TimeItem time) {
        if (time == null) return;

        int position = timeCache.indexOf(time);

        if (position == -1) return;

        timeCache.remove(position);
        notifyItemRemoved(position);
    }
}



