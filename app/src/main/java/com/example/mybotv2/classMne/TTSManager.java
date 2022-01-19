package com.example.mybotv2.classMne;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.File;
import java.util.Locale;

public class TTSManager {

    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    public void init(Context context){
        try{
            mTts = new TextToSpeech(context, onInitListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private TextToSpeech.OnInitListener onInitListener = status -> {
        Locale spanish = new Locale("es", "CO");
        if (status == TextToSpeech.SUCCESS){
            int result = mTts.setLanguage(spanish);
            isLoaded = true;

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("error", "Este lenguaje no está permitido");
            }
        }else{
            Log.e("error", "Fallo la inicializar");
        }
    };

    public void shutDown(){
        mTts.shutdown();
    }

    public void stop() { mTts.stop(); }

    //public void start() { mTts.}

    public void addQueue(String text){
        if(isLoaded){
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }else{
            Log.e("eror", "TTS not initialized");
        }
    }

    public void initQueue(String text){
        if(isLoaded){
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else{
            Log.e("error", "TTS not initialized");
        }
    }

    public boolean getIsLoaded() {
        return isLoaded;
    }

    public boolean getIsSpeaking(){
        return mTts.isSpeaking();
    }
}