package com.example.android.bluetoothlegatt.helpers;

import android.bluetooth.BluetoothDevice;

import org.apache.commons.lang3.StringUtils;

public class MiBand2Helper extends DeviceHelper {
    private static final int ALERT_TEXT_MAX_LENGTH = 18;
    private static final String DEVICE_NAME = "MiBand 2";

    @Override
    public int getAlertTextMaxLength() {
        return ALERT_TEXT_MAX_LENGTH;
    }

    @Override
    public boolean isIt(BluetoothDevice device) {
        return device.getName().equals(DEVICE_NAME);
    }

    @Override
    public String formatAlertText(String text) {
        int maxLength = getAlertTextMaxLength();
        String textFormatted = super.formatAlertText(text);
        return maxLength == -1 ? textFormatted: StringUtils.substring(textFormatted, 0, maxLength);
    }
}
