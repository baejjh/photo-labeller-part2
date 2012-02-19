package com.accessibility.photolabeller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetOptions extends Activity implements OnClickListener {
	
	private static final String VOICE_INSTR_PREF = "voiceInstructions";
	private SharedPreferences mPreferences;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setoptions);
		
		//Initialize text to speech
		GlobalVariables.setTextToSpeech(getApplicationContext());
		
		Button fullInstrButton = (Button)findViewById(R.id.fullInstructionButton);
		Button partialInstrButton = (Button)findViewById(R.id.partialInstructionButton);
		Button noInstrButton = (Button)findViewById(R.id.noInstructionButton);
		
		fullInstrButton.setOnClickListener(this);
		partialInstrButton.setOnClickListener(this);
		noInstrButton.setOnClickListener(this);
		
	}



	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.fullInstructionButton){
			setVoiceInstructions(0);
		} else if( v.getId() == R.id.partialInstructionButton){
			setVoiceInstructions(1);
		} else {
			setVoiceInstructions(2);
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
