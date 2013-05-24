package com.example.myfirstappgit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by arcticmatt on 5/23/13.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++)
            {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
            }

            //---display the new SMS message---
            //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

            ArrayList<String> storedMessages = returnStoredMessages();
            storedMessages.add(str);
            String[] messageArray = listToArray(storedMessages);
            showReceivedMessage(messageArray);

        }
    }
    public void showReceivedMessage(String[] ownMessageArray)
    {
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ownMessageArray);
        ListView listView = (ListView) findViewById(R.id.listConvo);
        listView.setAdapter(adapter);
    }
}
