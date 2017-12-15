package com.example.android.bluetoothlegatt;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    public static final String SETTINGS_NAME = "app_settings";
    public static final String KEY_CONNECTED_DEVICE_ADDRESS = "connected_device_address";

    private SharedPreferences sharedPref;

    Settings(Context context) {
        sharedPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public boolean setString(String key, String value) {
        if (sharedPref == null) {
            return false;
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defValue) {
        if (sharedPref == null) {
            return null;
        }
        return sharedPref.getString(key, defValue);
    }

}
