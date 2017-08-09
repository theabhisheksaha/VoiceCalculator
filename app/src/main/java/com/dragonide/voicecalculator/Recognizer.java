package com.dragonide.voicecalculator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;


public class Recognizer extends AppCompatActivity implements RecognitionListener {


    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 1;

    private static final int RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS = 1;
    private static final int RECORDER_ENCODING_BIT = 16;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MAX_DECIBELS = 120;
    // CustomEditText customEditText;
    private AudioRecord audioRecord;

    private GLSurfaceView glSurfaceView;
    String resultSpeech;
    private Thread recordingThread;
    private byte[] buffer;
    ChatView chatView;
    SpeechRecognizer speechRecognizer;
    //  TextView resultText;
    CalculateResult calculateResult = new CalculateResult();
    TextToSpeech t1;
    private static final long REFRESH_INTERVAL_MS = 30;
    private boolean keepGoing = true;
    private DrawView view;
    //   private MediaRecorder mRecorder = null;
    MyDatabase myDatabase;
    ImageView imageView;
    LinearLayout layout;
    private ArrayList<Poses> posesArrayList;
    float tr = 0;

    /**
     * this listener helps us to synchronise real time
     * and actual drawing
     */


    private AudioRecord.OnRecordPositionUpdateListener recordPositionUpdateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioRecord recorder) {
            //empty for now
        }


        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                    && audioRecord.read(buffer, 0, buffer.length) != -1) {

            }
        }
    };


    private void runGameLoop() {
        // update the game repeatedly
        while (keepGoing) {
            long durationMs = redraw();
            try {
                Thread.sleep(Math.max(0, REFRESH_INTERVAL_MS - durationMs));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long redraw() {

        long t = System.currentTimeMillis();

        // At this point perform changes to the model that the component will
        // redraw

        display_game();


        // return time taken to do redraw in ms
        return System.currentTimeMillis() - t;
    }


    private void display_game() {


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.phase += view.phaseShift;

                Log.d("MaxAmplitude", tr + "");
//mRecorder.getMaxAmplitude()


                view.amplitude = (view.amplitude + Math.max(tr / 2590.5336f, 0.01f)) / 2;

                view.invalidate();

            }
        });

//        Log.v("Game", "Display Game" + view.phase);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizer);
        layout = (LinearLayout) findViewById(R.id.root);
        chatView = (ChatView) findViewById(R.id.chat_view);

        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                // perform actual message sending

                return true;
            }
        });


        view = new DrawView(this);
        view.invalidate();
        layout.addView(view);

       /* mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRecorder.setOutputFile("/dev/null");

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("mediaRecorder", "prepare() failed");
        }

        try {
            mRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
*/

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                runGameLoop();
            }
        });

        thread.start();


        //glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface);
        // customEditText = (CustomEditText) findViewById(R.id.editText);
        // customEditText.setDrawableClickListener(this);
        //   customEditText.addTextChangedListener(this);
        //   resultText = (TextView) findViewById(R.id.fResult);
        //  mHorizon = new Horizon(glSurfaceView, getResources().getColor(R.color.background),
        //         RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);
        //mHorizon.setMaxVolumeDb(MAX_DECIBELS);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });


        imageView = (ImageView) findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissionsAndStart();

            }
        });


    }


    @Override
    public void onReadyForSpeech(Bundle bundle) {
        imageView.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {
        v = Math.abs(v);
        double a = Math.pow(20, v / 14.140);

        tr = (float) a * 100;
        Log.d("RMS", v + " = " + a);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        tr = 0;
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        for (String key : bundle.keySet()) {
            Log.d("myApplication partial", key + " : " + bundle.get(key));

        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onError(int i) {
        imageView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.INVISIBLE);
        tr = 0;

        Log.d("Error Recording", "" + i);
        //recreate();
    }

    @Override
    public void onResults(Bundle bundle) {
        speechRecognizer.stopListening();

        imageView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.INVISIBLE);
        tr = 0;
        ArrayList<String> arraylist = bundle.getStringArrayList("results_recognition");
        for (String key : arraylist) {
            Log.d("myApplication", key);
        }


        resultSpeech = arraylist.get(0).toLowerCase();
/*resultSpeech = resultSpeech.replaceAll("how much is ", "");
        resultSpeech = resultSpeech.replaceAll("what is ", "");
        resultSpeech = resultSpeech.replaceAll("calculate ", "");*/

        myDatabase = new MyDatabase(Recognizer.this);

        posesArrayList = myDatabase.getPoses();

        Log.e("poses list", posesArrayList.size() + "");

        for (int i = 0; i < posesArrayList.size(); i++) {
            Log.d("text", posesArrayList.get(i).s_text);

            resultSpeech = resultSpeech.replaceAll(posesArrayList.get(i).s_text, posesArrayList.get(i).s_symbol);

        }


        resultSpeech = resultSpeech.replaceAll("X", "*");
        if (resultSpeech.contains("fuck") || resultSpeech.contains("f***") || resultSpeech.contains("bitch") || resultSpeech.contains("b****")
                || resultSpeech.contains("shit") || resultSpeech.contains("s***")) {

            ChatMessage c = new ChatMessage(resultSpeech, System.currentTimeMillis(), ChatMessage.Type.SENT);
            chatView.addMessage(c);

            speak("Don't ever try to talk me like that");
            ChatMessage c1 = new ChatMessage("Please be polite", System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
            chatView.addMessage(c1);



        }else{
            searchOnGoogle(resultSpeech);
            ChatMessage c = new ChatMessage(resultSpeech, System.currentTimeMillis(), ChatMessage.Type.SENT);
            chatView.addMessage(c);
        }







    }

    public void startVoiceRecorder() {
        int REQUEST_CODE = 1;

        try {
            Log.d("VoiceREcorder", "started");

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

                speechRecognizer.setRecognitionListener(this);
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
                speechRecognizer.startListening(speechIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("VoiceREcorder", "startedListening");







   /*     Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "New Voice Model");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, REQUEST_CODE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, 11);*/
        } catch (Exception e) {
           /* Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("https://market.android.com/details?id=APP_PACKAGE_NAME"));
            startActivity(browserIntent);*/
            Toast.makeText(this, "You don't have any voice recognizer app installed, Download from Google Play store", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //     checkPermissionsAndStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        if (speechRecognizer!=null) {
            speechRecognizer.stopListening();
        }
        super.onPause();

        //    glSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }

    }

    public void stopRecording() {
        if (audioRecord != null) {
            audioRecord.release();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }

        // stopRecording();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_RECORD_AUDIO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionsAndStart();
                } else {
                    finish();
                }
        }

    }

    private void checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_RECORD_AUDIO);
        } else {
            startVoiceRecorder();
          /*  initRecorder();
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                PrefUtils.setBoolean(this, "isInstialized", true);
                startRecording();

            }*/
        }
    }




    public void searchOnGoogle(String inp) {
        if (!calculateResult.isCancelled()) {
            calculateResult.cancel(true);

        }
        calculateResult = null;
        calculateResult = new CalculateResult();
        //   calculateResult.si = this;
        calculateResult.execute(inp);

    }

    public void speak(String s){
        if (Build.VERSION.SDK_INT>=21) {

            t1.speak(s, TextToSpeech.QUEUE_FLUSH, null, "12");
        }else{
            t1.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    private class CalculateResult extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("none")) {
                //resultText.setText("");
                speak("I don't think that is a Mathematical expression, Try Speaking again");
                ChatMessage c = new ChatMessage("I don't think that is a Mathematical expression, Try Speaking again", System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                chatView.addMessage(c);
                //   Toast.makeText(Recognizer.this, "Not a mathematical expression", Toast.LENGTH_SHORT).show();
            } else if (s.equals("conn")) {
                // resultText.setText("");
                speak("Sometimes I need a stable connection");
                ChatMessage c = new ChatMessage("I don't have a stable connection", System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                chatView.addMessage(c);
                // Toast.makeText(Recognizer.this, "Network Connection failure", Toast.LENGTH_SHORT).show();
            } else {
                ChatMessage c = new ChatMessage(s, System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                chatView.addMessage(c);
                speak(s);
               /* if (Build.VERSION.SDK_INT>=21) {
                    tts.speak(s, TextToSpeech.QUEUE_FLUSH, new Bundle(), "12");
                } else {
                    tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                }
*/


                // resultText.setText(s);

            }
        }



        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... strings) {
            //  strings[0] = strings[0].replaceAll(" ","");
            String ssearch;
            String fout;

/*

            InfixPostfixEvaluator eval = new    InfixPostfixEvaluator();
            String expression = strings[0];
            String postfix = eval.convert2Postfix(expression);

            fout=  " "+ eval.evaluatePostfix(postfix);

*/

            Infix fix=new Infix();
            String expression=strings[0];
           // = ""+ fix.infix(expression);

            try {
                double a = fix.infix(expression);
                if(a==(Math.round(a))){
                    //Integer
                    fout = ""+ (int) a;

                }else{
                    fout= ""+ a;
                    //Float
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    ssearch = "http://www.google.com/m/search?q="
                            + URLEncoder.encode(strings[0],
                            "UTF-8");
                } catch (UnsupportedEncodingException u) {
                    u.printStackTrace();
                    // Toast.makeText(this, "Network Issue", Toast.LENGTH_SHORT).show();
                    ssearch = "http://www.google.com/m/search?q=" + strings[0];
                }

                try {
                    Document d = Jsoup.connect(ssearch).get();
                    Log.d("search", d.html());
                    Element box = d.select("span#cwos").first();
                    if (box != null)
                        fout = box.text();
                    else {
                        fout = "none";
                        Element textBox = d.select("div.vk_ans").first();
                        if (textBox != null)
                            fout = textBox.text();
                        else {
                            fout = "none";
                        }


                    }

                } catch (IOException w) {
                    fout = "conn";
                    w.printStackTrace();
                }


            }



            //fout


           /* try {
                ssearch = "http://www.google.com/m/search?q="
                        + URLEncoder.encode(strings[0],
                        "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                // Toast.makeText(this, "Network Issue", Toast.LENGTH_SHORT).show();
                ssearch = "http://www.google.com/m/search?q=" + strings[0];
            }

            try {
                Document d = Jsoup.connect(ssearch).get();
                Log.d("search", d.html());
                Element box = d.select("span#cwos").first();
                if (box != null)
                    fout = box.text();
                else {
                    fout = "none";
                    Element textBox = d.select("div.vk_ans").first();
                    if (textBox != null)
                        fout = textBox.text();
                    else {
                        fout = "none";
                    }


                }

            } catch (IOException e) {
                fout = "conn";
                e.printStackTrace();
            }*/

            return fout;
        }
    }
}
