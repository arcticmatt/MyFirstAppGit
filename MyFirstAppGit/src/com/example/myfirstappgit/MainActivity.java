package com.example.myfirstappgit;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;


public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.myfirstappgit.MESSAGE";
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    protected ArrayList <String> storedMessages = new ArrayList<String>();


    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);



        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();

                if (phoneNo.length()>0 && message.length()>0)
                {
                    storedMessages.add(message);
                    String[] messageArray = listToArray(storedMessages);
                    sendSMS(phoneNo, message);
                    showOwnMessage(messageArray);

                }
                else
                {
                    Toast.makeText(getBaseContext(),
                            "Please enter both phone number and message.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //sends an SMS message to another device
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
        public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }, new IntentFilter(SENT));

        //when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }, new IntentFilter(DELIVERED));


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);



        // this is the simpler version
        /*
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, DisplayMessageActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        //you CANNOT use an intent instead of PendingIntent (it will create an error)
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        */

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    public void showOwnMessage(String[] ownMessageArray)
    {
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ownMessageArray);
        ListView listView = (ListView) findViewById(R.id.listConvo);
        listView.setAdapter(adapter);

    }

    public String[] listToArray(ArrayList<String> storageArrayList)
    {

        String[] arrayStoredMessages = new String[storageArrayList.size()];
        for (int i=0; i<storageArrayList.size(); i++)
        {
            arrayStoredMessages[i] = storageArrayList.get(i);
        }
        return arrayStoredMessages;

    }

    /*
    @Override
    protected void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null)
        {
            String message = intent.getStringExtra(SmsReceiver.EXTRA_MESSAGE);

            Toast.makeText(getBaseContext(), message,
                    Toast.LENGTH_LONG).show();

            storedMessages.add(message);
            String[] messageArray = listToArray(storedMessages);
            showOwnMessage(messageArray);
        }
    }
    */



    /*
    public class ReceiveMessages extends Activity
    {
        ReceiveMessages myReceiver = null;
        Boolean myReceiverIsRegistered = false;
        protected void onCreate(Bundle savedInstanceState)
        {
            myReceiver = new ReceiveMessages();

        }
        protected void onResume()
        {
            if (!myReceiverIsRegistered)
            {
                registerReceiver(myReceiver, new IntentFilter("com.mycompany.myapp.SOME_MESSAGE"));
                myReceiverIsRegistered = true;
            }
        }
        protected void onPause()
        {
            if (myReceiverIsRegistered)
            {
                unregisterReceiver(myReceiver);
                myReceiverIsRegistered = false;
            }
        }




    }
    */

    public class SmsReceiver extends BroadcastReceiver
    {
        public final static String EXTRA_MESSAGE = "com.example.myfirstappgit.MESSAGE";
        private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            //---get the SMS message passed in---
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String str = "test";
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

                //---display the new SMS message---
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();


                storedMessages.add(str);
                String[] messageArray = listToArray(storedMessages);
                showOwnMessage(messageArray);

            }
        }

        /*
        public void showReceivedMessage(String[] ownMessageArray)
        {
            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, ownMessageArray);
            ListView listView = (ListView) findViewById(R.id.listConvo);
            listView.setAdapter(adapter);
        }
        */
    }





}
