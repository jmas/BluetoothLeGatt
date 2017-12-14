package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import gcardone.junidecode.Junidecode;

public class DeviceHelper {
    private final static String TAG = DeviceService.class.getSimpleName();

    public int getAlertTextMaxLength() {
        return 0;
    }

    public boolean isIt(BluetoothDevice device) {
        return false;
    }

    public String formatAlertText(String text) {
        String messageBodyTransliterated = Junidecode.unidecode(text);
        Log.i(TAG, "formatAlertText(): " + text);
        return text;
    }
}
