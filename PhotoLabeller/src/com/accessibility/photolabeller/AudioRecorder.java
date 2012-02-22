package com.accessibility.photolabeller;

import java.io.IOException;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * @author <a href="http://www.benmccann.com">Ben McCann</a>
 */
	public class AudioRecorder {
		private static final String TAG = "AUDIORECORDER";
		final MediaRecorder recorder = new MediaRecorder();
		final String path;
		final String internalStoragePath;
	  	
  /**
   * Creates a new audio recording at the given path (relative to root of SD card).
   */
  public AudioRecorder(String path, String internalStoragePath) {
	  this.internalStoragePath = internalStoragePath;
	  this.path = sanitizePath(path);
   }


  private String sanitizePath(String path) {
	  if (!path.startsWith("/")) {
		  path = "/" + path;
	  }
	  if (!path.contains(".")) {
		  path += ".3gp";
	  }
    
	  String audioPath = internalStoragePath + path;
	  Log.d(TAG, audioPath);
	  return audioPath;
    
      //return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
   
  }

  /**
   * Starts a new recording.
   */
  public void start() throws IOException {
	 
	  /*String state = android.os.Environment.getExternalStorageState();
	  if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
		  throw new IOException("SD Card is not mounted.  It is " + state + ".");
	  }

	    // make sure the directory we plan to store the recording in exists
	    File directory = new File(path).getParentFile();
	    if (!directory.exists() && !directory.mkdirs()) {
	      throw new IOException("Path to file could not be created.");
	    }*/
	    
	    

	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	    recorder.setOutputFile(path);
	    
	    Log.d(TAG, path);
	    recorder.prepare();
	    recorder.start();
	    
  }

  /**
   * Stops a recording that has been previously started.
   */
  public void stop() throws IOException {
	  recorder.stop();
	  recorder.release();
  }
  

}