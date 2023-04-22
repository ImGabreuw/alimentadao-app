package br.com.alimentadao.app.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentadao.app.R;
import br.com.alimentadao.app.WelcomeActivity;
import br.com.alimentadao.app.bluetooth.BluetoothService;

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

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView cardViewDeviceContainer = holder.itemView.findViewById(R.id.cv_device_container);

        DeviceItem device = devices.get(position);

        holder.imageViewDeviceType.setImageResource(device.getType().getCode());
        holder.textViewDeviceName.setText(device.getName());

        holder.itemView.setOnClickListener(view -> {
            int previousSelected = selected;
            selected = position;

            notifyItemChanged(previousSelected);
            notifyItemChanged(previousSelected);
        });

        if (selected == position) {
            cardViewDeviceContainer.setCardBackgroundColor(0x793C3F40);

            BluetoothService bluetoothService = WelcomeActivity.getInstance().getBluetoothService();

            BluetoothDevice bluetoothDevice = bluetoothService.findByName(device.getName());
            bluetoothService.connectToDevice(bluetoothDevice);
        } else {
            cardViewDeviceContainer.setCardBackgroundColor(0x273C3F40);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(DeviceItem device) {
        if (devices.contains(device)) return;

        devices.add(device);
    }

    public DeviceItem getSelectedDevice() {
        if (selected == -1) return null;

        return devices.get(selected);
    }

    public List<DeviceItem> getDevices() {
        return devices;
    }
}
