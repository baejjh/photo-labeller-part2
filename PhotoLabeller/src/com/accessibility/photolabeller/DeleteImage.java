package com.accessibility.photolabeller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class DeleteImage extends Activity implements OnClickListener {
	
	private static final String VERBOSE_INST_DELETE = "Confirm Delete picture, or cancel action." +
	"  Touch screen for button prompts.";
	
	//DATABASE globals
	DbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteimage);
        
        //Initialize database
		mHelper = new DbHelper(this);
		//Open data base Connections
		mDb = mHelper.getWritableDatabase();
		String[] columns = new String[] {"_id", DbHelper.COL_IMG, DbHelper.COL_AUD};
		mCursor = mDb.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
        
		GlobalVariables.getTextToSpeech().say(VERBOSE_INST_DELETE);
        initializeButtons();
        
     }

	private void initializeButtons() {
		Button confirmButton = (Button)findViewById(R.id.confirmButton);
		Button cancelButton = (Button)findViewById(R.id.cancelButton);
		confirmButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.confirmButton) {
			int rowId = GlobalVariables.getRowId();
			mDb.delete(DbHelper.TABLE_NAME, "_id = " + rowId, null);
			mCursor.requery();
			mCursor.close();
			playSoundEffects(R.raw.paperrip);
			//GlobalVariables.setRowId(mCursor.getInt(0));
			
			// delete physical files
			startActivity(new Intent(this, PhotoBrowse.class));
			finish();
		} else {
			startActivity(new Intent(this, PhotoBrowse.class));
			finish();
		}
	}
	
	private void playSoundEffects(int imageId)
	{	
    	MediaPlayer m = MediaPlayer.create(this, imageId);
    	m.start();
    }
}
