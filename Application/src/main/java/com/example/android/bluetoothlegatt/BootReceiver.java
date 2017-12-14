package com.example.android.bluetoothlegatt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.bluetoothlegatt.helpers.AppSettings;

public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String deviceAddress = (new AppSettings(context)).getString(AppSettings.KEY_CONNECTED_DEVICE_ADDRESS);

            if (deviceAddress != null) {
                Intent serviceIntent = new Intent(context, DeviceService.class);
                serviceIntent.putExtra(DeviceService.EXTRAS_DEVICE_ADDRESS, deviceAddress);
                context.startService(serviceIntent);
            }
        }
    }
}
