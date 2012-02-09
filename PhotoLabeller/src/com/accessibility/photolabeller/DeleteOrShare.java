package com.accessibility.photolabeller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeleteOrShare extends Activity implements OnClickListener{
	private static final String VERBOSE_INST_DELETEORSHARE = "Delete picture, share picture, or cancel action." +
			"  Touch screen for button prompts.";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteorshare);
        
        initializeButtons();
        GlobalVariables.getTextToSpeech().say(VERBOSE_INST_DELETEORSHARE);
     }

	private void initializeButtons() {
		Button deleteItButton = (Button)findViewById(R.id.deleteItButton);
		Button shareItButton = (Button)findViewById(R.id.shareItButton);
		Button cancelButton = (Button)findViewById(R.id.cancelButton);
		deleteItButton.setOnClickListener(this);
		shareItButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.deleteItButton) {
			startActivity(new Intent(this, DeleteImage.class)); 
			finish();
		} else if(v.getId() == R.id.shareItButton){
			// share activity
		} else {
			startActivity(new Intent(this, PhotoBrowse.class));
			finish();
			
		}
		
	}

}
