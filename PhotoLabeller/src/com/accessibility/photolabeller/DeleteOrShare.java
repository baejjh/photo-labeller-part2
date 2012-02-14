package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DeleteOrShare extends Activity {
	
	private static final String VERBOSE_INST_DELETEORSHARE = "Delete picture, share picture, or cancel action." +
			"  Touch screen for button prompts.";
	
	private static final String TAG = "DELETE SHARE";
	
	private MenuView menuView;
	private DoubleClicker doubleClicker;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteorshare);
        
		menuView = (MenuView) findViewById(R.id.menu_view);
		menuView.setFocusable(true);
		menuView.setFocusableInTouchMode(true);
		menuView.setRowListener(new MyRowListener());
		menuView.setButtonNames("Delete", "Share", "Cancel");
		
		doubleClicker = new DoubleClicker();
        
        GlobalVariables.getTextToSpeech().say(VERBOSE_INST_DELETEORSHARE);
     }

    private class MyRowListener implements RowListener {
    	
        public void onRowOver() {
        	Btn focusedButton = menuView.getFocusedButton();
			doubleClicker.click(focusedButton);

			if (focusedButton == Btn.ONE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Delete");
					launchDeleteImage();
				} else {
					Log.v(TAG, "DELETE OVER!");
					GlobalVariables.getTextToSpeech().say("Delete Photo");
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Share");
					launchShareImage();
				} else {
					Log.v(TAG, "SHARE OVER!");
					GlobalVariables.getTextToSpeech().say("Share Photo");
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Cancel");
					launchPhotoBrowse();
				} else {
					Log.v(TAG, "CANCEL OVER!");
					GlobalVariables.getTextToSpeech().say("Cancel");
				}
			}

		}
        
       

		public void focusChanged() {
        	doubleClicker.reset();
        }
	}
    
    public void launchDeleteImage() {
    	startActivity(new Intent(this, DeleteImage.class)); 
		finish();
    }
    
    public void launchPhotoBrowse() {
    	startActivity(new Intent(this, PhotoBrowse.class));
		finish();
    }
    
    public void launchShareImage() {
    	startActivity(new Intent(this, ContactList.class));
		finish();
		
	}

}
