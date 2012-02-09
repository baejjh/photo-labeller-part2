package com.accessibility.photolabeller;

import android.app.Activity;
import android.os.Bundle;

import java.sql.Timestamp;
import java.util.Stack;
import com.accessibility.photolabeller.HomeView.Button;
import com.accessibility.photolabeller.HomeView.RowListener;
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

	//private TextToSpeech speaker;
	private SharedPreferences mPreferences;
	private HomeView homeView;
	private Stack<ClickEntry> clickStack;
	private static final String TAG = "HOME SCREEN";
	private static final int MY_DATA_CHECK_CODE = 0;
	private static final String FILE_NUMBER = "fileNum";
	public static final String PREF_NAME = "myPreferences";
	private static final int DOUBLE_CLICK_DELAY = 1000; // 1 second = 1000
	private static final String VERBOSE_INST = "HomeScreen.	Touch screen for button names, and double click to activate button.";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Initialize text to speech
		GlobalVariables.setTextToSpeech(getApplicationContext());
		
		homeView = (HomeView) findViewById(R.id.home_view);
		
		homeView.setFocusable(true);
		homeView.setFocusableInTouchMode(true);
		homeView.setRowListener(new MyRowListener());
		
		clickStack = new Stack<ClickEntry>();
		ClickEntry entry = new ClickEntry(Button.NOTHING, new Timestamp(System.currentTimeMillis()));
		clickStack.push(entry);
		
		// check if TTS installed on device
		//Intent checkIntent = new Intent();
		//checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		//startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

		setFileNumbering();
		GlobalVariables.getTextToSpeech().say(VERBOSE_INST);
		
		
	}
	
		protected class ClickEntry {
		protected Button button;
		protected Timestamp time;
		
		ClickEntry(Button button, Timestamp time) {
			this.button = button;
			this.time = time;
		}
	}
	
    private class MyRowListener implements RowListener {
        public void onRowOver() {
			Button focusedButton = homeView.getFocusedButton();
			ClickEntry entry = new ClickEntry(focusedButton, new Timestamp(System.currentTimeMillis()));
			boolean doubleClicked = false;
			if (clickStack.size() == 3) {
				clickStack.remove(0);
			}

			clickStack.push(entry);
			if (clickStack.size() == 3) {
				ClickEntry entry1 = clickStack.get(1);
				ClickEntry entry2 = clickStack.get(2);
				if (entry1.button == entry2.button)
					doubleClicked = Math.abs((entry1.time.getTime() - entry2.time.getTime())) < DOUBLE_CLICK_DELAY;
			}
			
			if (focusedButton == Button.CAPTURE) {
				if (doubleClicked) {
					Log.v(TAG, "Double Clicked - Capture");
					launchPhotoTaker();
				} else {
					Log.v(TAG, "CAPTURE OVER!");
					//say("Take Photos");
					GlobalVariables.getTextToSpeech().say("Take Photos");
				}
			} else if (focusedButton == Button.BROWSE) {
				if (doubleClicked) {
					Log.v(TAG, "Double Clicked - Browse");
					launchPhotoBrowse();
				} else {
					Log.v(TAG, "BROWSE OVER!");
					//say("Browse Photos");
					GlobalVariables.getTextToSpeech().say("Browse Photos");
				}
			} else if (focusedButton == Button.OPTIONS) {
				if (doubleClicked) {
					Log.v(TAG, "Double Clicked - Options");
				} else {
					Log.v(TAG, "OPTIONS OVER!");
					//say("Go to Options");
					GlobalVariables.getTextToSpeech().say("Options");
				}
			}

		}
        
        public void focusChanged() {
        	clickStack.removeAllElements();
        	ClickEntry entry = new ClickEntry(Button.NOTHING, new Timestamp(System.currentTimeMillis()));
    		clickStack.push(entry);
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
				
	}
	public void onRestart(){
		super.onRestart();
		GlobalVariables.getTextToSpeech().say(VERBOSE_INST);
	}

}