package com.accessibility.photolabeller;

import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;

/*
 * Home Screen Activity which presents user with three buttons:
 * Click - Takes user to ClickPicture Activity to click and save pictures
 * Browse - Takes user to Browse Activity to browse images and listen to tags
 * Options - Present options menu for voice instructions, camera options, etc.
 */
public class HomeScreen extends Activity implements OnLongClickListener, OnInitListener{
	
	private TextToSpeech speaker;
	private Button clickButton;
	private Button browseButton;
	private Button optionsButton;
	private SharedPreferences mPreferences; 
	private static final String TAG = "HOMESCREEN";
	private static final int MY_DATA_CHECK_CODE = 0;
	private static final String FILE_NUMBER = "fileNum";
	public static final String PREF_NAME = "myPreferences";
		
   
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // check if TTS installed on device
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        
        setFileNumbering();
        initializeButtons();
     }
    
    /*
     * Sets the file number counter in the application Shared Preferences
     * for the first time
     */
   	private void setFileNumbering() {
    	// initialize shared preferences
        mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        if(!mPreferences.contains(FILE_NUMBER)){
        	SharedPreferences.Editor editor = mPreferences.edit();
        	editor.putInt(FILE_NUMBER, 0);
        	editor.commit();
        }		
	}

	/*
     * Sets Long Click Listeners to the three buttons
     */
	private void initializeButtons() {
		clickButton = (Button)findViewById(R.id.clickButton);
		browseButton = (Button)findViewById(R.id.browseButton);
		optionsButton = (Button)findViewById(R.id.optionsButton);
						
		clickButton.setOnLongClickListener(this);
		browseButton.setOnLongClickListener(this);
		optionsButton.setOnLongClickListener(this);
	}

	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 * 
	 * Performs actions when any of the three buttons are long pressed
	 */
	public boolean onLongClick(View v) {
		int viewId = v.getId();
		switch(viewId){
		case R.id.clickButton:{ //go to ClickPicture activity
			
			Log.v(TAG, "Pressed Click Button :" + R.id.clickButton);
			
			Intent clickPictureIntent = new Intent(this, PhotoTaker.class);
			startActivity(clickPictureIntent);
			break;
			
		}
		case R.id.browseButton:{ //go to BrowsePicture activity
			Log.v(TAG, "Pressed Browse Button :" + R.id.browseButton);
			Intent browseIntent = new Intent(this, PhotoBrowser.class);
			startActivity(browseIntent);
			break;
			
		}
		case R.id.optionsButton:{ // go to SetOptions activity
			Log.v(TAG, "Pressed Options Button :" + R.id.optionsButton);
			break;
		}
		}
		return true;
	}
	
	public void onInit(int arg0) {
		speaker.setLanguage(Locale.US);
		//say("Welcome to Talking Memories.");
	}
	
	/*
	 * TTS speaks the string parameter
	 */
	private void say(String text2say) {
		speaker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * 
	 * Initialize TTS if already installed on device, otherwise install it
	 */
	protected void onActivityResult(
	        int requestCode, int resultCode, Intent data) {
	    if (requestCode == MY_DATA_CHECK_CODE) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            // success, create the TTS instance
	            speaker = new TextToSpeech(this, this);
	        } else {
	            // missing data, install it
	            Intent installIntent = new Intent();
	            installIntent.setAction(
	                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
	    }
	}
}