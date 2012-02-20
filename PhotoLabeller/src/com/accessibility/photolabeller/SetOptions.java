package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SetOptions extends Activity {
	
	public static final String PREF_NAME = "myPreferences";
	private SharedPreferences mPreferences;
	
	private OptionsView optionView;
	private DoubleClicker doubleClicker;
	
	private static final String VERBOSE_INST = "Choose how much voice instruction you want to here on each page.";
	private static final String VERBOSE_INST_SHORT = "Choose voice options.";
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
		
		Utility.playInstructions(VERBOSE_INST, VERBOSE_INST_SHORT, mPreferences);
	}

    private class MyRowListener implements RowListener {
    	
        public void onRowOver() {
        	Btn focusedButton = optionView.getFocusedButton();
			doubleClicker.click(focusedButton);

			if (focusedButton == Btn.ONE) {
				if (doubleClicker.isDoubleClicked()) {
					setVoiceInstructions(0);
				} else {
					Utility.getTextToSpeech().say("Full Instructions");
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					setVoiceInstructions(1);
				} else {
					Utility.getTextToSpeech().say("Short Instructions");
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					setVoiceInstructions(2);
				} else {
					Utility.getTextToSpeech().say("None");
				}
			}

		}
        
		public void focusChanged() {
        	doubleClicker.reset();
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

}
