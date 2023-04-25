package br.com.alimentadao.app.device;

import java.util.Arrays;

import br.com.alimentadao.app.R;

public enum DeviceType {
    DEVICE_TYPE_UNKNOWN(0, R.drawable.gear),
    DEVICE_TYPE_CLASSIC(1, R.drawable.speaker_filled_audio_tool),
    DEVICE_TYPE_LE(2, R.drawable.watch),
    DEVICE_TYPE_DUAL(3, R.drawable.tablet_telephone);

    private final int code;
    private final int icon;

    DeviceType(int code, int icon) {
        this.code = code;
        this.icon = icon;
    }

    public int getCode() {
        return code;
    }

    public int getIcon() {
        return icon;
    }

    public static DeviceType getIconBy(int code) {
        return Arrays.stream(DeviceType.values())
                .filter(deviceType -> deviceType.code == code)
                .findFirst()
                .orElse(DEVICE_TYPE_UNKNOWN);
    }
}
