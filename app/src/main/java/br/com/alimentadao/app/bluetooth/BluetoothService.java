package br.com.alimentadao.app.bluetooth;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothService {

    public static final int REQUEST_ENABLE_BT = 1;

    private static final UUID ALIMENTADAO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private final AppCompatActivity context;
    private final BluetoothAdapter bluetoothAdapter;

    private InputStream response;
    private OutputStream request;

    public BluetoothService(AppCompatActivity context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
        }
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> findPairedDevices() {
        List<BluetoothDevice> devices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
        Log.i("BluetoothService", "findPairedDevices: " + devices);
        return devices;
    }

    @SuppressLint("MissingPermission")
    public BluetoothDevice findByName(String name) {
        BluetoothDevice bluetoothDevice = findPairedDevices()
                .stream()
                .filter(device -> device.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        Log.i("BluetoothService", "findByName: " + bluetoothDevice);
        return bluetoothDevice;
    }

    @SuppressLint("MissingPermission")
    public void connectToDevice(BluetoothDevice device) {
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(ALIMENTADAO_UUID);
            socket.connect();
            Log.i("bluetooth connection", "Conexão estabelecida com sucesso");

            response = socket.getInputStream();
            request = socket.getOutputStream();

            // context.startActivity(new Intent(context, HomeActivity.class));
        } catch (IOException e) {
            Toast.makeText(
                    context,
                    "Não foi possível estabeler uma conexão com o dispositivo '" + device.getName() + "'.",
                    LENGTH_SHORT
            ).show();
            Log.e("bluetooth connection", e.getMessage());
        }
    }

    public void requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
                context,
                new String[]{Manifest.permission.BLUETOOTH},
                REQUEST_ENABLE_BT
        );
        Log.i("BluetoothService", "requestBluetoothPermission: ");
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
        Log.i("BluetoothService", "requestToEnableBluetooth: ");
    }

    public void disableBluetooth() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
            return;
        }

        if (!isEnabled()) return;

        bluetoothAdapter.disable();
        Log.i("BluetoothService", "disableBluetooth: ");
    }

    public boolean isEnabled() {
        boolean enabled = bluetoothAdapter.isEnabled();
        Log.i("BluetoothService", "isEnabled: " + enabled);
        return enabled;
    }

    public void sendTime(String time) {
        try {
            request.write(time.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e("Send content", "sendTimes: ", e);
            Toast.makeText(
                    context,
                    "Alimentador indisponível",
                    LENGTH_SHORT
            ).show();
        }
    }

    public String fetchTime() {
        try {
            return String.valueOf(response.read());
        } catch (IOException e) {
            Log.e("Fetch content", "sendTimes: ", e);
            Toast.makeText(
                    context,
                    "Alimentador indisponível",
                    LENGTH_SHORT
            ).show();
        }

        return null;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
