package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;

public class SetOptions extends Activity implements OnCompletionListener{
	
	public static final String PREF_NAME = "myPreferences";
	private SharedPreferences mPreferences;
	private MediaPlayer m;
	private OptionsView optionView;
	private DoubleClicker doubleClicker;
	private int selectedButton;
	
	private static final String INST_VERBOSE = "Option screen. " +
			"Choose how much voice instruction you want to here on each page." +
			"Touch screen for prompts. Double click button for selection.";
	private static final String INST_SHORT = "Option screen.";
	private static final String VOICE_INSTR_PREF = "voiceInstructions";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setoptions);
		
		mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

		optionView = (OptionsView) findViewById(R.id.options_view);
		optionView.setFocusable(true);
		optionView.setFocusableInTouchMode(true);
		optionView.setRowListener(new MyRowListener());
		optionView.setButtonNames("Full Instructions", "Short Instructions", "None");
		optionView.setSelectedIndex(mPreferences.getInt(VOICE_INSTR_PREF, 0));
		
		doubleClicker = new DoubleClicker();
		
		Utility.playInstructions(INST_VERBOSE, INST_SHORT, mPreferences);
	}

    private class MyRowListener implements RowListener {
    	
        public void onRowOver() {
        	Btn focusedButton = optionView.getFocusedButton();
			doubleClicker.click(focusedButton);

			if (focusedButton == Btn.ONE) {
				if (doubleClicker.isDoubleClicked()) {
					playSelectionConfirmation(focusedButton);
					selectedButton = 0;
					
				} else {
					Utility.getTextToSpeech().say("Full Instructions");
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					playSelectionConfirmation(focusedButton);
					selectedButton = 1;
					
				} else {
					Utility.getTextToSpeech().say("Short Instructions");
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					playSelectionConfirmation(focusedButton);
					selectedButton = 2;
					
				} else {
					Utility.getTextToSpeech().say("None");
				}
			}

		}
        
		private void playSelectionConfirmation(Btn focusedButton) {
			Utility.getTextToSpeech().stop();
			if (focusedButton == Btn.ONE) {
				m = MediaPlayer.create(SetOptions.this, R.raw.fullinstrset);
				
			} else if (focusedButton == Btn.TWO) {
				m = MediaPlayer.create(SetOptions.this, R.raw.shortinstrset);
				
			} else if (focusedButton == Btn.THREE) {
				m = MediaPlayer.create(SetOptions.this, R.raw.noinstrset);
				
			}
			
			m.setOnCompletionListener(SetOptions.this);
	    	m.start();
			
		}

		public void focusChanged() {
        	doubleClicker.reset();
        }

		public void onTwoFingersUp() {
			finish();
		}
	}

	// set the voice instruction to the given instructionLevel
	// where instructionLevel 0 = full voice instructions on each screen
	//		 instructionLevel 1 = only screen names spoken out
	//       instructionLevel 2 = no voice instructions
	private void setVoiceInstructions(int instructionLevel) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(VOICE_INSTR_PREF, instructionLevel);
		editor.commit();
		
		finish();
	}

	@Override
	public void onCompletion(MediaPlayer m) {
		setVoiceInstructions(selectedButton);
		
	}

}
