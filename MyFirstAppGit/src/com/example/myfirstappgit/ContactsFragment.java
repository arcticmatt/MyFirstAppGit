package com.example.myfirstappgit;

/**
 * Created by MattLim on 6/3/13.
 */
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.provider.ContactsContract.Contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ContactsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter contactsAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    AutoCompleteTextView contactsView;

    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.contact_list, container, false);
        contactsView = (AutoCompleteTextView) V.findViewById(R.id.contacts);
        return V;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);





        /*// Create an empty adapter we will use to display the loaded data.
        contactsAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.auto_complete, null,
                new String[]{Contacts.DISPLAY_NAME, Contacts._ID},
                new int[]{R.id.name, R.id.number}, 0);
*/

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(1, null, this);
    }



    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i("FragmentComplexList", "Item clicked: " + id);
    }*/

    // These are the Contacts rows that we will retrieve.
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            //ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            //ContactsContract.Contacts.CONTACT_STATUS,
            //ContactsContract.Contacts.CONTACT_PRESENCE,
            //ContactsContract.Contacts.PHOTO_ID,
            //ContactsContract.Contacts.LOOKUP_KEY,
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = ContactsContract.Contacts.CONTENT_URI;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(getActivity(), baseUri,
                null,
                null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor people) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        //contactsAdapter.swapCursor(data);

        //contactsAdapter.setStringConversionColumn(data.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));

        final LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());

        View v = inflater.inflate( R.layout.contact_list, null, false);

        //com.example.myfirstappgit.CustomAutoComplete textView =
                //(com.example.myfirstappgit.CustomAutoComplete) v.findViewById(R.id.contacts);


        //textView.setAdapter(contactsAdapter);

        //textView.setAdapter(contactsAdapter);


        mPeopleList = new ArrayList<Map<String, String>>();
        //PopulatePeopleList();
        //mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.contacts);


        while (people.moveToNext()) {
            String contactName = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = people
                    .getString(people
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)){
                // You know have the number so now query it like this
                Cursor phones = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                while (phones.moveToNext()){
                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));
                    Map<String, String> NamePhoneType = new HashMap<String, String>();
                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);
                    if(numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else
                    if(numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if(numberType.equals("2"))
                        NamePhoneType.put("Type",  "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");
                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
        //startManagingCursor(people);

        mTxtPhoneNo = (AutoCompleteTextView) v.findViewById(R.id.contacts);



        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

                String name  = map.get("Name");
                String number = map.get("Phone");
                mTxtPhoneNo.setText(""+name+"<"+number+">");

            }



        });

        mAdapter = new SimpleAdapter(getActivity(), mPeopleList, R.layout.custcontview,
                new String[] { "Name", "Phone", "Type" }, new int[] {
                R.id.ccontName, R.id.ccontNo, R.id.ccontType });
        mTxtPhoneNo.setAdapter(mAdapter);



    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        contactsAdapter.swapCursor(null);
    }
}



