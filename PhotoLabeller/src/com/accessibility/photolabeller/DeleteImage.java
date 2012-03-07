package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;

public class DeleteImage extends Activity implements OnCompletionListener{
	
	private static final String INST_VERBOSE = "Confirm to delete this picture.";
	private static final String INST_SHORT = "Confirm delete.";
	
	private SharedPreferences mPreferences;
	public static final String PREF_NAME = "myPreferences";
	private static final String TAG = "DELETE SHARE";
	
	private MenuView menuView;
	private DoubleClicker doubleClicker;
	
	//DATABASE globals
	DbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteimage);
        
		menuView = (MenuView) findViewById(R.id.menu_view2);
		menuView.setFocusable(true);
		menuView.setFocusableInTouchMode(true);
		menuView.setRowListener(new MyRowListener());
		menuView.setButtonNames("Confirm");
		
		doubleClicker = new DoubleClicker();
        
        //Initialize database
		mHelper = new DbHelper(this);
		//Open data base Connections
		mDb = mHelper.getWritableDatabase();
		String[] columns = new String[] {"_id", DbHelper.COL_IMG, DbHelper.COL_AUD};
		mCursor = mDb.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
        
		mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		Utility.playInstructions(INST_VERBOSE, INST_SHORT, mPreferences);
     }

    private class MyRowListener implements RowListener {
    	
        public void onRowOver() {
        	Btn focusedButton = menuView.getFocusedButton();
			doubleClicker.click(focusedButton);

			if (focusedButton == Btn.ONE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Confirm");
					//confirmDelete();
					Intent in = new Intent();
					setResult(4, in);
					finish();
				} else {
					Log.v(TAG, "CONFIRM OVER!");
					Utility.getTextToSpeech().say("Confirm Delete");
				}
			}
		}
        
        public void focusChanged() {
        	doubleClicker.reset();
        }

		public void onTwoFingersUp() {
			//launchPhotoBrowse();
			Intent in = new Intent();
			setResult(3, in);
			finish();
		}
	}
    
    public void confirmDelete() {
		int rowId = Utility.getRowId();
		mDb.delete(DbHelper.TABLE_NAME, "_id = " + rowId, null);
		mCursor.requery();
		mCursor.close();
		playSoundEffects(R.raw.paperrip);
		//GlobalVariables.setRowId(mCursor.getInt(0));
		
		//launchPhotoBrowse();
    }
    
    
    public void launchPhotoBrowse() {
    	startActivity(new Intent(this, PhotoBrowse.class));
		finish();
    }
	
    private void playSoundEffects(int imageId)
	{	
    	Utility.getTextToSpeech().stop();
    	MediaPlayer m = MediaPlayer.create(this, imageId);
    	m.setOnCompletionListener(this);
    	m.start();
    }

	@Override
	public void onCompletion(MediaPlayer m) {
		launchPhotoBrowse();
	}
}
