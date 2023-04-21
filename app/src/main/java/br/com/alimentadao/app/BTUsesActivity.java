package br.com.alimentadao.app;

import static br.com.alimentadao.app.bluetooth.BluetoothGateway.REQUEST_ENABLE_BT;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BTUsesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deny_bluetooth_permission);

        handleBtnAllowBluetooth();
        handleBtnCloseApp();
    }

    private void handleBtnAllowBluetooth() {
        Button btnAllow = findViewById(R.id.btn_allow_bt_permission);

        btnAllow.setOnClickListener(view -> ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.BLUETOOTH},
                REQUEST_ENABLE_BT
        ));
    }

    private void handleBtnCloseApp() {
        Button btnClose = findViewById(R.id.btn_close_app);

        btnClose.setOnClickListener(view -> finishAffinity());
    }
}
