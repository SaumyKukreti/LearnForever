package com.saumykukreti.learnforever.util;

import android.speech.tts.TextToSpeech;

import com.saumykukreti.learnforever.constants.Constants;

/**
 * Created by saumy on 12/16/2017.
 */

public class TextReader {

    private TextToSpeech mTts;

    public TextReader(TextToSpeech tts) {
        mTts = tts;
    }

    public void readAloud(String speech) {
        String[] splitspeech = speech.split(Constants.PAUSE_LINE_SEPARATOR);
        for (int i = 0; i < splitspeech.length; i++) {
            if (i == 0) { // Use for the first splited text to flush on audio stream
                mTts.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
            } else { // add the new test on previous then play the TTS
                mTts.speak(splitspeech[i].toString().trim(), android.speech.tts.TextToSpeech.QUEUE_ADD,null);
            }
            mTts.playSilence(750, android.speech.tts.TextToSpeech.QUEUE_ADD, null);
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
}
