package com.example.myfirstappgit;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;


public class MainActivity extends Activity {


    public final static String EXTRA_MESSAGE = "com.example.myfirstappgit.MESSAGE";
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;

    private TextView textCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        textCount = (TextView) findViewById(R.id.charCounter);

        /*
        TextWatcher is an interface. The new TextWatcher, therefore,
        is not an object - it is an anonymous class, which must
        implement the three methods of the interface.

        When an object of this type is attached to an Editable
        (in this case, the txtMessage TextView), its methods
        will be called when the text is changed.
        */
        final TextWatcher mTextEditorWatcher = new TextWatcher(){

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){

                textCount.setText(String.valueOf(s.length()));
                if(s.length() == 160){

                    textCount.setTextColor(Color.RED);
                } else textCount.setTextColor(Color.BLACK);
            }

            public void afterTextChanged(Editable s){}


        };

        //---adds a TextWatcher to the list of those whose methods are called
        //---whenever this TextView's text changes
        txtMessage.addTextChangedListener(mTextEditorWatcher);

        //---used instead of android:onclick, in order to support more APIs
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();


                //---ensures that only messages that meet the char limit
                //---are sent and shown
                if (phoneNo.length() > 0 && message.length() > 0 && message.length() <= 160)
                {
                    sendSMS(phoneNo, message);
                    txtMessage.setText(R.string.blank_string);

                }

                //---makes and shows toast that tells user to reduce character limit
                else if (message.length() > 160) {
                    Toast.makeText(getBaseContext(),
                            "Please reduce the number of characters to 160 or below",
                            Toast.LENGTH_SHORT).show();
                }

                //---makes and shows toast that tells user to enter aa recipient
                //---and a message
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

        /*
        Makes the PendingIntent to set off the first onReceive method.
        The intent carries the String SENT, as seen above.
        getBroadcast is used in place of a constructor,
        and retrieves a PI that will perform a broadcast,
        like calling Context.sendBroadcast.
        */
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        //---makes the PendingIntent to set off the second onReceive method.
        //---The intent caries the String DELIVERED, as seen above
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //when the SMS has been sent

        /*
        registerReceiver is run with an instance of BroadcastReceiver
        (which receives intents, usually sent by sendBroadcast).
        This registerReceiver method does not check the result yet,
        it just registers the BroadcastReceiver to be run in the main activity,
        i.e. it does not check any intents with the intent filter.

        BroadcastReceiver is an abstract class; the parameter seen below
        creates an anonymous CLASS, which must implement the onReceive method.

        A switch-case is used in order to determine which toast to show.

        When the PendingIntent is broadcast when the message is
        successfully sent/failed by the sendTextMessage method, as
        seen below. The result code from that broadcast is one of the five
        capped words below
         */
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

        /*
        sentPI is broadcasted when the message is sucessfully sent/failed.
        B/c the BroadcastReceiver was registered above, and b/c the broadcasted
        Intent in sentPI matches the filter (SENT), the BroadcastReceiver receives
        the broadcasted intent from sentPI, and thus its onReceive method is called.

        deliveredPi is is broadcast when the message is delivered to the recipient.
        The raw pdu of the status report is in the extended data ("pdu"). - taken
        from the android developers website

        for the SmsManager, getDefault() is used instead of a constructor
         */
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        ContentValues values = new ContentValues();
        values.put("address", "to" + phoneNumber);
        values.put("body", message);
        getContentResolver().insert(Uri.parse("content://sms/sent"), values);



        // this is the simpler version
        /*
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, DisplayMessageActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        //you CANNOT use an intent instead of PendingIntent (it will create an error)
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        */
    }

}
