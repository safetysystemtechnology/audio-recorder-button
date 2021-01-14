package br.com.safety.audio_recorder_button;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import br.com.safety.audio_recorder.AudioListener;
import br.com.safety.audio_recorder.AudioRecordButton;
import br.com.safety.audio_recorder.AudioRecording;
import br.com.safety.audio_recorder.RecordingItem;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private String session = "";
    private AudioRecordButton mAudioRecordButton;
    private AudioRecording audioRecording;
    private Button stopBtn;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioRecording = new AudioRecording(getBaseContext());

        initView();

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE},0);

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 0);

        this.mAudioRecordButton.setOnAudioListener(new AudioListener() {
            @Override
            public void onStop(final RecordingItem recordingItem) {
                Toast.makeText(getBaseContext(), "Audio..", Toast.LENGTH_SHORT).show();
                audioRecording.play(recordingItem);

                stopBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioRecording.stopAudio(recordingItem);
                    }
                });

                session = String.valueOf(audioRecording.sessionId);
                Toast.makeText(MainActivity.this, session, Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.d("MainActivity", "Error: " + e.getMessage());
            }
        });
    }

    private void initView() {
        this.mAudioRecordButton = (AudioRecordButton) findViewById(R.id.audio_record_button);
        this.stopBtn = (Button) findViewById(R.id.stop);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
