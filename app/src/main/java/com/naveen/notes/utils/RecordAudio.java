package com.naveen.notes.utils;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class RecordAudio {
    private final String LOGTAG = getClass().getSimpleName().toString();
    MediaRecorder mRecorder = null;
    private boolean isRecording = false;

    public void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
            Log.i(LOGTAG, "Recorder Released");
        }
        isRecording = false;
    }

    public void startRecording(String mFileName) {
        if (mRecorder == null){
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        if (!isRecording) {
            try {
                mRecorder.prepare();
                mRecorder.start();
                Log.i(LOGTAG, "Recording Started");
                isRecording = true;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOGTAG, "prepare() failed");
            }
        }else {
            isRecording = false;
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void stopRecording() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                Log.i(LOGTAG, "Recording Stopped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mRecorder != null)
                mRecorder.reset();
            releaseMediaRecorder();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
