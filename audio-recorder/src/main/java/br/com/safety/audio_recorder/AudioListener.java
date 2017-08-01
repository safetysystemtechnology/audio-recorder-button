package br.com.safety.audio_recorder;

/**
 * @author netodevel
 */
public interface AudioListener {

    void onStop(RecordingItem recordingItem);

    void onError(Exception e);
}
