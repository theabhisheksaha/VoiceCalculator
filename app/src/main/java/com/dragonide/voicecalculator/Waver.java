package com.dragonide.voicecalculator;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.io.IOException;


public class Waver extends ActionBarActivity {
    private static final String LOG_TAG = "AudioRecordTest";

    private MediaRecorder mRecorder = null;
    private static final long REFRESH_INTERVAL_MS = 30;
    private boolean keepGoing = true;
    private DrawView view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waver);

        LinearLayout layout = (LinearLayout) findViewById(R.id.root);
        view = new DrawView(this);
        view.invalidate();
        layout.addView(view);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(AudioEncoder.DEFAULT);
        mRecorder.setOutputFile("/dev/null");

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                runGameLoop();
            }
        });

        thread.start();

    }


    private void runGameLoop() {
        // update the game repeatedly
        while (keepGoing) {
            long durationMs = redraw();
            try {
                Thread.sleep(Math.max(0, REFRESH_INTERVAL_MS - durationMs));
            } catch (InterruptedException e) {
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
                view.amplitude = (view.amplitude + Math.max(mRecorder.getMaxAmplitude() / 2590.5336f, 0.01f)) / 2;

                view.invalidate();

            }
        });

//        Log.v("Game", "Display Game" + view.phase);
    }





}
