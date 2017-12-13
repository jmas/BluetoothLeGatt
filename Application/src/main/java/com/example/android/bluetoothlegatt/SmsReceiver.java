package com.example.android.bluetoothlegatt;

import android.telephony.SmsMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private static final String FROM_ADDRESS = "OTP Bank";

    public void onReceive(Context context, Intent intent) {
        String address = FROM_ADDRESS;
        String message = null;
        Log.i("IncomingSmsBroadcast", "onReceive");
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                address = smsMessage.getDisplayOriginatingAddress();
                message = smsMessage.getMessageBody();
                Log.i("IncomingSmsBroadcast", "onReceive address: " + address);
                Log.i("IncomingSmsBroadcast", "onReceive message: " + message);
            }
        }
        if (address.equals(FROM_ADDRESS)) {
            Pattern pattern = Pattern.compile("Suma: ([\\d]+).+Dostupnyi zalyshok: ([\\d]+)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                MatchResult result = matcher.toMatchResult();
                String spend = result.group(1);
                String left = result.group(2);
                Log.i("IncomingSmsBroadcast", "onReceive spend: " + spend + " left: " + left);
//                Intent serviceIntent = new Intent(context, MainForegroundService.class);
//                serviceIntent.putExtra("address", address);
//                serviceIntent.putExtra("message", "-" + spend + " @ " + left);
//                context.startService(serviceIntent);
            }
        }
    }
}
