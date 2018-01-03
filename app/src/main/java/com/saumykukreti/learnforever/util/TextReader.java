package com.saumykukreti.learnforever.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.saumykukreti.learnforever.constants.Constants;

import java.util.Locale;

/**
 * Created by saumy on 12/16/2017.
 */

public class TextReader implements TextToSpeech.OnInitListener, LifecycleObserver {

    private final Context mContext;
    private TextToSpeech mTts;

    public TextReader(Context context, Lifecycle lifecycle) {
       mContext = context;
       lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(){
        mTts = new TextToSpeech(mContext, this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(){
        //Freeing memory
        stopReading();
        mTts.shutdown();
        mTts = null;
    }



    public void readAloud(String speech) {
//        String[] splitspeech = speech.split(Constants.PAUSE_FOR_TWO_HUNDERED_MILISECONDS);
//        for (int i = 0; i < splitspeech.length; i++) {
//            if (i == 0) { // Use for the first splited text to flush on audio stream
//                mTts.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
//            } else { // add the new test on previous then play the TTS
//                mTts.speak(splitspeech[i].toString().trim(), android.speech.tts.TextToSpeech.QUEUE_ADD, null);
//            }
//            mTts.playSilence(750, android.speech.tts.TextToSpeech.QUEUE_ADD, null);
//        }

        //If speech contains some breaks
        if(speech.contains("&%&")) {
            int index = 1;
            boolean speechStarted = false;
            while (index > 0) {
                index = speech.indexOf("&%&");
                if (index != -1) {
                    if (!speechStarted) {
                        speechStarted = true;
                        mTts.speak(speech.substring(0, index).trim(), TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        mTts.speak(speech.substring(0, index).trim(), android.speech.tts.TextToSpeech.QUEUE_ADD, null);
                    }

                    //Playing silence
                    int silenceDuration = Integer.parseInt(String.valueOf(speech.charAt(index + 3))) * 100;
                    mTts.playSilence(silenceDuration, android.speech.tts.TextToSpeech.QUEUE_ADD, null);

                    speech = speech.substring(index + Constants.PAUSE_FOR_TWO_HUNDERED_MILISECONDS.length(), speech.length());
                }
                else{
                    //Reading remaining part
                    mTts.speak(speech, android.speech.tts.TextToSpeech.QUEUE_ADD, null);
                }
            }
        }
        else{
            mTts.speak(speech.trim(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void addToReadingText(String speech){
        if (mTts.isSpeaking()){
            mTts.speak(speech, android.speech.tts.TextToSpeech.QUEUE_ADD,null);
        }
        else{
            mTts.speak(speech, android.speech.tts.TextToSpeech.QUEUE_ADD,null);
        }
    }

    public void addPause(int milliSecond){
        if(mTts.isSpeaking()){
            mTts.playSilence(milliSecond, android.speech.tts.TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void stopReading(){
        if(mTts!=null && mTts.isSpeaking()){
            mTts.stop();
        }
    }

    /**
     * TTS Init
     *
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            //Setting language
            int result = mTts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }


}
