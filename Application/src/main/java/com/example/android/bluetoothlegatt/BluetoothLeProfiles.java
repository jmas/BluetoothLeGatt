package com.example.android.bluetoothlegatt;

import java.util.UUID;

/**
 * Created by jmas on 14.12.17.
 */

public class BluetoothLeProfiles {
    public static class AlertNotification {
        public static UUID service = UUID.fromString("00001811-0000-1000-8000-00805f9b34fb");
        public static UUID newAlert = UUID.fromString("00002A46-0000-1000-8000-00805f9b34fb");
    }
}
