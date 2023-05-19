package br.com.alimentadao.app.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ArduinoBluetoothConnection {
    private static final String TAG = "ArduinoBluetoothConn";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for SPP (Serial Port Profile)

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice arduinoDevice;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothConnectionListener connectionListener;

    public ArduinoBluetoothConnection(BluetoothAdapter bluetoothAdapter, BluetoothDevice arduinoDevice) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.arduinoDevice = arduinoDevice;
    }

    public void setConnectionListener(BluetoothConnectionListener listener) {
        this.connectionListener = listener;
    }

    public boolean isConnected() {
        return connectedThread != null;
    }

    public void connect() {
        connectThread = new ConnectThread();
        connectThread.start();
    }

    public void disconnect() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    public void sendData(String data) {
        ConnectedThread thread;
        synchronized (this) {
            if (connectedThread == null) {
                Log.e(TAG, "Cannot send data. Connection is not established.");
                return;
            }
            thread = connectedThread;
        }
        thread.write(data.getBytes());
    }

    private class ConnectThread extends Thread {
        @SuppressLint("MissingPermission")
        public void run() {
            try {
                socket = arduinoDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                connectedThread = new ConnectedThread(socket);
                connectedThread.start();

                if (connectionListener != null) {
                    connectionListener.onConnected(arduinoDevice.getName());
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to connect to Arduino device: " + e.getMessage());
                if (connectionListener != null) {
                    connectionListener.onConnectionFailed(arduinoDevice.getName(), e.getMessage());
                }
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close socket: " + e.getMessage());
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create input/output stream: " + e.getMessage());
            }

            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            byte[] buffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = mmInputStream.read(buffer);
                    String receivedData = new String(buffer, 0, numBytes);

                    if (connectionListener != null) {
                        connectionListener.onDataReceived(arduinoDevice.getName(), receivedData);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to read data from input stream: " + e.getMessage());
                    if (connectionListener != null) {
                        connectionListener.onConnectionLost(arduinoDevice.getName());
                    }
                    break;
                }
            }
        }

        public void write(byte[] data) {
            try {
                mmOutputStream.write(data);
            } catch (IOException e) {
                Log.e(TAG, "Failed to write data to output stream: " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmInputStream.close();
                mmOutputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close input/output stream: " + e.getMessage());
            }
        }
    }

    public interface BluetoothConnectionListener {
        void onConnected(String deviceName);
        void onConnectionFailed(String deviceName, String message);
        void onDataReceived(String deviceName, String data);
        void onConnectionLost(String deviceName);
    }
}
