package com.pb.criconetnewdesign.CommonUI;

import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.pb.criconetnewdesign.AGApplication;
import com.pb.criconetnewdesign.CustomeCamera.CustomeCameraActivity;
import com.pb.criconetnewdesign.R;
import com.pb.criconetnewdesign.databinding.ActivityAddTrainingTipsBinding;
import com.pb.criconetnewdesign.databinding.ToolbarInnerpageBinding;
import com.pb.criconetnewdesign.util.BookingHistoryDropDown;
import com.pb.criconetnewdesign.util.DataModel;
import com.pb.criconetnewdesign.util.Toaster;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import jaygoo.widget.wlv.WaveLineView;

public class AddTrainingTipsActivity extends AppCompatActivity {

    ActivityAddTrainingTipsBinding activityAddTrainingTipsBinding;

    ArrayList<DataModel> option_category = new ArrayList<>();
    ArrayList<DataModel> option_privacy = new ArrayList<>();

    public static Uri image_uri = null;
    Context mContext;
    Activity mActivity;

    MediaController mediaController;

    //todo audio recording

    public static final int MAX_DURATION = 100000;
    private static String mFileName="";
    private MediaPlayer mPlayer;
    private MediaRecorder mRecorder;
    long millisUntilFinishedd;
    File output;
    Timer timer = null;
    CountDownTimer countDownTimer = null;
    Chronometer tv_timer;
    private TextView statusTV;
    private TextView tv_countDown;
    FrameLayout fl_cancel;
    FrameLayout fl_save;
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_CAMERA_CODE = 2;

    int stoppedMilliseconds = 0;

    MaterialCardView mcv_startRecording;
    MaterialCardView mcv_stopRecording;
    MaterialCardView mcv_playRecording;
    MaterialCardView mcv_stopPlaying;
    ImageView iv_startRecording;
    ImageView ivStopRecording;
    ImageView ivPlayRecording;
    ImageView ivStopPlaying;
    TextView tvStartRecording;
    TextView tvStopRecording;
    TextView tvPlayRecording;
    TextView tvStopPlaying;

    private AudioManager audioManager;
    private WaveLineView waveLineView;
    private ImageView ivPlay;
    private File songFile;


    //todo get record video using camera
    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();
                    activityAddTrainingTipsBinding.flVideo.setVisibility(View.VISIBLE);
                    activityAddTrainingTipsBinding.videoView.setVideoURI(intent.getData());
                    activityAddTrainingTipsBinding.videoView.start();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddTrainingTipsBinding = ActivityAddTrainingTipsBinding.inflate(getLayoutInflater());
        setContentView(activityAddTrainingTipsBinding.getRoot());

        mContext = this;
        mActivity = this;

        ToolbarInnerpageBinding toolbarInnerpageBinding = activityAddTrainingTipsBinding.toolbar;
        toolbarInnerpageBinding.toolbartext.setText(R.string.add_training_tips);
        toolbarInnerpageBinding.imgBack.setOnClickListener(v -> {
            finish();
        });


