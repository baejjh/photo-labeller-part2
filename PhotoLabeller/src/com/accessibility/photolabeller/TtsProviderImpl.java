package com.accessibility.photolabeller;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

private TextToSpeech tts;

@Override
public void say(String sayThis) {
    tts.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null);
}

@Override
public void onInit(int status) {
    /*Locale loc = new Locale("", "", "");
    if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
        tts.setLanguage(loc);
    }*/
	tts.setLanguage(Locale.US);
}

public void shutdown() {
    tts.shutdown();
}

@Override
public void init(Context context) {
	// TODO Auto-generated method stub
	if (tts == null) {
        tts = new TextToSpeech(context, this);
    }
	
}

@Override
public void stop() {
	tts.stop();
}

@Override
public boolean isSpeaking(){
	return tts.isSpeaking();
}


}
