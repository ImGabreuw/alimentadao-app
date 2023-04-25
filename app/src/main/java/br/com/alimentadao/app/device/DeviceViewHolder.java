package br.com.alimentadao.app.device;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.alimentadao.app.R;

public class DeviceViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageViewDeviceType;
    public final TextView textViewDeviceName;

    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewDeviceType = itemView.findViewById(R.id.iv_device_type);
        textViewDeviceName = itemView.findViewById(R.id.tv_device_name);
    }
}
