package com.accessibility.photolabeller;

import com.accessibility.photolabeller.MenuView.Btn;
import com.accessibility.photolabeller.MenuView.RowListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

public class DeleteOrShare extends Activity {
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
		menuView.setButtonNames("Delete", "Share");
		
		doubleClicker = new DoubleClicker();
        
		mPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		Utility.getMediaPlayer().reset();
		Utility.playInstructionsMP(this, R.raw.delsharefullinstr,R.raw.delshareshortinstr, mPreferences);
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
					playDeletePhoto();
				}
			} else if (focusedButton == Btn.TWO) {
				if (doubleClicker.isDoubleClicked()) {
					Log.v(TAG, "Double Clicked - Share");
					launchShareImage();
				} else {
					Log.v(TAG, "SHARE OVER!");
					playSharePhoto();
				}
			}
		}
        
		public void focusChanged() {
        	doubleClicker.reset();
        }

		public void onTwoFingersUp() {
			Log.v(TAG, "Two Fingers Up");
			Intent in = new Intent();
			setResult(3,in);
			//launchPhotoBrowse();
			finish();
		}
	}
    
    public void launchDeleteImage() {
    	//startActivity(new Intent(this, DeleteImage.class));
    	Intent in = new Intent();
    	setResult(1,in);
		finish();
    }
    
    public void launchShareImage() {
    	Intent in = new Intent();
    	setResult(2, in);
    	finish();
	}
    
    @Override
    public void onBackPressed() {
       return;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    public void playSharePhoto() {
		Utility.getMediaPlayer().reset();
    	Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.sharephoto));
    	Utility.getMediaPlayer().start();
    }
    
    public void playDeletePhoto() {
		Utility.getMediaPlayer().reset();
    	Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.deletephoto));
    	Utility.getMediaPlayer().start();
    }

}
