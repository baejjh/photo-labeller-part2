package com.accessibility.photolabeller;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
	
	private int getCurrentFileNumber() {
		return mPreferences.getInt(FILE_NUMBER_KEY, -1);
	}
	
	private void updateCurrentFileNumber(int fileNumber) {
		SharedPreferences.Editor editor = mPreferences.edit();
		fileNumber = fileNumber + 1;
		editor.putInt(FILE_NUMBER_KEY, fileNumber);
		editor.commit();
	}
	
	private void recordTag() {
		Intent recordIntent = new Intent(this, TagRecorder.class);
		startActivity(recordIntent);
	}
		

}
