package com.example.android.bluetoothlegatt.device_helpers;

import android.bluetooth.BluetoothDevice;

import org.apache.commons.lang3.StringUtils;

import com.example.android.bluetoothlegatt.DeviceHelper;

public class MiBand2 extends DeviceHelper {
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
        return StringUtils.substring(super.formatAlertText(text), 0, getAlertTextMaxLength());
    }
}
