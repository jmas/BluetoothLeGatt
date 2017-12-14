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
    private static final String SPENDING_FRAGMENT_NAME = "Splata";
    private static final String ARRIVAL_FRAGMENT_NAME = "Popovnennya";
    private static final String C2C_FRAGMENT_NAME = "C2C perekaz";

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
        if (FROM_ADDRESS.equals(address)) {
            Pattern pattern = Pattern.compile(
                        "("+SPENDING_FRAGMENT_NAME
                        +"|"+ARRIVAL_FRAGMENT_NAME
                        +"|"+C2C_FRAGMENT_NAME
                        +").+Suma: ([\\d]+).+Dostupnyi zalyshok: ([\\d]+)",
                    Pattern.DOTALL);
            Matcher matcher = pattern.matcher(messageBody);
            if (matcher.find()) {
                MatchResult result = matcher.toMatchResult();
                boolean arrived = ARRIVAL_FRAGMENT_NAME.equals(result.group(1)) || C2C_FRAGMENT_NAME.equals(result.group(1));
                String amount = result.group(2);
                String balance = result.group(3);
                String alertText = (arrived ? "+": "-") + amount + " @ " + balance;
                sendAlert(context, alertText);
            }
        } else {
            sendAlert(context, messageBody);
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
