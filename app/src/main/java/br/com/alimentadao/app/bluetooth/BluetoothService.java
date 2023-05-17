package br.com.alimentadao.app.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.alimentadao.app.time.TimeItem;

@SuppressLint("MissingPermission")
public class BluetoothService {

    public static final int REQUEST_ENABLE_BT = 1;

    private static final String TAG = "Bluetooth Service";

    private final AppCompatActivity context;
    private final BluetoothAdapter bluetoothAdapter;

    private BluetoothConnectionThread connectionThread;


    public BluetoothService(AppCompatActivity context) {
        this.context = context;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(
                    context,
                    "Não suporte para conexão bluetooth",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
//            requestToEnableBluetooth();
            bluetoothAdapter.enable();
        }
    }

    public void requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
                context,
                new String[]{Manifest.permission.BLUETOOTH},
                REQUEST_ENABLE_BT
        );
        Log.i(TAG, "request bluetooth permission.");
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

        if (!bluetoothAdapter.isEnabled()) return;

        bluetoothAdapter.disable();
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public List<BluetoothDevice> findPairedDevices() {
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    public BluetoothDevice findByName(String name) {
        BluetoothDevice bluetoothDevice = findPairedDevices()
                .stream()
                .filter(device -> device.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (bluetoothDevice == null) {
            Log.i(
                    "Connection Bluetooth",
                    String.format(
                            "Not found device with the name '%s'",
                            name
                    )
            );
        } else {
            Log.i(
                    "Connection Bluetooth",
                    String.format(
                            "Find device with the name '%s' and adress '%s' (%s)",
                            bluetoothDevice.getName(),
                            bluetoothDevice.getAddress(),
                            Arrays.toString(bluetoothDevice.getUuids())
                    )
            );
        }

        return bluetoothDevice;
    }

    public boolean connect(BluetoothDevice device) {
        connectionThread = new BluetoothConnectionThread(device);

        connectionThread.start();

        return connectionThread.isConnected();
    }

    public boolean isConnected() {
        return connectionThread.isConnected();
    }

    public void stopConnection() {
        connectionThread.cancel();
    }

    public void sendTime(TimeItem time) {
        if (!isConnected()) return;

        try {
            connectionThread
                    .getSocket()
                    .getOutputStream()
                    .write(time.getFormattedTime().getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error while sending time to arduino.", e);
        }
    }

    public void sendFedNow() {
        if (!isConnected()) return;

        try {
            connectionThread
                    .getSocket()
                    .getOutputStream()
                    .write("a".getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error while sending time to arduino.", e);
        }
    }



}
