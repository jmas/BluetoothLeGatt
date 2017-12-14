package com.example.android.bluetoothlegatt.helpers;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.example.android.bluetoothlegatt.DeviceService;

import gcardone.junidecode.Junidecode;

public class DeviceHelper {
    private final static String TAG = DeviceService.class.getSimpleName();

    public int getAlertTextMaxLength() {
        return -1;
    }

    public boolean isIt(BluetoothDevice device) {
        return false;
    }

    public String formatAlertText(String text) {
        return Junidecode.unidecode(text);
    }
}
