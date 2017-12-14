package com.example.android.bluetoothlegatt;

import android.telephony.SmsMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.Log;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private final static String TAG = SmsReceiver.class.getSimpleName();

    private static final String FROM_ADDRESS = "OTP Bank";

    public void onReceive(Context context, Intent intent) {
        String address = null;
        String messageBody = null;
        Log.i(TAG, "onReceive()");
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                address = smsMessage.getDisplayOriginatingAddress();
                messageBody = smsMessage.getMessageBody();
                Log.i(TAG, "onReceive() address: " + address);
                Log.i(TAG, "onReceive() messageBody: " + messageBody);
            }
        }
        if (address.equals(FROM_ADDRESS)) {
            Pattern pattern = Pattern.compile("Suma: ([\\d]+).+Dostupnyi zalyshok: ([\\d]+)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(messageBody);
            if (matcher.find()) {
                MatchResult result = matcher.toMatchResult();
                String spend = result.group(1);
                String left = result.group(2);
                String alertText = "-" + spend + " @ " + left;
                sendAlert(context, alertText);
            }
        }
    }

    private void sendAlert(Context context, String text) {
        String deviceAddress = (new AppSettings(context)).getString(AppSettings.KEY_CONNECTED_DEVICE_ADDRESS);

        Log.i(TAG, "sendAlert() deviceAddress: " + deviceAddress);
        Log.i(TAG, "sendAlert() alert text: " + text);

        if (deviceAddress != null) {
            Intent serviceIntent = new Intent(context, MiBandService.class);
            serviceIntent.setAction(MiBandService.ACTION_NEW_ALERT);
            serviceIntent.putExtra(MiBandService.EXTRAS_DEVICE_ADDRESS, deviceAddress);
            serviceIntent.putExtra(MiBandService.EXTRAS_ALERT_TEXT, text);
            context.startService(serviceIntent);
        }
    }
}
