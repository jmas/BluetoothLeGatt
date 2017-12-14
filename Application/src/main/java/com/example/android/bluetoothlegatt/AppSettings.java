package com.example.android.bluetoothlegatt;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

    private static final String APP_PREFERENCES_NAME = "app_settings";
    public static final String KEY_CONNECTED_DEVICE_ADDRESS = "connected_device_address";

    private Context context;

    AppSettings(Context context) {
        this.context = context;
    }

    public boolean saveString(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getString(String key) {
        SharedPreferences deviceAddressPref = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return deviceAddressPref.getString(key, null);
    }

}
