package br.com.alimentadao.app.bluetooth;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.alimentadao.app.HomeActivity;
import br.com.alimentadao.app.time.TimeItem;

@SuppressLint("MissingPermission")
public class BluetoothService {

    public static final int REQUEST_ENABLE_BT = 1;

    private static final String DEFAULT_SPP__UUID = "00001101-0000-1000-8000-00805F9B34FB";


    private final AppCompatActivity context;
    private final BluetoothAdapter bluetoothAdapter;

    private OutputStream request;

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
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(DEFAULT_SPP__UUID));

            if (!socket.isConnected()) return false;

            socket.connect();
            request = socket.getOutputStream();

            Log.i("Bluetooth connection", "connection established.");

            context.startActivity(new Intent(context, HomeActivity.class));
            return true;
        } catch (IOException e) {
            Log.e("bluetooth connection", "error when connection device '" + device.getName() + "'.", e);
        }

        return false;
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
        try {
            if (request == null) return;

            request.write(time.getFormattedTime().getBytes(UTF_8));
        } catch (IOException e) {
            Log.e("Socket", "error when sending time (" + time.getFormattedTime() + ").", e);
        }
    }

    public void sendFedNow() {
        try {
            request.write("a".getBytes(UTF_8));
        } catch (IOException e) {
            Log.e("Socket", "error when sending action to fed now.", e);
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
