package com.dragonide.voicecalculator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Ankit on 8/6/2017.
 */

public class WidgetProvider extends AppWidgetProvider implements RecognitionListener {

    SpeechRecognizer speechRecognizer;
    TextView resultText;
    RemoteViews views;


    MyDatabase myDatabase;
    private ArrayList<Poses> posesArrayList;
Context context;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
this.context=context;
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity

            SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

            speechRecognizer.setRecognitionListener(this);
            Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
            speechRecognizer.startListening(speechIntent);


            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, speechIntent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
             views = new RemoteViews(context.getPackageName(), R.layout.initial_widget);
            views.setOnClickPendingIntent(R.id.fwidgetcall, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> arraylist = bundle.getStringArrayList("results_recognition");
        for (String key : arraylist) {
            Log.d("myApplication", key);
        }





      String  resultSpeech = arraylist.get(0);

        myDatabase=new MyDatabase(context);

        posesArrayList=myDatabase.getPoses();

        Log.e("poses list",posesArrayList.size()+"");

        for(int i=0;i<posesArrayList.size();i++){
            Log.d("text", posesArrayList.get(i).s_text);
            resultSpeech = resultSpeech.replaceAll(posesArrayList.get(i).s_text,posesArrayList.get(i).s_symbol );

        }









        resultSpeech = resultSpeech.replaceAll("X", "*");
        views.setTextViewText(R.id.fWidgetResult, resultSpeech);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
