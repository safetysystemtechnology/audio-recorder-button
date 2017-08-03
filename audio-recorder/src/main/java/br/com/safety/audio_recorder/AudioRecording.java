package br.com.safety.audio_recorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import java.io.IOException;

/**
 * @author netodevel
 */
public class AudioRecording {

    private String mFileName;
    private Context mContext;

    private MediaPlayer mMediaPlayer;
    private AudioListener audioListener;
    private MediaRecorder mRecorder;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;

    public AudioRecording(Context context) {
        this.mContext = context;
    }

    public AudioRecording() {
    }

    public AudioRecording setNameFile(String nameFile) {
        this.mFileName = nameFile;
        return this;
    }

    public AudioRecording start(AudioListener audioListener) {
        this.audioListener = audioListener;

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mContext.getCacheDir() + mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            this.audioListener.onError(e);
        }
        return this;
    }

    public void stop(Boolean cancel) {
        mRecorder.stop();
        mRecorder.release();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder = null;

        RecordingItem recordingItem = new RecordingItem();
        recordingItem.setFilePath(mContext.getCacheDir() + mFileName);
        recordingItem.setName(mFileName);
        recordingItem.setLength((int)mElapsedMillis);
        recordingItem.setTime(System.currentTimeMillis());

        if (cancel == false) {
            audioListener.onStop(recordingItem);
        } else {
            audioListener.onCancel();
        }
    }

    public void play(RecordingItem recordingItem) {
        try {
            this.mMediaPlayer = new MediaPlayer();
            this.mMediaPlayer.setDataSource(recordingItem.getFilePath());
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
