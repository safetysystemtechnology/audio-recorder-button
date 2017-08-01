package br.com.safety.audio_recorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

import static br.com.safety.audio_recorder.Constants.FORMAT_OUTPUT_DEFAULT;

/**
 * @author netodevel
 */
public class AudioRecording {

    public static final String BASE_FOLDER = "/SoundRecorder";

    private String mSaveFolder;
    private String mFileName;
    private Context mContext;

    private MediaPlayer mMediaPlayer;
    private AudioListener audioListener;
    private String mFilePath;
    private MediaRecorder mRecorder;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;

    public AudioRecording(Context context) {
        this.mContext = context;
    }

    public AudioRecording() {
    }

    public AudioRecording setSaveFolder(String nameFolder) {
        this.mSaveFolder = nameFolder;
        return this;
    }

    public AudioRecording setNameFile(String nameFile) {
        this.mFileName = nameFile;
        return this;
    }

    public AudioRecording start(AudioListener audioListener) {
        this.audioListener = audioListener;

        createDir();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + mSaveFolder + mFileName);
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

    public void stop() {
        mRecorder.stop();
        mRecorder.release();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder = null;

        RecordingItem recordingItem = new RecordingItem();
        recordingItem.setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + mSaveFolder + mFileName);
        recordingItem.setName(mFileName);
        recordingItem.setLength((int)mElapsedMillis);
        recordingItem.setTime(System.currentTimeMillis());

        audioListener.onStop(recordingItem);
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

    private void createAudioFile() {
        mFilePath = this.mSaveFolder + this.mFileName + FORMAT_OUTPUT_DEFAULT;
        new File(mFilePath);
    }

    private void createDir() {
        File folder = new File(Environment.getExternalStorageDirectory()
                + this.mSaveFolder != null ? this.mSaveFolder : BASE_FOLDER);

        if (!folder.exists()) {
            folder.mkdir();
        }
    }

}
