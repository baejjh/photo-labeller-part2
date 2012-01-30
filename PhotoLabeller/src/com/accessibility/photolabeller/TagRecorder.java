package com.accessibility.photolabeller;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		currentFileNumber = getCurrentFileNumber();
	}

	@Override
	public void onClick(View v) {
		
		if(!isRecording) {
			String fileName = audioFileName + currentFileNumber;
			String internalStoragePath = getFilesDir().toString();
			
			recorder = new AudioRecorder(fileName, internalStoragePath);
			try {
				
				recorder.start();
				isRecording = true;
				button.setText(R.string.stopRecording);
				Log.d(TAG, "RECORDING");
				
			} catch (Exception e)  {
				e.printStackTrace();
			}
				
		} else {
			try {
				recorder.stop();
				updateCurrentFileNumber(currentFileNumber);
				button.setText(R.string.startRecording);
				isRecording = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
							
		}
		
	}

	private int getCurrentFileNumber() {
		return mPreferences.getInt(FILE_NUMBER_KEY, -1);
	}
	
	private void updateCurrentFileNumber(int fileNumber) {
		SharedPreferences.Editor editor = mPreferences.edit();
		fileNumber = fileNumber + 1;
		editor.putInt(FILE_NUMBER_KEY, fileNumber);
		editor.commit();
	}
	

}
