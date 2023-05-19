package br.com.alimentadao.app;

import static android.Manifest.permission.BLUETOOTH;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.alimentadao.app.bluetooth.ArduinoBluetoothConnection;
import br.com.alimentadao.app.device.DeviceAdapter;
import br.com.alimentadao.app.device.DeviceItem;

public class ConnectionActivity extends AppCompatActivity implements ArduinoBluetoothConnection.BluetoothConnectionListener {

    public static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "Bluetooth Service";

    private static ConnectionActivity instance;

    private final Handler handler = new Handler();

    private DeviceAdapter deviceAdapter;

    private BluetoothAdapter bluetoothAdapter;

    private ArduinoBluetoothConnection bluetoothConnection;
    private Runnable task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_activity);

        RecyclerView recyclerViewDevices = findViewById(R.id.devices);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission();
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    "Não suporte para conexão bluetooth",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        deviceAdapter = new DeviceAdapter();
        recyclerViewDevices.setAdapter(deviceAdapter);

        int delay = 3_000;
        handler.postDelayed(task = () -> {
            try {
                if (ContextCompat.checkSelfPermission(this, BLUETOOTH) != PERMISSION_GRANTED
                        || !bluetoothAdapter.isEnabled()) {
                    return;
                }

                findPairedDevices()
                        .stream()
                        .map(DeviceItem::of)
                        .forEach(deviceAdapter::addDevice);

                showLoadingProgressBar(deviceAdapter.getItemCount() == 0);
            } catch (Exception e) {
                Log.e("AndroidTask", "onCreate: ", e);
            }

            handler.postDelayed(task, delay);
        }, delay);

        handleBluetoothSwitch();
        handleConnectDeviceButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            Switch switchBluetooth = findViewById(R.id.switch_bluetooth);
            switchBluetooth.setChecked(bluetoothAdapter.isEnabled());

            if (resultCode == RESULT_OK) return;

            requestBluetoothPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(task);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(task);
    }

    @Override
    public void onBackPressed() {
        if (bluetoothConnection != null && bluetoothConnection.isConnected()) {
            bluetoothConnection.disconnect();
        }
    }

    @Override
    public void onConnected(String deviceName) {
        String message = "Conexão estabelecida com '" + deviceName + "'";
        Log.d(TAG, message);
    }

    @Override
    public void onConnectionFailed(String deviceName, String message) {
        Log.d(TAG, "Não foi possível estebelecer uma conexão com '" + deviceName + "': " + message);
    }

    @Override
    public void onDataReceived(String deviceName, String data) {
        Log.d(TAG, "Recebendo dados do dispositivo '" + deviceName + "': " + data);
    }

    @Override
    public void onConnectionLost(String deviceName) {
        Log.d(TAG, "Conexão perdida com '" + deviceName + "'");
    }

    public void requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.BLUETOOTH},
                REQUEST_ENABLE_BT
        );
        Log.i("Bluetooth", "request bluetooth permission.");
    }

    @SuppressLint("MissingPermission")
    public void requestToEnableBluetooth() {
        startActivityForResult(
                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
        );
        Log.i("Bluetooth", "request to enable bluetooth.");
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> findPairedDevices() {
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
    private void handleBluetoothSwitch() {
        Switch switchBluetooth = findViewById(R.id.switch_bluetooth);

        switchBluetooth.setChecked(bluetoothAdapter.isEnabled());
        switchBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestToEnableBluetooth();
            } else {
                bluetoothAdapter.disable();
            }

            showLoadingProgressBar(!isChecked);
        });
    }

    @SuppressLint("MissingPermission")
    private void handleConnectDeviceButton() {
        Button button = findViewById(R.id.btn_connect_device);

        button.setOnClickListener(view -> {
            DeviceItem selectedDevice = deviceAdapter.getSelectedDevice();

            if (selectedDevice == null) {
                Toast.makeText(
                        this,
                        "Nenhum dispositivo selecionado",
                        LENGTH_SHORT
                ).show();
                return;
            }

            BluetoothDevice bluetoothDevice = findByName(selectedDevice.getName());

            if (bluetoothDevice == null) {
                Toast.makeText(
                        this,
                        String.format(
                                "O dispositivo '%s' não está mais disponível",
                                selectedDevice.getName()
                        ),
                        LENGTH_SHORT
                ).show();
                return;
            }

            bluetoothConnection = new ArduinoBluetoothConnection(bluetoothAdapter, bluetoothDevice);
            bluetoothConnection.setConnectionListener(this);
            bluetoothConnection.connect();

            if (bluetoothConnection.isConnected()) {
                this.startActivity(new Intent(this, HomeActivity.class));
                return;
            }

            Toast.makeText(
                    this,
                    "Não foi possível estabeler uma conexão com o dispositivo '" + bluetoothDevice.getName() + "'.",
                    LENGTH_SHORT
            ).show();
        });
    }

    public void showLoadingProgressBar(boolean show) {
        ProgressBar progressBar = findViewById(R.id.pb_loading);
        ScrollView scrollView = findViewById(R.id.devicesContainer);

        scrollView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public static ConnectionActivity getInstance() {
        return instance;
    }

    public ArduinoBluetoothConnection getBluetoothConnection() {
        return bluetoothConnection;
    }
}
