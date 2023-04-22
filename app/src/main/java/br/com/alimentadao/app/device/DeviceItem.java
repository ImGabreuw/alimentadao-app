package br.com.alimentadao.app.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import java.util.Objects;

public class DeviceItem {
    private final DeviceType type;
    private final String name;

    public DeviceItem(DeviceType type, String name) {
        this.type = type;
        this.name = name;
    }

    @SuppressLint("MissingPermission")
    public static DeviceItem of(BluetoothDevice device) {
        return new DeviceItem(
                DeviceType.getIconBy(device.getType()),
                device.getName()
        );
    }

    public DeviceType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceItem that = (DeviceItem) o;
        return getType() == that.getType() && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getName());
    }
}
