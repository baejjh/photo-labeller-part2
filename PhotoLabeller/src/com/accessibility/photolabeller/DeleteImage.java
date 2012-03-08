package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class DeleteImage extends Activity {
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
		Utility.getMediaPlayer().reset();
		Utility.playInstructionsMP(this, R.raw.confirmdeletefullinstr,R.raw.confirmdeleteshortinstr, mPreferences);
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
					Utility.getMediaPlayer().reset();
					Utility.playInstructionsMP(DeleteImage.this, R.raw.confirmdeletefullinstr, R.raw.confirmdeleteshortinstr, mPreferences);
				}
			}
		}
        
        public void focusChanged() {
        	doubleClicker.reset();
        }

		public void onTwoFingersUp() {
			//launchPhotoBrowse();
			Intent in = new Intent();
			setResult(6, in);
			finish();
		}
	}	
}
