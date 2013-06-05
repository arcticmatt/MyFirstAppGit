package com.example.myfirstappgit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by arcticmatt on 5/23/13.
 */
/*
There are two ways to make a broadcast receiver known to the system.
One is to declare it in the manifest file with the <receiver> element,
which is how this SmsReceiver was registered. The other is to create the
receiver dynamically in code and register it with the
Context.registerReceiver() method.

This receiver reacts on the Intent "android.provider.Telephony.SMS_RECEIVED."
This is shown in the <intent-filter> element in the <receiver> element.
The "android.provider.Telephony.SMS_RECEIVED" Intent is fired by the system
when there is an Incoming SMS. Permission must be granted to receive SMSs,
which was done in the manifest file.
 */
public class SmsReceiver extends BroadcastReceiver {
    /*
    This is the method that is called when the SMS Receiver is
    "invoked" by the System. This happens because we exposed this class
    in the AndroidManifest.xml.

    The SMS message is contained and attached to the Intent object (the
    second parameter in the onReceive() method) via a Bundle object.
    The messages are stored in an Object array in the PDU format (see
    the method "convertPDUToString" below
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        /*Since the SMS message is contained in a Bundle object,
        we must get that Bundle from the intent. The method
        getExtras() returns a Bundle.

        A Bundle is generally used for passing data between
        various Activities of android. It can hold different types
        of values and pass to the new activity.
        */
        Bundle bundle = intent.getExtras();

        //---if the Bundle object is not empty, then we will
        //---convert it to a string
        if (bundle != null)
        {
            String receivedMessage = convertBundleToString(bundle, context);
            //---display the new SMS message---
            Toast.makeText(context, receivedMessage, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, MainActivity.class);
            /*
            FLAG_ACTIVITY_SINGLE_TOP - If set, the activity will not be
            launched if it is already running at the top of the history stack.

            FLAG_ACTIVITY_NEW_TASK - a final int, that, if set, this activity
            will become the start of a new task on this history stack.
            If a task is already running for the activity you are now starting,
            then a new activity will not be started; instead, the current task will
             simply be brought to the front of the screen with the state it was last in.
             */
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("message", receivedMessage);
            context.startActivity(i);
        }
    }


    //---This method takes in a Bundle and converts it to a String
    protected String convertBundleToString (Bundle bundleFromIntent, Context context) {
        String str = "";
        /*
        The get(String key) method of Bundle class returns the entry
        with the given key as an object, in this case "pdus."
        A PDU is a "protocol description unit" which is the industry
        format for an SMS message. So basically, the get("pdus")
        gets all the PDU objects from the Bundle. In a large Bundle,
        there might be multiple PDU objects, which is why an Object
        Array is made.
         */
        Object[] pdus = (Object[]) bundleFromIntent.get("pdus");
        //---Makes a new Array that holds as many SmsMessage objects
        //---as there are PDUs in the Bundle.
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for (int i=0; i<msgs.length; i++)
        {


            /*
            Takes the first element of Array msgs and sets it to
            equal an Sms object. The Sms object is created by the
            static method createFromPdu, which creates an SmsMessage
            from a raw PDU.

            THIS METHOD WLL SOON BE DEPRECATED, IN FAVOR OF THE METHOD
            CREATEFROMPDU(BYTE[], STRING), WHICH TAKES AN EXTRA FORMAT
            PARAMETER
             */
            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            String name = findNameByAddress(context, msgs[i].getOriginatingAddress());
            str += "SMS from " + name;
            str += ": ";
            str += msgs[i].getMessageBody().toString();
        }
        return str;
    }

    public String findNameByAddress(Context ct,String addr)
    {
        Uri myPerson = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                Uri.encode(addr));

        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

        Cursor cursor = ct.getContentResolver().query(myPerson,
                projection, null, null, null);

        if (cursor.moveToFirst()) {
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            Log.e("", "Found contact name");
            cursor.close();
            return name;
        }
        cursor.close();
        Log.e("","Not Found contact name");
        return addr;
    }
}
