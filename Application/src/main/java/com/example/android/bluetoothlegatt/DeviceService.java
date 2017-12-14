package com.example.android.bluetoothlegatt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.android.bluetoothlegatt.device_helpers.MiBand2;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

public class DeviceService extends Service {
    private final static String TAG = DeviceService.class.getSimpleName();
    
    public static final int NOTIFICATION_ID = 1;
    public final static String ACTION_NEW_ALERT =
            "com.example.bluetoothlegatt.deviceservice.ACTION_NEW_ALERT";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_ALERT_TEXT = "ALERT_TEXT";

    private boolean mConnected = false;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;
    private String pendingAlert;
    private NotificationManager notificationManager;

    public static final byte ALERT_TYPE_EMAIL = 0x01;
    public static final byte ALERT_TYPE_PHONE = 0x03;
    public static final byte ALERT_TYPE_MESSAGE = 0x05;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mBluetoothLeService.connect(mDeviceAddress);
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    private void showForegroundNotification(String contentText) {
        Log.i(TAG, "showForegroundNotification()");
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), DeviceScanActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        // process alert
        if (ACTION_NEW_ALERT.equals(action)) {
            String alertText = intent.getStringExtra(EXTRAS_ALERT_TEXT);
            Log.i(TAG, "onStartCommand() ACTION_NEW_ALERT (" + alertText +")");
            if  (mConnected) {
                sendNewAlert(ALERT_TYPE_MESSAGE, alertText);
            } else {
                pendingAlert = alertText;
            }
        }
        // bind service
        String deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        if (deviceAddress != null) {
            mDeviceAddress = deviceAddress;
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(ACTION_NEW_ALERT);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                showForegroundNotification("Connected");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                notificationManager.cancel(NOTIFICATION_ID);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mConnected = true;
                showForegroundNotification("Discovered");
                if (pendingAlert != null) {
                    sendNewAlert(ALERT_TYPE_MESSAGE, pendingAlert);
                    pendingAlert = null;
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.i(TAG, "New bond state: " + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0));
            }
        }
    };

    private void sendNewAlert(byte type, String text) {
        Log.i(TAG, "sendNewAlert(): " + text);
        BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(
                BluetoothLeProfiles.AlertNotification.service,
                BluetoothLeProfiles.AlertNotification.newAlert);
        // Message format here: https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.new_alert.xml
        // Message (characteristic value) format:
        // first byte
        //   0x01 - as email
        //   0x03 - as phone
        //   0x05 - as message
        // second byte
        //   count of messages (random number - not showing on display for mi band 2)
        // rest bytes
        //   content of message
        DeviceHelper deviceHelper = getDeviceHelper(mBluetoothLeService.getDevice());
        characteristic.setValue(ArrayUtils.addAll(new byte[]{type, 0x01}, deviceHelper.formatAlertText(text).getBytes()));
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private DeviceHelper getDeviceHelper(BluetoothDevice device) {
        ArrayList<DeviceHelper> types = new ArrayList<DeviceHelper>() {{
            add(new MiBand2());
        }};
        for (DeviceHelper type : types) {
            if (type.isIt(device)) {
                return type;
            }
        }
        return new DeviceHelper();
    }
}
