package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
	      Mail m = new Mail("salama.obada@gmail.com", "testandroid"); 
	 
	      String[] toArr = {"salama.obada@gmail.com", "nikhilkarkarey@gmail.com"}; 
	      m.setTo(toArr); 
	      m.setFrom("salama.obada@gmail.com"); 
	      m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device."); 
	      m.setBody("Email body."); 
	 
	      try { 
	    	  File fileSrc = new File(Utility.getImagePath());
	  		  File destFile = new File(Environment.getExternalStorageDirectory(), "sendFile.jpg");
	  		  Log.d("sendEmail", Environment.getExternalStorageDirectory().toString()+ "/sendFile.jpg");
	  		
	  		try {
	  			copyFile(fileSrc, destFile);
	  			Log.d("trySendEmail", "ok");
	  		} catch (IOException e) {
	  			Log.d("catchSendEmail", e.getMessage().toString());
	  		}
	  		String filePath = Environment.getExternalStorageDirectory().toString()+ "/sendFile.jpg";
	        m.addAttachment(filePath); 
	 
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
	
	public static void copyFile(File src, File dst) throws IOException
	{
	    FileChannel inChannel = new FileInputStream(src).getChannel();
	    FileChannel outChannel = new FileOutputStream(dst).getChannel();
	    try
	    {
	        inChannel.transferTo(0, inChannel.size(), outChannel);
	    }
	    finally
	    {
	        if (inChannel != null)
	            inChannel.close();
	        if (outChannel != null)
	            outChannel.close();
	    }
	}


}
