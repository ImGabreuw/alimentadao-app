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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BluetoothGateway {

    public static final int REQUEST_ENABLE_BT = 1;

    private static final UUID ALIMENTADAO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private final AppCompatActivity context;
    private final BluetoothAdapter bluetoothAdapter;

    private InputStream response;
    private OutputStream request;

    public BluetoothGateway(AppCompatActivity context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(
                    context,
                    "O aparelho não tem suporte ao bluetooth",
                    LENGTH_SHORT
            ).show();
            return;
        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
        }

        if (!bluetoothAdapter.isEnabled()) {
            requestToEnableBluetooth();
        }

        pairWithAlimentadaoDevice();
    }

    public InputStream getResponse() {
        return response;
    }

    public OutputStream getRequest() {
        return request;
    }

    @SuppressLint("MissingPermission")
    private void pairWithAlimentadaoDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() == 0) {
            Toast.makeText(
                    context,
                    "Não há nenhum dispositivo para realizar o pareamento",
                    LENGTH_SHORT
            ).show();
            return;
        }
        List<String> deviceNames = pairedDevices
                .stream()
                .map(BluetoothDevice::getName)
                .collect(Collectors.toList());

        String[] devices = deviceNames.toArray(new String[pairedDevices.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Selecione um dispositivo Bluetooth");
        builder.setItems(
                devices,
                (dialog, which) -> pairedDevices
                        .stream()
                        .filter(device -> device.getName().equals(devices[which]))
                        .findFirst()
                        .ifPresent(this::connectToDevice)
        );
        builder.show();
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(ALIMENTADAO_UUID);
            socket.connect();

            response = socket.getInputStream();
            request = socket.getOutputStream();

            Log.i("bluetooth connection", "Conexão estabelecida com sucesso");
        } catch (IOException e) {
            Log.e("bluetooth connection", e.getMessage());
            Toast.makeText(
                    context,
                    "Não foi possível estabeler uma conexão com o dispositivo '" + device.getName() + "'.",
                    LENGTH_SHORT
            ).show();
        }
    }

    private void requestBluetoothPermission() {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
    }

    @SuppressLint("MissingPermission")
    private void requestToEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
}
