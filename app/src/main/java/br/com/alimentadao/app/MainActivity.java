package br.com.alimentadao.app;

import static android.Manifest.permission.BLUETOOTH;
import static android.content.Intent.ACTION_PICK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_SHORT;
import static br.com.alimentadao.app.bluetooth.BluetoothService.REQUEST_ENABLE_BT;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import br.com.alimentadao.app.bluetooth.BluetoothService;
import br.com.alimentadao.app.device.DeviceAdapter;
import br.com.alimentadao.app.device.DeviceItem;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PICK_IMAGE = 1;

    private static MainActivity instance;

    private final Handler handler = new Handler();

    private Runnable task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerViewDevices = findViewById(R.id.devices);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));

        bluetoothService = new BluetoothService(this);
        deviceAdapter = new DeviceAdapter();
        recyclerViewDevices.setAdapter(deviceAdapter);

        handleChangeProfileButton();
        handleBluetoothSwitch();
        handleConnectDeviceButton();
    }
    private DeviceAdapter deviceAdapter;

    private BluetoothService bluetoothService;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            ImageView meuImageButton = findViewById(R.id.WelcomedogImage);
            meuImageButton.setImageURI(imageUri);

            Toast.makeText(
                    getApplicationContext(),
                    "Foto atualizada com sucesso",
                    LENGTH_SHORT
            ).show();
            return;
        }

        if (requestCode == REQUEST_ENABLE_BT) {
            Switch switchBluetooth = findViewById(R.id.switch_bluetooth);
            switchBluetooth.setChecked(bluetoothService.getBluetoothAdapter().isEnabled());

            if (resultCode == RESULT_OK) return;

            bluetoothService.requestBluetoothPermission();
        }
    }

    @Override
    protected void onResume() {
        int delay = 3_000;

        handler.postDelayed(task = () -> {
            handler.postDelayed(task, delay);

            try {
                if (ContextCompat.checkSelfPermission(this, BLUETOOTH) != PERMISSION_GRANTED
                        || !bluetoothService.getBluetoothAdapter().isEnabled()) {
                    return;
                }

                bluetoothService
                        .findPairedDevices()
                        .stream()
                        .map(DeviceItem::of)
                        .forEach(deviceAdapter::addDevice);
                deviceAdapter.notifyDataSetChanged();
                showLoadingProgressBar(deviceAdapter.getItemCount() == 0);
            } catch (Exception e) {
                Log.e("AndroidTask", "onCreate: ", e);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(task);
        super.onPause();
    }

    private void handleChangeProfileButton() {
        ImageButton buttonChangeImgProfile = findViewById(R.id.btnWelcomeChangeProfile);
        buttonChangeImgProfile.setOnClickListener(view -> openGallery());
    }

    private void handleBluetoothSwitch() {
        Switch switchBluetooth = findViewById(R.id.switch_bluetooth);

        switchBluetooth.setChecked(bluetoothService.getBluetoothAdapter().isEnabled());
        switchBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bluetoothService.requestToEnableBluetooth();
                return;
            }

            bluetoothService.disableBluetooth();
        });
    }

    private void handleConnectDeviceButton() {
        MaterialButton button = findViewById(R.id.btn_connect_device);

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

            BluetoothDevice bluetoothDevice = bluetoothService.findByName(selectedDevice.getName());

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

            bluetoothService.connectToDevice(bluetoothDevice);
        });
    }

    public void showLoadingProgressBar(boolean show) {
        ProgressBar progressBar = findViewById(R.id.pb_loading);
        ScrollView scrollView = findViewById(R.id.devicesContainer);

        if (show) {
            scrollView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            return;
        }

        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void openGallery() {
        startActivityForResult(
                new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_PICK_IMAGE
        );
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }
}
