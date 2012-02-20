package com.accessibility.photolabeller;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

public class Utility extends Application {
	
	private static String imagePath;
	private static String audioPath;
	private static int rowId = -1;
	private static Vibrator vibrator;
	
	private static final String VOICE_INSTR_PREF = "voiceInstructions";
	
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
	
	public static void setVibrator(Vibrator v) {
		vibrator = v;
	}

	public static void playInstructions(String fullVoice, String shortVoice, SharedPreferences pref) {
		int option = pref.getInt(VOICE_INSTR_PREF, 0);
		if (option == 0)
			ttsProviderImpl.say(fullVoice);
		else if (option == 1)
			ttsProviderImpl.say(shortVoice);
		else {
			vibrator.vibrate(150);
		}
	}
}
