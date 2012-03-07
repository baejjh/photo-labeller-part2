package com.accessibility.photolabeller;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/*
 * Home Screen Activity which presents user with three buttons:
 * Click - Takes user to ClickPicture Activity to click and save pictures
 * Browse - Takes user to Browse Activity to browse images and listen to tags
 * Options - Present options menu for voice instructions, camera options, etc.
 */
public class HomeScreen extends Activity{

	private SharedPreferences mPreferences;
	private MenuView menuView;
	private DoubleClicker doubleClicker;
	private static final String TAG = "HOME SCREEN";
	private static final String FILE_NUMBER = "fileNum";
	public static final String PREF_NAME = "myPreferences";
	private static final String VOICE_INSTR_PREF = "voiceInstructions";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Initialize text to speech
		Utility.setTextToSpeech(getApplicationContext());
		
		menuView = (MenuView) findViewById(R.id.home_view);
		menuView.setFocusable(true);
		menuView.setFocusableInTouchMode(true);
		menuView.setRowListener(new MyRowListener());
		menuView.setButtonNames("Capture", "Browse", "Options");
		
		doubleClicker = new DoubleClicker();
	
		setFileNumbering();
		setInstructionPreferences();
		Utility.setVibrator((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
		Utility.playInstructionsMP(this, R.raw.hsfullinst, R.raw.hsshortinst, mPreferences);
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
					playTakePhotos();
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Browse");
					launchPhotoBrowse();
				} else {
					Log.v(TAG, "BROWSE OVER!");
					playBrowsePhotos();
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Options");
					launchOptions();
				} else {
					Log.v(TAG, "OPTIONS OVER!");
					playOptions();
				}
			}

		}
        
        public void focusChanged() {
        	doubleClicker.reset();
        }

		public void onTwoFingersUp() {
			exitApp();
		}
	}
    
    public void launchPhotoTaker() {
    	startActivity(new Intent(this, PhotoTaker.class));
    }
    
    public void launchPhotoBrowse() {
    	startActivity(new Intent(this, PhotoBrowse.class));
    }
    
    public void launchOptions() {
    	startActivity(new Intent(this, SetOptions.class));
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
	
	public void onStop(){
		super.onStop();
		/*if(mp != null) {
			mp.stop();
		}*/
	}
	
	public void  onPause(){
		super.onPause();
		/*if(mp != null) {
			mp.stop();
		}*/
	}
	
	public void onRestart(){
		super.onRestart();
		Utility.playInstructionsMP(this, R.raw.hsfullinst, R.raw.hsshortinst, mPreferences);
		menuView.requestFocus();
		menuView.resetButtonFocus();
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}
	
	public void exitApp() {
		if(Utility.getMediaPlayer() != null) {
			Utility.getMediaPlayer().stop();
		}
		Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.goodbye));
		Utility.getMediaPlayer().start();
		finish();
	}
	
	public void playTakePhotos() {
		if(Utility.getMediaPlayer() != null) {
			Utility.getMediaPlayer().stop();
		}
		Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.takephoto));
		Utility.getMediaPlayer().start();
	}
	
	public void playBrowsePhotos() {
		if(Utility.getMediaPlayer() != null) {
			Utility.getMediaPlayer().stop();
		}
		Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.browsephoto));
		Utility.getMediaPlayer().start();
	}
	
	public void playOptions() {
		if(Utility.getMediaPlayer() != null) {
			Utility.getMediaPlayer().stop();
		}
		Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.options));
		Utility.getMediaPlayer().start();
	}
}