package br.com.alimentadao.app;

import static android.content.Intent.ACTION_PICK;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.alimentadao.app.bluetooth.BluetoothService;
import br.com.alimentadao.app.device.DeviceAdapter;
import br.com.alimentadao.app.device.DeviceItem;

public class WelcomeActivity extends AppCompatActivity {

    public static final int REQUEST_PICK_IMAGE = 1;

    private static WelcomeActivity instance;

    private DeviceAdapter deviceAdapter;
    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        RecyclerView recyclerViewDevices = findViewById(R.id.devices);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));

        bluetoothService = new BluetoothService(this);
        deviceAdapter = new DeviceAdapter();

        recyclerViewDevices.setAdapter(deviceAdapter);
        bluetoothService
                .findPairedDevices()
                .stream()
                .map(DeviceItem::of)
                .forEach(deviceAdapter::addDevice);
        deviceAdapter.notifyDataSetChanged();

        handleChangeProfileButton();
        handleBluetoothSwitch();
    }

    private void handleChangeProfileButton() {
        ImageButton buttonChangeImgProfile = findViewById(R.id.btnWelcomeChangeProfile);
        buttonChangeImgProfile.setOnClickListener(view -> openGallery());
    }

    private void handleBluetoothSwitch() {
        Switch switchBluetooth = findViewById(R.id.switch_bluetooth);

        switchBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bluetoothService.requestToEnableBluetooth();
                return;
            }

            bluetoothService.disableBluetooth();
        });
    }


    private void openGallery() {
        startActivityForResult(
                new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_PICK_IMAGE
        );
    }

    public static WelcomeActivity getInstance() {
        return instance;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }
}
