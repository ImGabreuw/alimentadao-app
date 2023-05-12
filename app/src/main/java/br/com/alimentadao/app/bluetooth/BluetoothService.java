package br.com.alimentadao.app.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentadao.app.time.TimeItem;

@SuppressLint("MissingPermission")
public class BluetoothService {

    public static final int REQUEST_ENABLE_BT = 1;

    private final AppCompatActivity context;
    private final BluetoothAdapter bluetoothAdapter;


    public BluetoothService(AppCompatActivity context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
        }
    }

    public List<BluetoothDevice> findPairedDevices() {
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    public BluetoothDevice findByName(String name) {
        return findPairedDevices()
                .stream()
                .filter(device -> device.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean connectToDevice(BluetoothDevice device) {
        BluetoothThread.CreateConnectThread bluetoothThread = new BluetoothThread.CreateConnectThread(
                bluetoothAdapter,
                device.getAddress()
        );
        bluetoothThread.start();

        if (BluetoothThread.mmSocket == null) return false;

        return BluetoothThread.mmSocket.isConnected();
    }

    public void requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
                context,
                new String[]{Manifest.permission.BLUETOOTH},
                REQUEST_ENABLE_BT
        );
        Log.i("bluetooth connection", "request bluetooth permission.");
    }

    public void requestToEnableBluetooth() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
            return;
        }

        if (bluetoothAdapter.isEnabled()) return;

        context.startActivityForResult(
                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
        );
        Log.i("bluetooth connection", "request to enable bluetooth.");
    }

    public void disableBluetooth() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
            return;
        }

        if (!isEnabled()) return;

        bluetoothAdapter.disable();
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void sendTime(TimeItem time) {
        if (BluetoothThread.mmSocket == null || BluetoothThread.mmSocket.isConnected()) return;

        BluetoothThread.connectedThread.write(time.getFormattedTime());
    }

    public void sendFedNow() {
        if (BluetoothThread.mmSocket == null || BluetoothThread.mmSocket.isConnected()) return;

        BluetoothThread.connectedThread.write("a");
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
