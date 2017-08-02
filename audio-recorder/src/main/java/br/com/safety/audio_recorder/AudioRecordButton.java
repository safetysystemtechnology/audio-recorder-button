package br.com.safety.audio_recorder;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AudioRecordButton extends RelativeLayout {

    private static String DEFAULT_FILE_NAME_AUDIO = "/audio.ogg";
    private static String DEFAULT_SAVE_FOLDER_NAME = "/Music";

    private Context mContext;

    private RelativeLayout mLayoutTimer;
    private RelativeLayout mLayoutVoice;

    private Chronometer mChronometer;

    private ImageView mImageView;
    private ImageButton mImageButton;

    private AudioListener mAudioListener;
    private AudioRecording mAudioRecording;

    private float initialX;
    private float initialXImageButton;

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
                mImageButton.setVisibility(VISIBLE);
                startRecord();
                return true;
            case MotionEvent.ACTION_MOVE:

                if (initialX == 0) {
                    initialX = this.mImageView.getX();
                }

                this.initialXImageButton = this.mImageView.getLeft();

                if (event.getX() < initialX + this.mImageView.getWidth() / 2) {
                    this.mImageView.setX(event.getX() - mImageView.getWidth() / 2);
                    this.mImageButton.setAlpha(0.9f);
                }

                if (this.mImageView.getX() < getX()) {
                    Log.d("x", "imageView" + this.mImageView.getX());
                    Log.d("collide?", "collide?");
                }

                break;
            case MotionEvent.ACTION_UP:
                moveImageToBack();
                unRevealImageView();
                mLayoutTimer.setVisibility(INVISIBLE);
                mImageButton.setVisibility(INVISIBLE);
                stopRecord();
                break;
            default:
                return false;
        }
        return true;
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }


    private void moveImageToBack() {
        this.mImageButton.setAlpha(0.5f);
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(this.mImageView.getX(), 0);

        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) animation.getAnimatedValue();
                mImageView.setX(x);
            }
        });

        positionAnimator.setDuration(200);
        positionAnimator.start();
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
        /**
         * layout to chronometer
         */
        mLayoutTimer = new RelativeLayout(context);
        mLayoutTimer.setId(9 + 1);
        mLayoutTimer.setVisibility(INVISIBLE);
        mLayoutTimer.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_event));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                30);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        addView(mLayoutTimer, layoutParams);

        /**
         * chronometer
         */
        this.mChronometer = new Chronometer(context);
        this.mChronometer.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams layoutParamsChronometer = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsChronometer.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mLayoutTimer.addView(this.mChronometer, layoutParamsChronometer);

        /**
         * Layout to voice and cancel audio
         */
        mLayoutVoice = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutVoiceParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        layoutVoiceParams.addRule(RelativeLayout.BELOW, 9 + 1);

        addView(this.mLayoutVoice, layoutVoiceParams);

        /**
         * Image voice
         */
        this.mImageView = new ImageView(context);
        this.mImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fileviewer));

        LayoutParams layoutParamImage = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        layoutParamImage.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        this.mLayoutVoice.addView(this.mImageView, layoutParamImage);

        /**
         * Image Button
         */
        this.mImageButton = new ImageButton(context);
        this.mImageButton.setVisibility(INVISIBLE);
        this.mImageButton.setAlpha(0.5f);
        this.mImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_circle));

        RelativeLayout.LayoutParams layoutParamImageButton = new RelativeLayout.LayoutParams(
                25,
                25
        );
        layoutParamImageButton.setMargins(0, 0, 4, 0);
        layoutParamImageButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        this.mLayoutVoice.addView(this.mImageButton, layoutParamImageButton);

        this.initialXImageButton = this.mImageButton.getX();
    }

    public void changeImageView() {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(600);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.CHANGING);
        }
        this.mLayoutTimer.setLayoutTransition(transition);
        this.setLayoutTransition(transition);

        this.mChronometer.setBase(SystemClock.elapsedRealtime());
        this.mChronometer.start();

        this.getLayoutParams().width = this.getWidth() + 30;
        this.getLayoutParams().height = this.getHeight() + 30;
        this.requestLayout();

        this.mImageView.getLayoutParams().width = this.mImageView.getWidth() + 30;
        this.mImageView.getLayoutParams().height = this.mImageView.getHeight() + 30;
        this.mImageView.requestLayout();
    }

    public void unRevealImageView() {
        this.mChronometer.stop();

        this.getLayoutParams().width = this.getWidth() - 30;
        this.getLayoutParams().height = this.getHeight() - 30;
        this.requestLayout();

        this.mImageView.getLayoutParams().width = this.mImageView.getWidth() - 30;
        this.mImageView.getLayoutParams().height = this.mImageView.getHeight() - 30;
        this.mImageView.requestLayout();
    }

}