package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class DeleteOrShare extends Activity {

	private static final String VERBOSE_INST = "Delete this picture, share this picture, or cancel this action.";
	private static final String VERBOSE_INST_SHORT = "Delete, share, or cancel.";

	private SharedPreferences mPreferences;
	public static final String PREF_NAME = "myPreferences";
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
        
		mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		Utility.playInstructions(VERBOSE_INST, VERBOSE_INST_SHORT, mPreferences);
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
					Utility.getTextToSpeech().say("Delete Photo");
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Share");
					launchShareImage();
				} else {
					Log.v(TAG, "SHARE OVER!");
					Utility.getTextToSpeech().say("Share Photo");
				}
			} else if (focusedButton == Btn.THREE) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Cancel");
					launchPhotoBrowse();
				} else {
					Log.v(TAG, "CANCEL OVER!");
					Utility.getTextToSpeech().say("Cancel");
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
    	//startActivity(new Intent(this, ContactList.class));
		startActivity(new Intent(this, MailSender.class));
    	finish();
		
	}

}
