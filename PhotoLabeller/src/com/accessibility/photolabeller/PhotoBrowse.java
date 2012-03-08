package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class PhotoBrowse extends Activity implements OnClickListener, OnPreparedListener
{
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final String TAG = "BROWSING";
	private GestureDetector gestureDetector;
	private SharedPreferences mPreferences;
	View.OnTouchListener gestureListener;
	ViewFlipper imageFrame;
	RelativeLayout slideShowBtn;
	Handler handler;
	Runnable runnable;
	ImageView imageView;
	RelativeLayout.LayoutParams params;
	List<String> ImageList;
    List<String> AudioList;
    MediaPlayer mp = new MediaPlayer();
    MediaPlayer m;
    int imageCount;
    int requestCode;
    String s;
    String audioPath;
    AudioManager audiomanager;
    
    
    
    //DATABASE globals
	DbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photobrowse);
		
		
		//Initialize database
		mHelper = new DbHelper(this);
		//Open data base Connections
		mDb = mHelper.getWritableDatabase();
		String[] columns = new String[] {"_id", DbHelper.COL_IMG, DbHelper.COL_AUD};
		mCursor = mDb.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
		//get the user settings
		mPreferences = getSharedPreferences(HomeScreen.PREF_NAME, Activity.MODE_WORLD_READABLE);
		
		audiomanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);

		File parentFolder = getFilesDir();
		Log.d(TAG, parentFolder.getPath().toString());
		mp.setOnPreparedListener(this);
		addFlipperImages(imageFrame, parentFolder);
		
		Utility.getMediaPlayer().reset();
		Utility.playInstructionsMP(this, R.raw.browsefullinstr, R.raw.browseshortinstr, mPreferences);

		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event) {
	
				int action = event.getAction();
				if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) && event.getPointerCount() == 2) {
					finish();
					return true;
				}
				
				if (gestureDetector.onTouchEvent(event))
					return true;
				else
					return false;
			}
		};
		handler = new Handler();
		imageFrame.setOnClickListener(PhotoBrowse.this);
		imageFrame.setOnTouchListener(gestureListener);
		//slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
		/*slideShowBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				runnable = new Runnable()
				{
					public void run()
					{
						handler.postDelayed(runnable, 3000);
						//imageFrame.showNext();
						Utility.getMediaPlayer().reset();
						mCursor.moveToNext();
						if(mCursor.isAfterLast()) {
							mCursor.moveToFirst();
						}
						s = mCursor.getString(1);
						audioPath = mCursor.getString(2);
						try 
						{
							params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
							FileInputStream imageStream = new FileInputStream(s);
							BitmapFactory.Options o = new BitmapFactory.Options();
							o.inPurgeable = true;
							o.inInputShareable = true;
							Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD(), null, o);
							playSoundEffects(R.raw.imagechange);
							imageView.setImageBitmap(imbm);
							mp.reset();
							playTag(audioPath);
						} 	
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				};
				handler.postDelayed(runnable, 500);
			}
		});*/
	}

	private void addFlipperImages(ViewFlipper flipper, File parent)
	{
		if (isDataBaseEmpty())
		{
			imageView = new ImageView(this);
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
			Bitmap imbm = BitmapFactory.decodeResource(getResources(), R.drawable.noimagefound);
			imageView.setImageBitmap(imbm);
			imageView.setLayoutParams(params);
			flipper.addView(imageView);
		}
		else {
			mCursor.moveToLast();
			s = mCursor.getString(1);
			audioPath = mCursor.getString(2);
			try 
			{
				imageView = new ImageView(this);
				params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
				FileInputStream imageStream = new FileInputStream(s);
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inPurgeable = true;
				o.inInputShareable = true;
				Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD(), null, o);
				imageView.setImageBitmap(imbm);
				imageView.setLayoutParams(params);
				flipper.addView(imageView);
			} 	
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	class MyGestureDetector extends SimpleOnGestureListener
	{
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			Utility.getMediaPlayer().reset();
			mp.reset();
			Utility.playInstructionsMP(PhotoBrowse.this, R.raw.browsefullinstr, R.raw.browseshortinstr, mPreferences);
			return true;
		}
		
		/*
         *  return to home screen on a double tap
         * 
         */
        public boolean onDoubleTap(MotionEvent e) {
			Utility.getMediaPlayer().reset();
        	if (!isDataBaseEmpty()) {
        		
				mp.reset();
				playTag(audioPath);
				
				//slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
				//slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
				handler.removeCallbacks(runnable);
				/*runnable = new Runnable()
				{
					public void run()
					{
						slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
					}
				};
				handler.postDelayed(runnable, 2000);*/
				return true;
			}
			else {
				Utility.getMediaPlayer().reset();
				Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this,R.raw.noimagesfound));
				Utility.getMediaPlayer().start();
				return true;
			}
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
        	if (!isDataBaseEmpty()) {
            	Utility.setImagePath(s);
            	Utility.setAudioPath(audioPath);
            	Utility.setRowId(mCursor.getInt(0));
            	startActivityForResult(new Intent(PhotoBrowse.this, DeleteOrShare.class),requestCode);
        	}
        	else {
        		Utility.getMediaPlayer().reset();
        		Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this, R.raw.noimagesfound));
        		Utility.getMediaPlayer().start();
        	}
       }
        
		@SuppressWarnings("static-access")
		public boolean onSingleTapUp(MotionEvent e)
		{			
			//slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			//slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			handler.removeCallbacks(runnable);
			/*runnable = new Runnable()
			{
				public void run()
				{
					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
				}
			};
			handler.postDelayed(runnable, 2000);*/
			return true;
		}
	
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY)
		{
			try 
			{
				if (e1.getY() - e2.getY() > SWIPE_MAX_OFF_PATH&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					Log.d("slideShow", "slideShowAccepted");
					audioPath = mCursor.getString(2);
					int duration = getAudioDuration(audioPath);
					Log.d("slideShow", "duration= " + duration);
					playTag(audioPath);
					runnable = new Runnable()
					{
						public void run()
						{
							Utility.getMediaPlayer().reset();
							mCursor.moveToNext();
							if(mCursor.isAfterLast()) {
								mCursor.moveToFirst();
							}
							s = mCursor.getString(1);
							audioPath = mCursor.getString(2);
							int wait = getAudioDuration(audioPath);
							handler.postDelayed(runnable, wait);
							//imageFrame.showNext();
							try 
							{
								params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
								FileInputStream imageStream = new FileInputStream(s);
								BitmapFactory.Options o = new BitmapFactory.Options();
								o.inPurgeable = true;
								o.inInputShareable = true;
								Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD(), null, o);
								playSoundEffects(R.raw.imagechange);
								imageView.setImageBitmap(imbm);
								mp.reset();
								playTag(audioPath);
							} 	
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					};
					handler.postDelayed(runnable, duration);
					
					return true;
				}
					
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					mp.reset();
					handler.removeCallbacks(runnable);
					imageFrame.setInAnimation(inFromRightAnimation());
					imageFrame.setOutAnimation(outToLeftAnimation());
					
					if (!isDataBaseEmpty()) 
					{
						mCursor.moveToNext();
						if(mCursor.isAfterLast())
						{
							if(isOnlyOnePicture()) {
								Utility.getMediaPlayer().reset();
								Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this, R.raw.onlyonepic));
								Utility.getMediaPlayer().start();
							}
							else {
								Utility.getMediaPlayer().reset();
								Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this, R.raw.endpic));
								Utility.getMediaPlayer().start();
							}
							mCursor.moveToLast();
						} else
						{
							Utility.getMediaPlayer().reset();
							s = mCursor.getString(1);
							audioPath = mCursor.getString(2);
							try 
							{
								params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
								FileInputStream imageStream = new FileInputStream(s);
								BitmapFactory.Options o = new BitmapFactory.Options();
								o.inPurgeable = true;
								o.inInputShareable = true;
								Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD(), null, o);
								playSoundEffects(R.raw.imagechange);
								imageView.setImageBitmap(imbm);
							} 	
							catch (FileNotFoundException e)
							{
								e.printStackTrace();
							} 
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
					else {
						Utility.getMediaPlayer().reset();
						Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this, R.raw.noimagesfound));
						Utility.getMediaPlayer().start();
					}
				}
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					mp.reset();
					handler.removeCallbacks(runnable);
					imageFrame.setInAnimation(inFromLeftAnimation());
					imageFrame.setOutAnimation(outToRightAnimation());
					
					if (!isDataBaseEmpty()) 
					{
						mCursor.moveToPrevious();
						if(mCursor.isBeforeFirst()) {
							if(isOnlyOnePicture()) {
								Utility.getMediaPlayer().reset();
								Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this,R.raw.onlyonepic));
								Utility.getMediaPlayer().start();
							}
							else {
								Utility.getMediaPlayer().reset();
								Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this, R.raw.endpic));
								Utility.getMediaPlayer().start();
							}
							mCursor.moveToFirst();
						} else
						{
							Utility.getMediaPlayer().reset();
							s = mCursor.getString(1);
							audioPath = mCursor.getString(2);
							try 
							{
								params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
								FileInputStream imageStream = new FileInputStream(s);
								BitmapFactory.Options o = new BitmapFactory.Options();
								o.inPurgeable = true;
								o.inInputShareable = true;
								Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD(), null, o);
								playSoundEffects(R.raw.imagechange);
								imageView.setImageBitmap(imbm);
							} 	
							catch (FileNotFoundException e)
							{
								e.printStackTrace();
							} 
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
					else {
						Utility.getMediaPlayer().reset();
						Utility.setMediaPlayer(MediaPlayer.create(PhotoBrowse.this,R.raw.noimagesfound));
						Utility.getMediaPlayer().start();
					}
				}
			}
			catch (Exception e)
			{
				// nothing
			}
			return false;
		}
	}

	public void onClick(View view) {
	
	}
	
	private Animation inFromRightAnimation()
	{
		Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.2f,
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	private Animation outToLeftAnimation() 
	{
		Animation outtoLeft = new TranslateAnimation(
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, -1.2f,
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	private Animation inFromLeftAnimation()
	{
		Animation inFromLeft = new TranslateAnimation(
											Animation.RELATIVE_TO_PARENT, -1.2f,
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	private Animation outToRightAnimation()
	{
		Animation outtoRight = new TranslateAnimation(
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, +1.2f,
											Animation.RELATIVE_TO_PARENT, 0.0f,
											Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
	private void playTag(String s)
	{	
    	try
        {	
    		
    		FileInputStream f = new FileInputStream(s);
        	mp.setDataSource(f.getFD());
    	} 
        catch (IllegalArgumentException e)
        {
    		Log.e(TAG, e.getMessage().toString());
    		e.printStackTrace();
    	} catch (IOException e) {
    		Log.e(TAG, e.getMessage().toString());
    		e.printStackTrace();
    	}
    	try
    	{	
    		Log.d("TAG", "just before prepareAsync");
    		Log.d("TAG", s);
    		mp.prepareAsync();
    	}
    	catch (IllegalStateException e)
    	{
    		Log.e(TAG, e.getMessage().toString());
    		//e.printStackTrace();
    	}
            	
	}
	
	private int getAudioDuration(String audioPath) {
		File file = new File(audioPath);
		MediaPlayer temp = new MediaPlayer();
		FileInputStream fs;
		FileDescriptor fd;
		int length = 0;
		try {
			fs = new FileInputStream(file);
			fd = fs.getFD();
			temp.setDataSource(fd);
			temp.prepare(); // might be optional
			length = temp.getDuration();
			temp.release();
		}
		catch(Exception e) {
			
		}
		return length;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		Log.d("TAG", "just before mp.start");
		int volume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.d(TAG, String.valueOf(volume));
		mp.setVolume(volume, volume);
		mp.start();
	}

	private void playSoundEffects(int imageId)
	{	
		if(m != null) {
			m.reset();
		}
    	m = MediaPlayer.create(this, imageId);
    	m.start();
    }
	
	private boolean isDataBaseEmpty() {
		Cursor cur = mDb.rawQuery("SELECT COUNT(*) FROM " + DbHelper.TABLE_NAME, null);
		if (cur != null) {
		    cur.moveToFirst();                       // Always one row returned.
		    if (cur.getInt (0) == 0)
		    {               // Zero count means empty table.
		        return true;
	        }
		    return false;
	    }
		return false;
	}
	
	private boolean isOnlyOnePicture() {
		Cursor cur = mDb.rawQuery("SELECT COUNT(*) FROM " + DbHelper.TABLE_NAME, null);
		if (cur != null) {
		    cur.moveToFirst();                       // Always one row returned.
		    if (cur.getInt (0) == 1)
		    {               // Zero count means empty table.
		        return true;
	        }
		    return false;
	    }
		return false;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mp.reset();
	}
	
	
	@Override
	public void onBackPressed() {
	   return;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 	   Log.d("CheckStartActivity","onActivityResult and resultCode = "+resultCode);
 	   super.onActivityResult(requestCode, resultCode, data);
 	   if(resultCode == 1) {
 		  //start the confirmation delete activity
 		  startActivityForResult(new Intent(this, DeleteImage.class), requestCode);
 	   }
 	   else if (resultCode == 2) {
 		   // start the keyboard activity
  		  startActivityForResult(new Intent(this, TouchKeyboard.class), requestCode);
 	   }
 	   else if (resultCode == 4) {
 		   // delete picture
 			Utility.getMediaPlayer().reset();
 			Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.deletedphoto));
 			Utility.getMediaPlayer().start();
 		    confirmDelete();
 	   }
 	   else if (resultCode == 5) {
 		   //Utility.getTextToSpeech().say("Sending image");
 		   startActivityForResult(new Intent(this, MailSender.class), requestCode);
 	   }
 	   else if (resultCode == 6) {
 		   Utility.getMediaPlayer().reset();
 		   Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.cancelleddeletion));
 		   Utility.getMediaPlayer().start();
 	   }
 	   else {
 		   Utility.getMediaPlayer().reset();
 		   Utility.setMediaPlayer(MediaPlayer.create(this, R.raw.browseshortinstr));
 		   Utility.getMediaPlayer().start();
 		   //result code = 3. do nothing
 	   }
    }
	
	public void confirmDelete() {
		// get the current row position
		int rowPosition = mCursor.getPosition();
		// delete image
		int rowId = Utility.getRowId();
		mDb.delete(DbHelper.TABLE_NAME, "_id = " + rowId, null);
		// load updated database
		mCursor.requery();
		if(isDataBaseEmpty()) {
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
			Bitmap imbm = BitmapFactory.decodeResource(getResources(), R.drawable.noimagefound);
			imageView.setImageBitmap(imbm);
		}
		else {
			// move cursor to the next in line picture
			mCursor.moveToFirst();
			mCursor.move(rowPosition);
			if(mCursor.isAfterLast()) {
				mCursor.moveToLast();
			}
			
			// show next in line picture
			s = mCursor.getString(1);
			audioPath = mCursor.getString(2);
			try 
			{
				params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
				FileInputStream imageStream = new FileInputStream(s);
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inPurgeable = true;
				o.inInputShareable = true;
				Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD(), null, o);
				playSoundEffects(R.raw.imagechange);
				imageView.setImageBitmap(imbm);
			} 	
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
    }
}

