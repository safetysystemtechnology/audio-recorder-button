package br.com.safety.audio_recorder;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AudioRecordButton extends LinearLayout {

    private static String DEFAULT_FILE_NAME_AUDIO = "/audio.ogg";
    private static String DEFAULT_SAVE_FOLDER_NAME = "/Music";

    private Drawable mImageVoice;
    private RelativeLayout mLayoutTimer;
    private Chronometer mChronometer;
    private ImageView imageView;
    private Context mContext;
    private AudioListener mAudioListener;
    private AudioRecording mAudioRecording;

    public AudioRecordButton(Context context) {
        super(context);
        setupLayout(context, null, -1, -1);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupLayout(context, attrs, -1, -1);
    }

    public AudioRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupLayout(context, attrs, defStyleAttr, -1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AudioRecordButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setupLayout(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeImageView();
                mLayoutTimer.setVisibility(VISIBLE);
                startRecord();
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                unRevealImageView();
                mLayoutTimer.setVisibility(INVISIBLE);
                stopRecord();
                break;
            default:
                return false;
        }
        return true;
    }

    private void startRecord() {
        if (mAudioListener != null) {
            AudioListener audioListener = new AudioListener() {
                @Override
                public void onStop(RecordingItem recordingItem) {
                    mAudioListener.onStop(recordingItem);
                }

                @Override
                public void onError(Exception e) {
                    mAudioListener.onError(e);
                }
            };

            this.mAudioRecording =
                    new AudioRecording(this.mContext)
                            .setNameFile(DEFAULT_FILE_NAME_AUDIO)
                            .setSaveFolder(DEFAULT_SAVE_FOLDER_NAME)
                            .start(audioListener);
        }
    }

    private void stopRecord() {
        if (mAudioListener != null) {
            this.mAudioRecording.stop();
        }
    }

    public void setOnAudioListener(AudioListener audioListener) {
        this.mAudioListener = audioListener;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setupLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        this.setOrientation(LinearLayout.VERTICAL);

        /**
         * layout to chronometer
         */
        mLayoutTimer = new RelativeLayout(context);
        mLayoutTimer.setVisibility(INVISIBLE);
        mLayoutTimer.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_event));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                30);

        layoutParams.gravity = Gravity.CENTER;
        addView(mLayoutTimer, layoutParams);

        /**
         * chronometer
         */
        this.mChronometer = new Chronometer(context);
        this.mChronometer.setGravity(Gravity.CENTER);
        this.mChronometer.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams layoutParamsChronometer = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsChronometer.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mLayoutTimer.addView(this.mChronometer, layoutParamsChronometer);

        /**
         * Image voice
         */
        this.imageView = new ImageView(context);
        this.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fileviewer));

        LayoutParams layoutParamImage = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        addView(this.imageView, layoutParamImage);
    }

    public void changeImageView() {
        AnimatorSet inAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.animation_up);

        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(400);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.CHANGING);
        }
        this.mLayoutTimer.setLayoutTransition(transition);

        /**
         * Animation fade in for image voice..
         */
//        Animation animFadeIn = AnimationUtils.loadAnimation(this.mContext, R.anim.fade_in);
//        animFadeIn.reset(); 
//        this.imageView.clearAnimation();
//        this.imageView.startAnimation(animFadeIn);

        this.mChronometer.setBase(SystemClock.elapsedRealtime());
        this.mChronometer.start();

        this.getLayoutParams().width = this.getWidth() + 30;
        this.getLayoutParams().height = this.getHeight() + 30;
        this.requestLayout();

        this.imageView.getLayoutParams().width = this.imageView.getWidth() + 30;
        this.imageView.getLayoutParams().height = this.imageView.getHeight() + 30;
        this.imageView.requestLayout();
    }

    public void unRevealImageView() {
        this.mChronometer.stop();

        this.getLayoutParams().width = this.getWidth() - 30;
        this.getLayoutParams().height = this.getHeight() - 30;
        this.requestLayout();

        this.imageView.getLayoutParams().width = this.imageView.getWidth() - 30;
        this.imageView.getLayoutParams().height = this.imageView.getHeight() - 30;
        this.imageView.requestLayout();
    }

}