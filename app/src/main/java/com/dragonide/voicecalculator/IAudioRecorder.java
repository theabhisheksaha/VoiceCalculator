package com.dragonide.voicecalculator;

/**
 * Interface for audio recorder
 */
interface IAudioRecorder {
    void startRecord();
    void finishRecord();
    boolean isRecording();
}
