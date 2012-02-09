package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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

public class PhotoBrowse extends Activity implements OnClickListener, OnPreparedListener, OnInitListener
{
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final String TAG = "BROWSING";
	private GestureDetector gestureDetector;
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
    int imageCount;
    String s;
    String audioPath;
    TextToSpeech talker;
    
  //DATABASE globals
	DbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photobrowse);
		
		//Initialize database
		mHelper = new DbHelper(this);
		//Open data base Connections
		mDb = mHelper.getWritableDatabase();
		String[] columns = new String[] {"_id", DbHelper.COL_IMG, DbHelper.COL_AUD};
		mCursor = mDb.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
		
		// check if TTS installed on device
		//Intent checkIntent = new Intent();
		//checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		//startActivityForResult(checkIntent, 0);
		talker = new TextToSpeech(this, this);
		
		imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);

		File parentFolder = getFilesDir();
		Log.d(TAG, parentFolder.getPath().toString());
		mp.setOnPreparedListener(this);
		addFlipperImages(imageFrame, parentFolder);

		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event))
					return true;
				else
					return false;
			}
		};
		handler = new Handler();
		imageFrame.setOnClickListener(PhotoBrowse.this);
		imageFrame.setOnTouchListener(gestureListener);
		slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
		slideShowBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				runnable = new Runnable()
				{
					public void run()
					{
						handler.postDelayed(runnable, 3000);
						imageFrame.showNext();
					}
				};
				handler.postDelayed(runnable, 500);
			}
		});
	}

	private void addFlipperImages(ViewFlipper flipper, File parent)
	{
		//ImageList = new ArrayList<String>();
    	//AudioList = new ArrayList<String>();
		//imageCount = parent.listFiles().length;
		/*for (int count = 0; count < imageCount; count++) {
			s = parent.listFiles()[count].getAbsolutePath();
			if (s.endsWith(".jpg"))
			{
				ImageList.add(s);
			}
			else
			{
				AudioList.add(s);
			}
		}*/
		//ImageView imageView = new ImageView(this);
		//imageCount = ImageList.size();
		if (isDataBaseEmpty()) {
			say("No images found");
			imageView = new ImageView(this);
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
			Bitmap imbm = BitmapFactory.decodeResource(getResources(), R.drawable.noimagefound);
			imageView.setImageBitmap(imbm);
			imageView.setLayoutParams(params);
			flipper.addView(imageView);
			Log.d("TAG", "just before say");
			
		}
		else {
			//int row = GlobalVariables.getRowId();
			/*
			if(row != -1) {
				mCursor = mDb.
			} else {
				mCursor.moveToLast();
			}
			*/
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//imageView.setImageDrawable(Drawable.createFromPath(imagePath));
			//System.gc();
			
			playTag(audioPath);
		}
	}
	class MyGestureDetector extends SimpleOnGestureListener
	{
		@SuppressWarnings("static-access")
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			mp.reset();
			playTag(audioPath);
			// TODO Auto-generated method stub
			slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			handler.removeCallbacks(runnable);
			runnable = new Runnable()
			{
				public void run()
				{
					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
				}
			};
			handler.postDelayed(runnable, 2000);
			return true;
		}
		
		/*
         *  return to home screen on a double tap
         * 
         */
        public boolean onDoubleTap(MotionEvent e) {
        	playSoundEffects(R.raw.imagechange);
        	finish();
			return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
        	playSoundEffects(R.raw.imagechange);
        	GlobalVariables.setImagePath(s);
        	GlobalVariables.setAudioPath(audioPath);
        	GlobalVariables.setRowId(mCursor.getInt(0));
        	startActivity(new Intent(PhotoBrowse.this, DeleteOrShare.class));    
        	finish();
       }
        
        
	
		@SuppressWarnings("static-access")
		public boolean onSingleTapUp(MotionEvent e)
		{
			// TODO Auto-generated method stub
			slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
			slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
			handler.removeCallbacks(runnable);
			runnable = new Runnable()
			{
				public void run()
				{
					slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
				}
			};
			handler.postDelayed(runnable, 2000);
			return true;
		}
	
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY)
		{
			try 
			{
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					handler.removeCallbacks(runnable);
					imageFrame.setInAnimation(inFromRightAnimation());
					imageFrame.setOutAnimation(outToLeftAnimation());
					
					if (!isDataBaseEmpty()) 
					{
						mCursor.moveToNext();
						if(mCursor.isAfterLast())
						{
							playSoundEffects(R.raw.lock);
							mCursor.moveToLast();
						} else
						{
							mp.reset();
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
								playSoundEffects(R.raw.pageflip);
								imageView.setImageBitmap(imbm);
								//imageView.setLayoutParams(params);
								//imageFrame.addView(imageView);
								playTag(audioPath);
							} 	
							catch (FileNotFoundException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					//imageView.setImageDrawable(Drawable.createFromPath(imagePath));
					//System.gc();
					//imageFrame.showNext();*/
				}
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					handler.removeCallbacks(runnable);
					imageFrame.setInAnimation(inFromLeftAnimation());
					imageFrame.setOutAnimation(outToRightAnimation());
					
					if (!isDataBaseEmpty()) 
					{
						mCursor.moveToPrevious();
						if(mCursor.isBeforeFirst()) {
							playSoundEffects(R.raw.lock);
							mCursor.moveToFirst();
						} else
						{
							mp.reset();
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
								playSoundEffects(R.raw.pageflip);
								imageView.setImageBitmap(imbm);
								//imageView.setLayoutParams(params);
								//imageFrame.addView(imageView);
								playTag(audioPath);
							} 	
							catch (FileNotFoundException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					/*imageFrame.showPrevious();*/
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
    	//String internalAudioPath = s.replace(".jpg", ".3gp");
        // get file name only
        //int startIndex = internalAudioPath.indexOf("tm_file");
        //String filename = internalAudioPath.substring(startIndex);
        //Log.d(TAG, "audio file name : " + filename);
    	//Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" +filename);
    	//String externalAudioPath =  Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" +filename;	
        
        if (!s.equalsIgnoreCase("NoTag"))
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
        else
        {
        	say("No Tag Found");
        }
    	
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		Log.d("TAG", "just before mp.start");
		mp.start();
		// TODO Auto-generated method stub
		
	}
	
	public void onInit(int arg0) {
		talker.setLanguage(Locale.US);
		// say("Welcome to Talking Memories.");
	}
	
	/*
	 * TTS speaks the string parameter
	 */
	private void say(String text2say) {
		Log.d("TAG", "inside say");
		talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 * 
	 * Initialize TTS if already installed on device, otherwise install it
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				talker = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}
	
	
	private void playSoundEffects(int imageId)
	{	
    	MediaPlayer m = MediaPlayer.create(this, imageId);
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
}

