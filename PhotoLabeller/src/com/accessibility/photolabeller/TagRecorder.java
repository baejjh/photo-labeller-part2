package com.accessibility.photolabeller;


import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TagRecorder extends Activity implements OnClickListener{
	
	private static final String FILE_NUMBER_KEY = "fileNum";
	private static final String TAG = "TAG_RECORDER";
	private SharedPreferences mPreferences;
	private static final String audioFileName = "tm_file";
	public static final String PREF_NAME = "myPreferences";
	private Button button;
	private int currentFileNumber;
	private AudioRecorder recorder;
	private boolean isRecording = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagrecorder);
		
		Log.d(TAG, "Created TagRecorder")	;			
		mPreferences = getSharedPreferences(HomeScreen.PREF_NAME, Activity.MODE_WORLD_READABLE);
		button = (Button)findViewById(R.id.StartandStop);
		button.setOnClickListener(this);
		
		// get the current file counter value from Shared  Preferences
		currentFileNumber = getCurrentFileNumber();
	}

	@Override
	public void onClick(View v) {
		if(!isRecording) {
			// set the file name using the file counter and create path to save file 
			String fileName = audioFileName + currentFileNumber;
			String internalStoragePath = getFilesDir().toString();
			
			recorder = new AudioRecorder(fileName, internalStoragePath);
			
			try {
				recorder.start();
				isRecording = true;
				button.setText(R.string.stopRecording);
				Log.d(TAG, "RECORDING");
				
			} catch (Exception e)  {
				Log.d(TAG, e.getMessage().toString());
				e.printStackTrace();
			}
				
		} else {
			try {
				// stop recording, update the file number counter in Shared Preferences
				// and exit activity to return to camera
				recorder.stop();
				updateCurrentFileNumber(currentFileNumber);
				button.setText(R.string.startRecording);
				isRecording = false;
				finish();
							
			} catch (IOException e) {
				Log.d(TAG, e.getMessage().toString());
				e.printStackTrace();
			}
							
		}
		
	}
	
	/*
	 * Returns current value of file counter from shared preferences
	 */
	private int getCurrentFileNumber() {
		return mPreferences.getInt(FILE_NUMBER_KEY, -1);
	}
	
	/*
	 * Increments the file counter in the shared preferences,
	 * where parameter fileNumber is the current file counter
	 * 
	 */
	private void updateCurrentFileNumber(int fileNumber) {
		SharedPreferences.Editor editor = mPreferences.edit();
		fileNumber = fileNumber + 1;
		editor.putInt(FILE_NUMBER_KEY, fileNumber);
		editor.commit();
	}
	

}