        initView();
    }

    private void initView() {

        option_category.add(new DataModel("Batting"));
        option_category.add(new DataModel("Bowling"));
        option_category.add(new DataModel("Fielding"));
        option_category.add(new DataModel("Wicket keeping"));
        option_category.add(new DataModel("Mental"));
        option_category.add(new DataModel("Tactical"));
        option_category.add(new DataModel("Physical"));
        option_category.add(new DataModel("Health and Fitness"));
        option_category.add(new DataModel("Spots wellness"));

        activityAddTrainingTipsBinding.dropTipsCategory.setOptionList(option_category);
        activityAddTrainingTipsBinding.dropTipsCategory.setClickListener(new BookingHistoryDropDown.onClickInterface() {
            @Override
            public void onClickAction() {
            }

            @Override
            public void onClickDone(String name) {


            }


            @Override
            public void onDismiss() {
            }
        });

        option_privacy.add(new DataModel("Batting"));
        option_privacy.add(new DataModel("Bowling"));
        option_privacy.add(new DataModel("Fielding"));
        option_privacy.add(new DataModel("Wicket keeping"));
        option_privacy.add(new DataModel("Mental"));

        activityAddTrainingTipsBinding.dropPrivacy.setOptionList(option_category);
        activityAddTrainingTipsBinding.dropPrivacy.setClickListener(new BookingHistoryDropDown.onClickInterface() {
            @Override
            public void onClickAction() {
            }

            @Override
            public void onClickDone(String name) {


            }


            @Override
            public void onDismiss() {
            }
        });


        //todo tips title component
        activityAddTrainingTipsBinding.editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                activityAddTrainingTipsBinding.filledTextFieldTitle.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    activityAddTrainingTipsBinding.filledTextFieldTitle.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                activityAddTrainingTipsBinding.filledTextFieldTitle.setErrorEnabled(false);
            }
        });

        //todo firstName component
        activityAddTrainingTipsBinding.EditTextWhatDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                activityAddTrainingTipsBinding.filledInputLayoutDes.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    activityAddTrainingTipsBinding.filledInputLayoutDes.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                activityAddTrainingTipsBinding.filledInputLayoutDes.setErrorEnabled(false);
            }
        });

        activityAddTrainingTipsBinding.tvVideo.setOnClickListener(v -> {
         checkCameraPermissions();
        });

        activityAddTrainingTipsBinding.tvPicImage.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CustomeCameraActivity.class)
                    .putExtra("FROM", "AddTrainingTipsActivity"));
        });

        activityAddTrainingTipsBinding.ivCrossImage.setOnClickListener(v -> {
            image_uri = null;
            activityAddTrainingTipsBinding.flImage.setVisibility(View.GONE);
        });

        activityAddTrainingTipsBinding.ivCrossVideo.setOnClickListener(v -> {
            activityAddTrainingTipsBinding.flVideo.setVisibility(View.GONE);

        });

        activityAddTrainingTipsBinding.tvAudio.setOnClickListener(v -> {
            showBottomStartAudioRecordingDialog();
        });


        activityAddTrainingTipsBinding.layPlayStop.setOnClickListener(v -> {
            if(mPlayer==null){
                startFinalPlaying();
            }else{
                stopFinalPlaying();
            }
        });

        activityAddTrainingTipsBinding.ivCrossAudio.setOnClickListener(v -> {
            mFileName = "";
            activityAddTrainingTipsBinding.flAudio.setVisibility(View.GONE);
        });

    }


    public void launchVideoCamera(){
        Intent chooserIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityIntent.launch(chooserIntent);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO},
                        REQUEST_PERMISSION_CODE);
            } else {
                startRecording();
                // Permissions are already granted
                // Continue with your audio recording and storage logic
            }
        } else {
            startRecording();
            // Permissions are not needed for devices below Android M
            // Continue with your audio recording and storage logic
        }
    }

    private void checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_AUDIO},
                        REQUEST_CAMERA_CODE);
            } else {
                launchVideoCamera();
                // Permissions are already granted
                // Continue with your audio recording and storage logic
            }
        } else {
            launchVideoCamera();
            // Permissions are not needed for devices below Android M
            // Continue with your audio recording and storage logic
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            // Check if all permissions are granted
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                startRecording();
                // Permissions are granted, continue with your audio recording and storage logic
            } else {
                Toaster.customToast("Permission Denied");
                // Permissions are not granted, handle accordingly (e.g., show a message or exit the app)
            }
        } else if(requestCode == REQUEST_CAMERA_CODE){
            boolean allPermissionsGrantedd = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGrantedd = false;
                    break;
                }
            }

            if (allPermissionsGrantedd) {
                launchVideoCamera();
                // Permissions are granted, continue with your audio recording and storage logic
            } else {
                Toaster.customToast("Permission Denied");
                // Permissions are not granted, handle accordingly (e.g., show a message or exit the app)
            }
        }
    }


    private void showBottomStartAudioRecordingDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_start_recording);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        statusTV = bottomSheetDialog.findViewById(R.id.idTVstatus);
        tv_countDown = bottomSheetDialog.findViewById(R.id.tv_countDown);
        tv_timer = bottomSheetDialog.findViewById(R.id.tv_timer);
        fl_save = bottomSheetDialog.findViewById(R.id.fl_save);
        fl_cancel = bottomSheetDialog.findViewById(R.id.fl_cancel);

        mcv_startRecording = bottomSheetDialog.findViewById(R.id.mcv_startRecording);
        mcv_stopRecording = bottomSheetDialog.findViewById(R.id.mcv_stopRecording);
        mcv_playRecording = bottomSheetDialog.findViewById(R.id.mcv_playRecording);
        mcv_stopPlaying = bottomSheetDialog.findViewById(R.id.mcv_stopPlaying);

        iv_startRecording = bottomSheetDialog.findViewById(R.id.iv_startRecording);
        ivStopRecording = bottomSheetDialog.findViewById(R.id.ivStopRecording);
        ivPlayRecording = bottomSheetDialog.findViewById(R.id.ivPlayRecording);
        ivStopPlaying = bottomSheetDialog.findViewById(R.id.ivStopPlaying);

        tvStartRecording = bottomSheetDialog.findViewById(R.id.tvStartRecording);
        tvStopRecording = bottomSheetDialog.findViewById(R.id.tvStopRecording);
        tvPlayRecording = bottomSheetDialog.findViewById(R.id.tvPlayRecording);
        tvStopPlaying = bottomSheetDialog.findViewById(R.id.tvStopPlaying);


        mcv_startRecording.setOnClickListener(v -> {
            mcv_startRecording.setEnabled(true);
            mcv_stopRecording.setEnabled(true);
            iv_startRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.lemon_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStartRecording.setTextColor(mContext.getColor(R.color.lemon_color));
                tvStartRecording.setTextSize(20);
            }

            ivStopRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvStopRecording.setTextSize(14);
            }

            ivPlayRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvPlayRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvPlayRecording.setTextSize(14);
            }
            ivStopPlaying.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopPlaying.setTextColor(mContext.getColor(R.color.darkGray));
                tvStopPlaying.setTextSize(14);
            }

            mcv_stopRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_startRecording.setBackgroundColor(mContext.getResources().getColor(R.color.app_green));
            mcv_playRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_stopPlaying.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            statusTV.setText("Recording Started");
            if (tv_countDown.getVisibility() == View.VISIBLE) {
                tv_countDown.setVisibility(View.GONE);
            }
            tv_timer.setVisibility(View.VISIBLE);
            tv_timer.setBase(SystemClock.elapsedRealtime());
            tv_timer.start();
            checkAndRequestPermissions();
        });
        mcv_stopRecording.setOnClickListener(v -> {
            mcv_playRecording.setEnabled(true);
            mcv_stopPlaying.setEnabled(true);
            mcv_stopRecording.setBackgroundColor(mContext.getResources().getColor(R.color.app_green));
            mcv_startRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_playRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_stopPlaying.setBackgroundColor(mContext.getResources().getColor(R.color.white));

            iv_startRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStartRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvStartRecording.setTextSize(14);
            }

            ivStopRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.lemon_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopRecording.setTextColor(mContext.getColor(R.color.lemon_color));
                tvStopRecording.setTextSize(20);
            }

            ivPlayRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvPlayRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvPlayRecording.setTextSize(14);
            }
            ivStopPlaying.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopPlaying.setTextColor(mContext.getColor(R.color.darkGray));
                tvStopPlaying.setTextSize(14);
            }
            pauseRecording();
        });
        mcv_playRecording.setOnClickListener(v -> {
            mcv_stopRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_stopRecording.setEnabled(false);
            mcv_startRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_startRecording.setEnabled(false);
            mcv_playRecording.setBackgroundColor(mContext.getResources().getColor(R.color.app_green));
            mcv_stopPlaying.setBackgroundColor(mContext.getResources().getColor(R.color.white));

            iv_startRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStartRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvStartRecording.setTextSize(14);
            }

            ivStopRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvStopRecording.setTextSize(14);
            }

            ivPlayRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.lemon_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvPlayRecording.setTextColor(mContext.getColor(R.color.lemon_color));
                tvPlayRecording.setTextSize(20);
            }
            ivStopPlaying.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopPlaying.setTextColor(mContext.getColor(R.color.darkGray));
                tvStopPlaying.setTextSize(14);
            }
            statusTV.setText("Recording Started Playing");
            playAudio();
        });
        mcv_stopPlaying.setOnClickListener(v -> {
            mcv_startRecording.setEnabled(true);
            mcv_stopRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_startRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_playRecording.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mcv_stopPlaying.setBackgroundColor(mContext.getResources().getColor(R.color.app_green));

            iv_startRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStartRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvStartRecording.setTextSize(14);
            }

            ivStopRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvStopRecording.setTextSize(14);
            }

            ivPlayRecording.setColorFilter(ContextCompat.getColor(mContext, R.color.darkGray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvPlayRecording.setTextColor(mContext.getColor(R.color.darkGray));
                tvPlayRecording.setTextSize(14);
            }

            ivStopPlaying.setColorFilter(ContextCompat.getColor(mContext, R.color.lemon_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvStopPlaying.setTextColor(mContext.getColor(R.color.lemon_color));
                tvStopPlaying.setTextSize(20);
            }

            statusTV.setText("Recording Play Stopped");
            pausePlaying();
        });

        fl_save.setOnClickListener(v -> {

            if(mFileName.isEmpty()){
                Toaster.customToast("First record your audio then save!");
            }else{
                activityAddTrainingTipsBinding.tvFileNameAudio.setText(mFileName.substring(mFileName.lastIndexOf("/")+1));
                activityAddTrainingTipsBinding.flAudio.setVisibility(View.VISIBLE);
                startFinalPlaying();
                bottomSheetDialog.dismiss();
            }
        });
        fl_cancel.setOnClickListener(v -> {
            MediaPlayer mediaPlayer = mPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mPlayer = null;
            }
            mFileName="";
            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "recordingAudio");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public void startRecording() {

        File outputFolder = getApplicationContext().getExternalFilesDir(getFilename());
        Log.i("TAG", "startRecording: creating output file " + outputFolder.mkdirs());
        File file = new File(outputFolder.getAbsolutePath() + new Date().getTime() + ".wav");
        output = file;
        mFileName = file.getAbsolutePath();
        Log.d("filname", mFileName);

        MediaRecorder mediaRecorder = new MediaRecorder();
        mRecorder = mediaRecorder;
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(output.getAbsolutePath());
        mRecorder.setMaxDuration(MAX_DURATION);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("TAG", "startRecording: ", e);
        }
        mRecorder.start();
    }

    public void pauseRecording() {

        MediaRecorder mediaRecorder = mRecorder;
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                statusTV.setText("Recording Stopped");
                tv_timer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void playAudio() {


        MediaPlayer mediaPlayer = new MediaPlayer();
        this.mPlayer = mediaPlayer;
        try {
            String str = mFileName;
            if (str != null) {
                mediaPlayer.setDataSource(str);
                this.mPlayer.prepare();
                this.mPlayer.start();
                String chronoText = tv_timer.getText().toString();
                String[] array = chronoText.split(":");
                if (array.length == 2) {
                    stoppedMilliseconds = (Integer.parseInt(array[0]) * 60 * 1000) + (Integer.parseInt(array[1]) * 1000);
                } else if (array.length == 3) {
                    stoppedMilliseconds = (Integer.parseInt(array[0]) * 60 * 60 * 1000) + (Integer.parseInt(array[1]) * 60 * 1000) + (Integer.parseInt(array[2]) * 1000);
                }

                timerStart(stoppedMilliseconds);
            }
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pausePlaying() {
        MediaPlayer mediaPlayer = mPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mPlayer = null;
        }
        timerPause();

    }

    public void timerStart(long timeLengthMilli) {
        countDownTimer = new CountDownTimer(timeLengthMilli + 1000, 1000L) { // from class: com.selectronic.MainActivity.7
            @Override // android.os.CountDownTimer
            public void onTick(long millisUntilFinished) {

                millisUntilFinishedd = millisUntilFinished;
                if (tv_timer.getVisibility() == View.VISIBLE) {
                    tv_timer.setVisibility(View.GONE);
                }
                tv_countDown.setVisibility(View.VISIBLE);
                tv_countDown.setText("00: " + (millisUntilFinished / 1000));
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
            }
        };
        countDownTimer.start();

    }

    public void timerPause() {

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void timerResume() {
        timerStart(stoppedMilliseconds);
    }


    private void startFinalPlaying(){
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.setVolume(5f, 5f);
            mPlayer.setLooping(false);
            //mediaSeekBar.setMax(mPlayer.getDuration());
            mPlayer.start();
            activityAddTrainingTipsBinding.waveLineView.setVisibility(View.VISIBLE);
            activityAddTrainingTipsBinding.waveLineView.startAnim();
            activityAddTrainingTipsBinding.ivPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
            activityAddTrainingTipsBinding.tvPlay.setText(getString(R.string.stop_recordinggg));
            mPlayer.setOnCompletionListener(mp -> stopFinalPlaying());
            //mediaPlayerSeekBar();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopFinalPlaying(){
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;

        activityAddTrainingTipsBinding.waveLineView.stopAnim();
        activityAddTrainingTipsBinding.waveLineView.clearAnimation();
        activityAddTrainingTipsBinding.waveLineView.setVisibility(View.GONE);
        activityAddTrainingTipsBinding.ivPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
        activityAddTrainingTipsBinding.tvPlay.setText(getString(R.string.play_recordinggg));

    }

   /* public void PostFeedFinal() {
        try {
            this.loaderView.showLoader();
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("userId", new StringBody(SessionManager.get_user_id(this.prefs)));
            entity.addPart("s", new StringBody(SessionManager.get_session_id(this.prefs)));
            entity.addPart("activeUser", new StringBody(SessionManager.get_get_active_user(this.prefs)));
            File file = new File(mFileName);
            FileBody fileBody = new FileBody(file);
            entity.addPart("myfile", fileBody);
            // Log.d("set", entity + com.intuit.sdp.BuildConfig.FLAVOR);
            MultipartRequest req = new MultipartRequest("https://www.selectronicindia.com/manager/webservice/uploadAudioFile", new Response.Listener<String>() { // from class: com.selectronic.MainActivity.8
                @Override // com.android.volley.Response.Listener
                public void onResponse(String response) {
                    try {
                        MainActivity.this.loaderView.hideLoader();
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.getInt("status_code") == 200) {
                            if (MainActivity.this.output.exists()) {
                                MainActivity.this.output.delete();
                                String unused = MainActivity.mFileName = null;
                            }
                            MainActivity.this.startTV.setEnabled(true);
                            MainActivity.this.playTV.setEnabled(false);
                            MainActivity.this.stopplayTV.setEnabled(false);
                            MainActivity.this.tv_uplod.setEnabled(false);
                            MainActivity.this.tv_cancel.setEnabled(false);
                            MainActivity.this.stopTV.setEnabled(false);
                            MainActivity.this.stopTV.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.dark_gray));
                            MainActivity.this.playTV.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.dark_gray));
                            MainActivity.this.stopplayTV.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.dark_gray));
                            MainActivity.this.startTV.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.dark_gray));
                            MainActivity.this.tv_uplod.setBackgroundColor(Color.parseColor("#F0F6FC"));
                            MainActivity.this.tv_cancel.setBackgroundColor(Color.parseColor("#FFE5E5"));
                            Toaster.customToast(jsonObject.getString("message"));
                        } else if (jsonObject.optString("status_code").equalsIgnoreCase("failed")) {
                            Global.msgDialog(MainActivity.this.mActivity, jsonObject.optJSONObject("errors").optString("error_text"));
                        } else {
                            Global.msgDialog(MainActivity.this.mActivity, MainActivity.this.getResources().getString(R.string.error_serverr));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() { // from class: com.selectronic.MainActivity.9
                @Override // com.android.volley.Response.ErrorListener
                public void onErrorResponse(VolleyError error) {
                    MainActivity.this.loaderView.hideLoader();
                    error.printStackTrace();
                }
            }, entity);
            Log.d("PostEntity", req.toString());
            RetryPolicy policy = new DefaultRetryPolicy(50000, 0, 1.0f);
            req.setRetryPolicy(policy);
            this.queue.add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (null != image_uri) {
            activityAddTrainingTipsBinding.flImage.setVisibility(View.VISIBLE);
            activityAddTrainingTipsBinding.roundedImagePic.setVisibility(View.VISIBLE);
            activityAddTrainingTipsBinding.roundedImagePic.setImageURI(image_uri);
        }

        // creating object of
        // media controller class
        mediaController = new MediaController(this);

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(activityAddTrainingTipsBinding.videoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(activityAddTrainingTipsBinding.videoView);

        // sets the media controller to the videoView
        activityAddTrainingTipsBinding.videoView.setMediaController(mediaController);

        // starts the video
        activityAddTrainingTipsBinding.videoView.start();
    }

    public void onPause() {
        super.onPause();
        pauseRecording();
        MediaPlayer mediaPlayer = mPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mPlayer = null;
            stopFinalPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaPlayer mediaPlayer = this.mPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.mPlayer = null;
            stopFinalPlaying();
        }
    }


}