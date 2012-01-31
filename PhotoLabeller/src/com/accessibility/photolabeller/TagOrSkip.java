package com.accessibility.photolabeller;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * Activity that allows user to either proceed with voice tagging
 * or skip tagging and go back to taking another picture.
 */
public class TagOrSkip extends Activity implements OnClickListener {
	
	private static final String FILE_NUMBER_KEY = "fileNum";
	private int currentFileNumber;
	private SharedPreferences mPreferences;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tagorskip);
        
        mPreferences = getSharedPreferences(HomeScreen.PREF_NAME, Activity.MODE_PRIVATE);
        currentFileNumber = getCurrentFileNumber();           
        initializeButtons();
     }

	private void initializeButtons() {
		Button tagButton = (Button)findViewById(R.id.tagItButton);
		Button skipButton = (Button)findViewById(R.id.skipTagButton);
		tagButton.setOnClickListener(this);
		skipButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.skipTagButton) {
			updateCurrentFileNumber(currentFileNumber);
			finish();
		} else {
			recordTag();
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
	 * calls TagRecorder activity
	 */
	private void recordTag() {
		
		Intent recordIntent = new Intent(this, TagRecorder.class);
		startActivity(recordIntent);
		// finish after calling TagRecorder activity, so that TagRecorder on finishing
		// return directly to camera
		finish(); 
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//finish();
	}
		

}
