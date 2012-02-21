package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class ContactList extends ListActivity implements AdapterView.OnItemClickListener{
	Cursor mContacts;
	private static String TAG = "CONTACT_LIST";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//Return all contacts ordered by name
		String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME 
		};
		mContacts = managedQuery(ContactsContract.Contacts.CONTENT_URI, projection, null, null,
				ContactsContract.Contacts.DISPLAY_NAME);
		
		//Display on contacts in a List view
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
				mContacts, new String[] {ContactsContract.Contacts.DISPLAY_NAME},
				new int[] {android.R.id.text1});
		setListAdapter(mAdapter);
		
		//Listen for item selections
		getListView().setOnItemClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// temp - try sending a test MMS
		File fileSrc = new File(Utility.getImagePath());
		File destFile = new File(Environment.getExternalStorageDirectory(), "sendFile.jpg");
		Log.d(TAG, Environment.getExternalStorageDirectory().toString()+ "/sendFile.jpg");
		
		try {
			copyFile(fileSrc, destFile);
		} catch (IOException e) {
			Log.d(TAG, e.getMessage().toString());
		}
		String filePath = Environment.getExternalStorageDirectory().toString()+ "/sendFile.jpg";
		
		
		Uri image_URI = Uri.parse(filePath);
		Intent mmsIntent = new Intent(Intent.ACTION_SEND, image_URI);
		mmsIntent.putExtra("sms_body", "Please see the attached image");
		mmsIntent.putExtra("address", "4528902666");
		Log.d(TAG, "Before Starting MSG Activity");
		mmsIntent.putExtra(Intent.EXTRA_STREAM, image_URI);
		mmsIntent.setType("image/png");
		startActivity(mmsIntent);	
	}

	public static void copyFile(File src, File dst) throws IOException
	{
	    FileChannel inChannel = new FileInputStream(src).getChannel();
	    FileChannel outChannel = new FileOutputStream(dst).getChannel();
	    try
	    {
	        inChannel.transferTo(0, inChannel.size(), outChannel);
	    }
	    finally
	    {
	        if (inChannel != null)
	            inChannel.close();
	        if (outChannel != null)
	            outChannel.close();
	    }
	}
	
	
}
