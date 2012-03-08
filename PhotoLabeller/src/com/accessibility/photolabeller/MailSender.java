package com.accessibility.photolabeller;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;

public class MailSender extends Activity implements OnCompletionListener {
	
	private static final String TAG = "MAILSENDER";
	Timer timer;
	//private GestureDetector gestureDetector;
	//View.OnTouchListener gestureListener;
	Button send;
		
	@Override 
	public void onCreate(Bundle icicle) { 
	  super.onCreate(icicle); 
	  setContentView(R.layout.mailsender);
	  send = (Button) findViewById(R.id.send_email);
	  new MailSendTask(send).execute();
	
	  
	  
	// Gesture detection
      //gestureDetector = new GestureDetector(new MyGestureDetector());
      /*gestureListener = new View.OnTouchListener() {
          public boolean onTouch(View v, MotionEvent event) {
          	int action = event.getAction();
				if (action == MotionEvent.ACTION_POINTER_UP && event.getPointerCount() == 2) {
					finish();
					return true;
				}
				else if (gestureDetector.onTouchEvent(event))
                  return true;
              else
                  return false;
          }
      };*/
	  
	 //send.setOnTouchListener(gestureListener);
	  /*send.setOnClickListener(new View.OnClickListener() { 
	    public void onClick(View view) { 
	    	// makes asynchronous call to send email here
	    	new MailSendTask(send).execute();
	     } 
	  }); */
	}
	
	/*
	 * Inner GestureDetector class
	 */
	//class MyGestureDetector extends SimpleOnGestureListener {
		
		/*
		 * single tap should play back instructions
		 */
        /*public boolean onSingleTapConfirmed(MotionEvent e) {
        	return false;
        }*/
        
        /*public boolean onSingleTapUp(MotionEvent e) {
           return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
        	float velocityY) {
            return false;
        }*/
        
        /*
         *  double click takes a picture
         * 
         */
        /*public boolean onDoubleTap(MotionEvent e) {
        	new MailSendTask(send).execute();
            return true;
        }*/

    //}
	
	/**
	 * This class used to send email with attached image and audio tags
	 * asynchronously
	 * 
	 */
	private class MailSendTask extends AsyncTask<Void, Void, Boolean>{
		private static final String TAG = "MAILSENDTASK_ASYNC";
		private Mail m;
		private Button send;
				
		public MailSendTask(Button send){
			this.m = new Mail("talkingmemories@gmail.com", "talkingmemories2012"); 
			this.send = send;
		}
		
		// runs on UI thread
		@Override
		protected void onPreExecute(){
			Utility.getTextToSpeech().say("Sending email. Please wait.");
			send.setText("Sending email. Please wait");
			send.setClickable(false);
		}
		
		// run on background thread
		@Override
		protected Boolean doInBackground(Void... params) {
			
			TelephonyManager mgr = (TelephonyManager) getSystemService("phone");
			String phoneNumber = mgr.getLine1Number();
			Log.d(TAG, phoneNumber);
			
			  long delay = 5000;
			  long period = 5000;
			  timer = new Timer();
			  timer.scheduleAtFixedRate(
					  new TimerTask() {
						  public void run() {
							  Utility.getTextToSpeech().say("sending email. Please wait");
						  }
					  }, delay,period);
			  
			  // hard coded recipient email addresses to be changed later
		      String[] toArr = {Utility.getReceiverEmail()}; 
		      m.setTo(toArr); 
		      m.setFrom("talkingmemories@gmail.com"); 
		      m.setSubject("A friend with the phone number: " + phoneNumber + " is sharing a new tagged image with you."); 
		      m.setBody("Attached is a TalkingMemories image file and its associated audio tag." + "\n" +
		    		   "Play the audio tag using QuickTime."); 
		 
		      try { 
		    	  
		    	  // get path of attachments to send
		    	  File srcImgFile = new File(Utility.getImagePath());
		    	  File srcAudFile = new File(Utility.getAudioPath());
		  		  File destImgFile = new File(Environment.getExternalStorageDirectory(), "tmImage.jpg");
		  		  File destAudFile = new File(Environment.getExternalStorageDirectory(), "tmAudio.3gp");
		  		  Log.d(TAG, "Image attached: " + Environment.getExternalStorageDirectory().toString()+ "/tmImage.jpg");
		  		  Log.d(TAG, "Tag attached: " + Environment.getExternalStorageDirectory().toString()+ "/tmImage.3gp");
		  		
		  		try {
		  			
		  			//copy attachment files from internal storage to external storage
		  			Utility.copyFile(srcImgFile, destImgFile);
		  			Utility.copyFile(srcAudFile, destAudFile);
		  			Log.d(TAG, "After copying files to external location");
		  		} catch (IOException e) {
		  			Log.d(TAG, "Failed copying files to external location: " + e.getMessage().toString());
		  			return false;
		  		}
		  		
		  		// add attachments
		  		String fileImgPath = Environment.getExternalStorageDirectory().toString()+ "/tmImage.jpg";
		  		String fileAudPath = Environment.getExternalStorageDirectory().toString()+ "/tmAudio.3gp";
		        m.addAttachment(fileImgPath, "tmImage.jpg");
		        m.addAttachment(fileAudPath, "tmAudio.3gp");
		        Log.d(TAG, "After attaching attachments");
		        
		        
		        // try sending the email
		        if(m.send()) { 
		        	Log.d(TAG, "Email sent successfully");
		        	return true;
		          //Toast.makeText(MailSender.this, "Email was sent successfully.", Toast.LENGTH_LONG).show(); 
		        } else { 
		        	Log.d(TAG, "Email sending fail");
		        	return false;
		          //Toast.makeText(MailSender.this, "Email was not sent.", Toast.LENGTH_LONG).show(); 
		        } 
		      } catch(Exception e) {
		    	  Log.e(TAG, "Email send fail: " + e.getMessage().toString()); 
		    	  return false;
		        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
		        
		      }
			
		}
		
		// runs on UI thread
		@Override
		protected void onPostExecute(Boolean success){
			timer.cancel();
			if(success){
				Utility.getTextToSpeech().stop();
				playMessage(R.raw.photosharelong);
				send.setText("Photo shared successfully.");
				//send.setClickable(true);
			} else {
				Utility.getTextToSpeech().stop();
				playMessage(R.raw.photofaillong);
				send.setText("Photo share failed.");
				//send.setClickable(true);
			}
		}
		
		
		
	}	
	
	@Override
    public void onBackPressed() {
       return;
    }
	
	private void playMessage(int id)
	{	
    	MediaPlayer m = MediaPlayer.create(this, id);
    	m.setOnCompletionListener(this);
    	m.start();
    }

	@Override
	public void onCompletion(MediaPlayer mp) {
		//startActivity(new Intent(this, PhotoBrowse.class));
		Intent in = new Intent();
		setResult(3, in);
		finish();
	}
		
		

}
