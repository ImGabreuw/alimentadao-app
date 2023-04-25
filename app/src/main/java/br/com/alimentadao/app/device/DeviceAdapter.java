package br.com.alimentadao.app.device;

import static android.graphics.Color.TRANSPARENT;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentadao.app.R;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private final List<DeviceItem> devices = new ArrayList<>();

    private int selected = RecyclerView.NO_POSITION;

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.device_item,
                        parent,
                        false
                );

        return new DeviceViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MaterialCardView cardViewDeviceContainer = holder.itemView.findViewById(R.id.cv_device_container);

        cardViewDeviceContainer.setStrokeColor(selected == position ? 0xFF03D8C3 : TRANSPARENT);
        cardViewDeviceContainer.setStrokeWidth(selected == position ? 3 : 0);

        DeviceItem device = devices.get(position);

        holder.imageViewDeviceType.setImageResource(device.getType().getIcon());
        holder.textViewDeviceName.setText(device.getName());
        holder.itemView.setOnClickListener(view -> {
            if (selected == position) return;

            selected = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addDevice(DeviceItem device) {
        if (devices.contains(device)) return;

        devices.add(device);
        notifyDataSetChanged();
    }

    public DeviceItem getSelectedDevice() {
        if (selected == -1) return null;

        return devices.get(selected);
    }

    public List<DeviceItem> getDevices() {
        return devices;
    }
}
