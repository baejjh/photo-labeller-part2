package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;

/*
 * Camera screen: Displays camera preview on the screen
 * Actions: Single tap on screen : Takes a photo
 * 			Double tap on screen : Goes back to the homescreen
 */
public class PhotoTaker extends Activity implements SurfaceHolder.Callback, ShutterCallback,
														PictureCallback, OnClickListener{
	
	private static final String FILE_NUMBER = "fileNum";
	private static final String TAG = "PHOTO_TAKER";
	private SharedPreferences mPreferences;
	private static final String picFileName = "tm_file";
	private GestureDetector gestureDetector;
	public static final String PREF_NAME = "myPreferences";
	public static final String VERBOSE_INST = "Camera View. Tap screen to take photo, or double tap to return to home screen.";
	public static final String INST_SHORT = "Camera View.";

	//DATABASE globals
	DbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;
	
	
    View.OnTouchListener gestureListener;
    Camera mCamera;
	SurfaceView mPreview;
	
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clickpicture);
		
		//Initialize database
		mHelper = new DbHelper(this);
		//Open data base Connections
		mDb = mHelper.getWritableDatabase();
		String[] columns = new String[] {"_id", DbHelper.COL_IMG, DbHelper.COL_AUD};
		mCursor = mDb.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
		
		// Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event))
                    return true;
                else
                    return false;
            }
        };
		        
		mPreview = (SurfaceView)findViewById(R.id.mPreview);
		mPreview.setOnClickListener(this);
		mPreview.setOnTouchListener(gestureListener);
        mPreview.getHolder().addCallback(this);
		mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mPreferences = getSharedPreferences(HomeScreen.PREF_NAME, Activity.MODE_PRIVATE);
		mCamera = Camera.open();
		Utility.playInstructions(VERBOSE_INST, INST_SHORT, mPreferences);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mCamera.stopPreview();
		Utility.getTextToSpeech().stop();
		//Close all database connections
		//mDb.close();
		//mCursor.close();
	} 
	
	public void onRestart(){
		super.onRestart();
		Utility.playInstructions(VERBOSE_INST, INST_SHORT, mPreferences);
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mCamera.release();
		Log.d(TAG, "DESTROY");
		mDb.close();
		mCursor.close();
	}
	
	// Surface call back methods
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		Camera.Size selectedSize = sizes.get(0);
		params.setPreviewSize(selectedSize.width, selectedSize.height);
		mCamera.setParameters(params);
		mCamera.startPreview();
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(mPreview.getHolder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Action on clicking preview screen
	 * Snaps a picture with callback only when JPEG image ready
	 */
	public void onClick(View v) {
		Log.d(TAG,"IN ON_CLICK");
		//mCamera.takePicture(this, null, null, this);
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		//Save the picture
		//FileOutputStream outStream = null;
		
		/*
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}*/
		
		//if(mExternalStorageAvailable && mExternalStorageWriteable) {
			
			int fileNumber = getCurrentFileNumber();
						
			try {
				FileOutputStream fos = openFileOutput(picFileName + fileNumber + ".jpg", Context.MODE_PRIVATE);
				fos.write(data);
				fos.close();
				File s = getFilesDir();
				Log.d(TAG, "FILENAME: " + picFileName + fileNumber +".jpg");
				Log.d("TAG", s.getPath().toString());
				
				// add new image path to the data base.
				ContentValues cv = new ContentValues(2);
				cv.put(DbHelper.COL_IMG, s.getPath().toString() + "/"+picFileName + fileNumber + ".jpg");
				cv.put(DbHelper.COL_AUD, "NoTag");
				mDb.insert(DbHelper.TABLE_NAME, null, cv);
				//Refresh the list
				mCursor.requery();
				mCursor.moveToLast();
				/*
				File folder = Environment.getExternalStorageDirectory();
				File outputFile= new File(folder, "test.jpg");
				FileOutputStream fos = new FileOutputStream(outputFile);
				fos.write(data);
				fos.close();
				*/
						
				/*
				 
				Uri uriTarget = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
				String s = uriTarget.getPath();
				Log.d(TAG, s);
				OutputStream imageFileOS;
				imageFileOS = getContentResolver().openOutputStream(uriTarget);
				imageFileOS.write(data);
				imageFileOS.flush();
				imageFileOS.close();
				*/
												
			} catch (FileNotFoundException e) {
				Log.d(TAG, e.getMessage());
				//e.printStackTrace();
			} catch (IOException e) {
				Log.d(TAG, e.getMessage());
				//e.printStackTrace();
			} 
			
			tagOrSkip();
		//}
	}
	
	private void tagOrSkip() {
		Intent tagOrSkipIntent = new Intent(this, TagOrSkip.class);
		startActivity(tagOrSkipIntent);
		
	}

	/*
	 * get the current file number counter stored in the Shared Preferences
	 */
	private int getCurrentFileNumber() {
		return mPreferences.getInt(FILE_NUMBER, -1);
	}

	public void onShutter() {
		playSoundEffects(R.raw.camera1);
		
	}
	
	public void takePhoto(){
		mCamera.takePicture(this, null, null, this);
	}
	
	/*
	 * Inner GestureDetector class
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		
		/*
		 * Clicks picture on single tap
		 */
        public boolean onSingleTapConfirmed(MotionEvent e) {
        	Utility.getTextToSpeech().stop();
            takePhoto();
            return true;
        }
        
        public boolean onSingleTapUp(MotionEvent e) {
           return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
        	float velocityY) {
            return false;
        }
        
        /*
         *  return to home screen on a double tap
         * 
         */
        public boolean onDoubleTap(MotionEvent e) {
        	playSoundEffects(R.raw.imagechange);
        	finish();
			return true;
        }

    }
	
	@Override
	public void onBackPressed() {
	   return;
	}
	
	private void playSoundEffects(int imageId)
	{	
    	MediaPlayer m = MediaPlayer.create(this, imageId);
    	m.start();
    }

	
}
