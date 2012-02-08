package com.accessibility.photolabeller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeleteOrShare extends Activity implements OnClickListener{

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteorshare);
        
        initializeButtons();
     }

	private void initializeButtons() {
		Button deleteItButton = (Button)findViewById(R.id.deleteItButton);
		Button shareItButton = (Button)findViewById(R.id.shareItButton);
		deleteItButton.setOnClickListener(this);
		shareItButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.deleteItButton) {
			startActivity(new Intent(this, DeleteImage.class)); 
			finish();
		} else if(v.getId() == R.id.shareItButton){
			// share activity
		} else {
			// cancel and go back button
			
		}
		
	}

}
