package com.sms13033.sms13033;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class MyTTS {
    private TextToSpeech tts;

    private TextToSpeech.OnInitListener initListener=
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS) {
                        int result= tts.setLanguage(Locale.US);

                        if(result== TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED ) {

                            Log.e("TTS","Language not supported");

                        }
                    }else{

                        Log.e("TTS","Initialization Failed");
                    }
                }
            };


    public MyTTS(Context context) {
        tts = new TextToSpeech(context,initListener);
    }



    public void speak(String message){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_ADD, null);
        }

    }

}
