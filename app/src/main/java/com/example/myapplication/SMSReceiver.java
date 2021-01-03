package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class SMSReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages = null;
        String code = "";

        if (myBundle != null)
        {
            Object [] pdus = (Object[]) myBundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++)
            {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (messages[i].getOriginatingAddress().equals("Israel Rail")) {
                    code = extractDigits(messages[i].getMessageBody());
                }
            }

            if (!code.equals("")) {
                try {
                    afterAuth(code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String extractDigits(final String in) {
        final Pattern p = Pattern.compile("(^[0-9]{5,6}$)");
        final Matcher m = p.matcher(in);
        if ( m.find() ) {
            return m.group( 0 );
        }
        return "";
    }

    public abstract void afterAuth(String code) throws ExecutionException, InterruptedException, JSONException;
}