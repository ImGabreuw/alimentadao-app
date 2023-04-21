package br.com.alimentadao.app.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BluetoothFacade {
    private final BluetoothGateway gateway;

    public BluetoothFacade(BluetoothGateway gateway) {
        this.gateway = gateway;
    }

    public void sendTime(String time) {
        try {
            gateway.getRequest().write(time.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e("Send content", "sendTimes: ", e);
        }
    }

    public String fetchTime() {
        try {
            return String.valueOf(gateway.getResponse().read());
        } catch (IOException e) {
            Log.e("Fetch content", "sendTimes: ", e);
        }

        return null;
    }
}
