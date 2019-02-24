package com.sudhanshujaisani.mywhatsapp;



import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallsFragment extends ListFragment {


    public CallsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
View view=super.onCreateView(inflater, container, savedInstanceState);
        ContentResolver contentResolver=inflater.getContext().getContentResolver();
        Uri uri=ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor=contentResolver.query(uri,null,null,null,null);
        SimpleCursorAdapter simpleCursorAdapter=new SimpleCursorAdapter(
                inflater.getContext(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{android.R.id.text1},
                0
                );
        setListAdapter(simpleCursorAdapter);
        return view;
    }
}
