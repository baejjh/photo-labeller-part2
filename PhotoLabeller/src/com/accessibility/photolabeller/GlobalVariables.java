package com.accessibility.photolabeller;

import android.app.Application;

public class GlobalVariables extends Application{
	
	private static String imagePath;
	private static String audioPath;
	private static int rowId = -1;

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


}
