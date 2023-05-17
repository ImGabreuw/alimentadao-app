package br.com.alimentadao.app.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectionThread extends Thread {

    private static final String TAG = "Connection Bluetooth";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final BluetoothDevice device;
    private BluetoothSocket socket;

    public BluetoothConnectionThread(BluetoothDevice device) {
        this.device = device;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect bluetooth", e);
        }
    }


    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void cancel() {
        if (socket == null) return;

        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to stop connection thread", e);
        }
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}


