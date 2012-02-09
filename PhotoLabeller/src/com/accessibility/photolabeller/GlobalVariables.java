package com.accessibility.photolabeller;

import android.app.Application;
import android.content.Context;

public class GlobalVariables extends Application{
	
	private static String imagePath;
	private static String audioPath;
	private static int rowId = -1;
	
	// Text to speech
	private static TtsProviderFactory ttsProviderImpl;

	public static String getImagePath() {
	    return imagePath;
	}

	public static void setImagePath(String var) {
	imagePath = var;
	}
	
	public static String getAudioPath() {
	    return audioPath;
	}

	public static void setAudioPath(String var) {
		audioPath = var;
	}
	
	public static int getRowId() {
	    return rowId;
	}

	public static void setRowId(int var) {
		rowId = var;
	}
	
	public static void setTextToSpeech(Context context) {
		ttsProviderImpl = TtsProviderFactory.getInstance();
		if (ttsProviderImpl != null) {
		    ttsProviderImpl.init(context);
		}
	}
	
	public static TtsProviderFactory getTextToSpeech() {
		return ttsProviderImpl;
	}


}
