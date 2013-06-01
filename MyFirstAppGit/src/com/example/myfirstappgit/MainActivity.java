package com.example.myfirstappgit;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
    protected ArrayList <String> storedMessages = new ArrayList<String>();
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
                } else textCount.setTextColor(Color.WHITE);
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
                //---are added to the storage ArrayList
                if (message.length() <= 160 && message.length() > 0) {
                    storedMessages.add(message);
                }

                //---creates an Array from the storage ArrayList
                String[] messageArray = listToArray(storedMessages);

                //---ensures that only messages that meet the char limit
                //---are sent and shown
                if (phoneNo.length() > 0 && message.length() > 0 && message.length() <= 160)
                {
                    sendSMS(phoneNo, message);
                    //showMessage(messageArray);
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



        // this is the simpler version
        /*
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, DisplayMessageActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        //you CANNOT use an intent instead of PendingIntent (it will create an error)
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        */

    }

    //---This is a simple method I composed that turns ArrayLists
    //---into Arrays. The Array is to be used for ListView.
    public String[] listToArray(ArrayList<String> storageArrayList)
    {

        String[] arrayStoredMessages = new String[storageArrayList.size()];
        for (int i=0; i<storageArrayList.size(); i++)
        {
            arrayStoredMessages[i] = storageArrayList.get(i);
        }
        return arrayStoredMessages;

    }


    public static class CursorLoaderListFragment extends ListFragment
            implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

        // This is the Adapter being used to display the list's data.
        SimpleCursorAdapter mAdapter;

        // If non-null, this is the current filter the user has provided.
        String mCurFilter;

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Give some text to display if there is no data.  In a real
            // application this would come from a resource.
            setEmptyText("No phone numbers");

            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);

            // Create an empty adapter we will use to display the loaded data.
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_2, null,
                    new String[] { "person", "body" },
                    new int[] { android.R.id.text1, android.R.id.text2 }, 0);
            setListAdapter(mAdapter);

            // Start out with a progress indicator.
            setListShown(false);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Place an action bar item for searching.
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            SearchView sv = new SearchView(getActivity());
            sv.setOnQueryTextListener(this);
            item.setActionView(sv);
        }

        public boolean onQueryTextChange(String newText) {
            // Called when the action bar search text has changed.  Update
            // the search filter, and restart the loader to do a new query
            // with this filter.
            String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
            // Don't do anything if the filter hasn't actually changed.
            // Prevents restarting the loader when restoring state.
            if (mCurFilter == null && newFilter == null) {
                return true;
            }
            if (mCurFilter != null && mCurFilter.equals(newFilter)) {
                return true;
            }
            mCurFilter = newFilter;
            getLoaderManager().restartLoader(0, null, this);
            return true;
        }

        @Override public boolean onQueryTextSubmit(String query) {
            // Don't care about this.
            return true;
        }

        @Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("FragmentComplexList", "Item clicked: " + id);
        }

        // These are the Contacts rows that we will retrieve.
        /*static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.CONTACT_STATUS,
                ContactsContract.Contacts.CONTACT_PRESENCE,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.LOOKUP_KEY,
        };*/

        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader, so we don't care about the ID.
            // First, pick the base URI to use depending on whether we are
            // currently filtering.


            /*Uri baseUri;
            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
                        Uri.encode(mCurFilter));
            } else {
                baseUri = ContactsContract.Contacts.CONTENT_URI;
            }*/

            Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
            /*Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri,
                    new String[] { "_id", "thread_id", "address", "person", "date",
                            "body", "type" }, null, null, null);
            startManagingCursor(cursor1);*/
            String[] columns = new String[] { "person", "body" };
            /*if (cursor1.getCount() > 0) {
                String count = Integer.toString(cursor1.getCount());
                Log.e("Count",count);
                while (cursor1.moveToNext()){
                    String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                    String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                    String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                    String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                    String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                }
            }*/


            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.

            /*String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                    + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                    + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";*/
            return new CursorLoader(getActivity(), mSmsinboxQueryUri,
                    new String[] {"person", "body"}, null, null,
                    null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap the new cursor in.  (The framework will take care of closing the
            // old cursor once we return.)
            mAdapter.swapCursor(data);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            mAdapter.swapCursor(null);
        }
    }

}
