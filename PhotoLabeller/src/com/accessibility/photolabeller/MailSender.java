package com.accessibility.photolabeller;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MailSender extends Activity{
	
	
	@Override 
	public void onCreate(Bundle icicle) { 
	  super.onCreate(icicle); 
	  setContentView(R.layout.mailsender); 
	 
	  Button addImage = (Button) findViewById(R.id.send_email);
	 	 
	  addImage.setOnClickListener(new View.OnClickListener() { 
	    public void onClick(View view) { 
	      Mail m = new Mail("talkingmemories@gmail.com", "talkingmemories2012"); 
	 
	      String[] toArr = {"nikhilkarkarey@gmail.com", "salama.obada@gmail.com", "han@cs.washington.edu"}; 
	      m.setTo(toArr); 
	      m.setFrom("talkingmemories@gmail.com"); 
	      m.setSubject("You have recieved a new tagged image."); 
	      m.setBody("Attached is a TalkingMemories image file and its associated audio tag." + "\n" +
	    		   "Play the audio tag using QuickTime."); 
	 
	      try { 
	    	  File srcImgFile = new File(Utility.getImagePath());
	    	  File srcAudFile = new File(Utility.getAudioPath());
	  		  File destImgFile = new File(Environment.getExternalStorageDirectory(), "tmImage.jpg");
	  		  File destAudFile = new File(Environment.getExternalStorageDirectory(), "tmAudio.3gp");
	  		  Log.d("sendEmail", Environment.getExternalStorageDirectory().toString()+ "/tmImage.jpg");
	  		
	  		try {
	  			Utility.copyFile(srcImgFile, destImgFile);
	  			Utility.copyFile(srcAudFile, destAudFile);
	  			Log.d("trySendEmail", "ok");
	  		} catch (IOException e) {
	  			Log.d("catchSendEmail", e.getMessage().toString());
	  		}
	  		String fileImgPath = Environment.getExternalStorageDirectory().toString()+ "/tmImage.jpg";
	  		String fileAudPath = Environment.getExternalStorageDirectory().toString()+ "/tmAudio.3gp";
	        m.addAttachment(fileImgPath, "tmImage.jpg");
	        m.addAttachment(fileAudPath, "tmAudio.3gp");
	        
	        
	 
	        if(m.send()) { 
	          Toast.makeText(MailSender.this, "Email was sent successfully.", Toast.LENGTH_LONG).show(); 
	        } else { 
	          Toast.makeText(MailSender.this, "Email was not sent.", Toast.LENGTH_LONG).show(); 
	        } 
	      } catch(Exception e) { 
	        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
	        Log.e("MailApp", "Could not send email", e); 
	      } 
	    } 
	  }); 
	}
	

}
