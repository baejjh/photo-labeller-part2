package com.accessibility.photolabeller;

import java.io.IOException;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;

/*
 * Activity that allows user to either proceed with voice tagging
 * or skip tagging and go back to taking another picture.
 */
public class TagOrSkip extends Activity implements OnCompletionListener {

	private static final String FILE_NUMBER_KEY = "fileNum";
	private int currentFileNumber;
	private SharedPreferences mPreferences;
	private AudioRecorder recorder;
	private boolean isRecording;
	private static final String audioFileName = "tm_file";
	public static final String PREF_NAME = "myPreferences";
	private static final String TAG = "TAG_RECORDER";
	private static final String VERBOSE_INST_TAGORSKIP = "Tag or skip.";
	MediaPlayer mp = new MediaPlayer();

	private MenuView menuView;
	private DoubleClicker doubleClicker;

	//DataBase globals
	DbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagorskip);

		menuView = (MenuView) findViewById(R.id.menu_view3);
		menuView.setFocusable(true);
		menuView.setFocusableInTouchMode(true);
		menuView.setRowListener(new MyRowListener());
		menuView.setButtonNames("Tag", "Skip");

		doubleClicker = new DoubleClicker();

		//Initialize database
		mHelper = new DbHelper(this);
		//Open data base Connections
		mDb = mHelper.getWritableDatabase();
		String[] columns = new String[] {"_id", DbHelper.COL_IMG, DbHelper.COL_AUD};
		mCursor = mDb.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);

		Log.d(TAG, "Created TagRecorder");

		//mPreferences = getSharedPreferences(HomeScreen.PREF_NAME, Activity.MODE_PRIVATE);
		initializeUI();
		isRecording = false;
		GlobalVariables.getTextToSpeech().say(VERBOSE_INST_TAGORSKIP);
	}

	private void initializeUI() {
		mPreferences = getSharedPreferences(HomeScreen.PREF_NAME, Activity.MODE_WORLD_READABLE);
		currentFileNumber = getCurrentFileNumber();		
	}

	private class MyRowListener implements RowListener {

		public void onRowOver() {
			Btn focusedButton = menuView.getFocusedButton();
			doubleClicker.click(focusedButton);
			
			if (isRecording)
				stopRecording();
			else {
				if (focusedButton == Btn.ONE) {
					if (doubleClicker.isDoubleClicked()) {
						Log.v(TAG, "Double Clicked - Tag");
						startRecording();
					} else {
						Log.v(TAG, "TAG OVER!");
						GlobalVariables.getTextToSpeech().say("Tag Photo");
					}
				} else if (focusedButton == Btn.TWO) {
					if (doubleClicker.isDoubleClicked()) {
						Log.v(TAG, "Double Clicked - Skip");
						finish();
					} else {
						Log.v(TAG, "SKIP OVER!");
						GlobalVariables.getTextToSpeech().say("Skip Tagging");
					}
				}
			}

		}

		public void focusChanged() {
			doubleClicker.reset();
		}
	}

	public void startRecording() {
		if (!isRecording) {
			GlobalVariables.getTextToSpeech().stop();
			// set the file name using the file counter and create path to save file
			String fileName = audioFileName + currentFileNumber;
			String internalStoragePath = getFilesDir().toString();

			recorder = new AudioRecorder(fileName, internalStoragePath);
									
			MediaPlayer m = MediaPlayer.create(this, R.raw.recprompt);
			m.setOnCompletionListener(this);
	    	m.start();
		}
	}

	public void stopRecording() {
		try {
			// stop recording, update the file number counter in Shared Preferences and exit activity to return to camera
			recorder.stop();
			mCursor.moveToLast();
			ContentValues cv = new ContentValues(1);
			cv.put(DbHelper.COL_AUD, mCursor.getString(1).replace(".jpg", ".3gp"));
			mDb.update(DbHelper.TABLE_NAME, cv, "iFile = ?", new String[] { mCursor.getString(1) });
			mCursor.requery();
			mCursor.moveToLast();
			Log.d(TAG, mCursor.getString(0) + ", " + mCursor.getString(1) + ", " + mCursor.getString(2));
			updateCurrentFileNumber(currentFileNumber);
			isRecording = false;
			finish();
		} catch (IOException e) {
			Log.d(TAG, e.getMessage().toString());
			e.printStackTrace();
		}
	}

	/*
	 * get the current index for file numbering
	 */
	private int getCurrentFileNumber() {
		return mPreferences.getInt(FILE_NUMBER_KEY, -1);
	}

	/*
	 * increment the index used for file numbering, stored in the Shared Preferences
	 */
	private void updateCurrentFileNumber(int fileNumber) {
		SharedPreferences.Editor editor = mPreferences.edit();
		fileNumber = fileNumber + 1;
		editor.putInt(FILE_NUMBER_KEY, fileNumber);
		editor.commit();
	}

	/*
	private void recordTag() {
		Intent recordIntent = new Intent(this, TagRecorder.class);
		startActivity(recordIntent);
		// finish after calling TagRecorder activity, so that TagRecorder on finishing
		// return directly to camera
		finish(); 
	}
	 */

	@Override
	public void onPause() {
		super.onPause();
		GlobalVariables.getTextToSpeech().stop();
		//finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//finish();
	}

	//this is called when the screen rotates.
	// (onCreate is no longer called when screen rotates due to manifest, see: android:configChanges)
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.tagorskip);

		initializeUI();
	}

	@Override
	public void onCompletion(MediaPlayer m) {
		try {
			recorder.start();
			isRecording = true;
			Log.d(TAG, "RECORDING");
		} catch (Exception e) {
			Log.d(TAG, e.getMessage().toString());
			e.printStackTrace();
		}
	}

	/*
	private void playSoundEffects(int imageId)
	{	
    	MediaPlayer m = MediaPlayer.create(this, imageId);
    	m.start();
    }*/
}
