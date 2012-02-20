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
		optionView.setSelectedIndex(mPreferences.getInt("OPTIONS", 0));
		
		doubleClicker = new DoubleClicker();
		
		GlobalVariables.getTextToSpeech().say(VOICE_INSTR_PREF);
	}

    private class MyRowListener implements RowListener {
    	
        public void onRowOver() {
        	Btn focusedButton = optionView.getFocusedButton();
			doubleClicker.click(focusedButton);

			if (focusedButton == Btn.ONE) {
				if (doubleClicker.isDoubleClicked()) {
					setVoiceInstructions(0);
				} else {
					GlobalVariables.getTextToSpeech().say("Full Instructions");
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					setVoiceInstructions(1);
				} else {
					GlobalVariables.getTextToSpeech().say("Short Instructions");
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					setVoiceInstructions(2);
				} else {
					GlobalVariables.getTextToSpeech().say("None");
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
		editor.putInt("OPTIONS", instructionLevel);
		editor.commit();
		
		finish();
	}

}
