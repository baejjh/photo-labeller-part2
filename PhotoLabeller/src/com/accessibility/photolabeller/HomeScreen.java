package com.accessibility.photolabeller;

import android.app.Activity;
import android.os.Bundle;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/*
 * Home Screen Activity which presents user with three buttons:
 * Click - Takes user to ClickPicture Activity to click and save pictures
 * Browse - Takes user to Browse Activity to browse images and listen to tags
 * Options - Present options menu for voice instructions, camera options, etc.
 */
public class HomeScreen extends Activity {

	private SharedPreferences mPreferences;
	private MenuView menuView;
	private DoubleClicker doubleClicker;
	private static final String TAG = "HOME SCREEN";
	private static final String FILE_NUMBER = "fileNum";
	public static final String PREF_NAME = "myPreferences";
	private static final String VERBOSE_INST = "Home Screen. Touch screen to navigate, and double tap to take actions.";
	private static final String VOICE_INSTR_PREF = "voiceInstructions";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Initialize text to speech
		GlobalVariables.setTextToSpeech(getApplicationContext());
		
		menuView = (MenuView) findViewById(R.id.home_view);
		menuView.setFocusable(true);
		menuView.setFocusableInTouchMode(true);
		menuView.setRowListener(new MyRowListener());
		menuView.setButtonNames("Capture", "Browse", "Options");
		
		doubleClicker = new DoubleClicker();
		
		// check if TTS installed on device
		// Intent checkIntent = new Intent();
		// checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		// startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

		setFileNumbering();
		setInstructionPreferences();
		playInstructions();
	}
	


	public void playInstructions() {
		GlobalVariables.getTextToSpeech().say(VERBOSE_INST);
	}
	
    private class MyRowListener implements RowListener {
    	
        public void onRowOver() {
        	Btn focusedButton = menuView.getFocusedButton();
			doubleClicker.click(focusedButton);

			if (focusedButton == Btn.ONE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Capture");
					launchPhotoTaker();
				} else {
					Log.v(TAG, "CAPTURE OVER!");
					GlobalVariables.getTextToSpeech().say("Take Photos");
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Browse");
					launchPhotoBrowse();
				} else {
					Log.v(TAG, "BROWSE OVER!");
					GlobalVariables.getTextToSpeech().say("Browse Photos");
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Options");
				} else {
					Log.v(TAG, "OPTIONS OVER!");
					GlobalVariables.getTextToSpeech().say("Options");
				}
			}

		}
        
        public void focusChanged() {
        	doubleClicker.reset();
        }
	}
    
    public void launchPhotoTaker() {
    	startActivity(new Intent(this, PhotoTaker.class));
    }
    
    public void launchPhotoBrowse() {
    	startActivity(new Intent(this, PhotoBrowse.class));
    }

	/*
	 * Sets the file number counter in the application Shared Preferences for
	 * the first time
	 */
	private void setFileNumbering() {
		// initialize shared preferences
		mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		if (!mPreferences.contains(FILE_NUMBER)) {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putInt(FILE_NUMBER, 0);
			editor.commit();
		}
	}
	
	private void setInstructionPreferences() {
		
		// initialize shared preferences
		mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		if (!mPreferences.contains(VOICE_INSTR_PREF)) {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putInt(VOICE_INSTR_PREF, 0);
			editor.commit();
		}
		
	}
	/*
	public void onInit(int arg0) {
		speaker.setLanguage(Locale.US);
		say(VERBOSE_INST);
	}
	
	/*


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 * 
	 * Initialize TTS if already installed on device, otherwise install it
	 */
	/*
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				speaker = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}
	*/
	public void onStop(){
		super.onStop();
		//GlobalVariables.getTextToSpeech().stop();
	}
	
	public void  onPause(){
		super.onPause();
		GlobalVariables.getTextToSpeech().stop();
		
	}
	public void onRestart(){
		super.onRestart();
		playInstructions();
		menuView.requestFocus();
	}

}