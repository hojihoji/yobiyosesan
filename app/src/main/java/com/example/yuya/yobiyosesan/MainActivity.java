package com.example.yuya.yobiyosesan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    Button mPlay_button;
    Button mStop_button;
    Button mRecord_button;
    MediaPlayer mMediaPlayer;
    MediaRecorder mMediaRecorder;
    String mFilepath;
    SeekBar mSeekBar;
    Runnable mRunnable;
    Handler mHandler= new Handler();

    final int PERMISSION_REQUEST_CODE = 100;

    int mRecStatus = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ラジオボタンの初期設定
        RadioGroup rbGroup = (RadioGroup) findViewById(R.id.rbGroup);
        rbGroup.check(R.id.rbMusic1);
        mFilepath = "music1.mp3";

        //シークバーの初期設定
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);

        //ラジオボタンのリスナー
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton =(RadioButton)findViewById(checkedId);
                boolean checked = radioButton.isChecked();
                switch (radioButton.getId()){
                    case R.id.rbMusic1:
                        if(checked){
                            mFilepath = "music1.mp3";
                            Toast.makeText(getApplication(), "音楽１が選択されました", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.rbMusic2:
                        if(checked){
                            mFilepath = "music2.mp3";
                            Toast.makeText(getApplication(), "音楽２が選択されました", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        //音楽再生
        mPlay_button = (Button) findViewById(R.id.btnPlay);
        mPlay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlay();
            }
        });

        //音楽停止
        mStop_button = (Button) findViewById(R.id.btnStop);
        mStop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioStop();
            }
        });

        //音声録音
        mRecord_button = (Button)findViewById(R.id.btnRec);
        mRecord_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                audioRec();
            }

        });

    }

    private boolean audioSetup(){
        boolean fileCheck = false;

        //音楽再生中は録音ボタンを無効にする
        mRecord_button.setEnabled(false);

        //インスタンスを生成
        mMediaPlayer = new MediaPlayer();

        //assetsからmp3ファイルを読み込み
        try(AssetFileDescriptor assetFileDescriptor = getAssets().openFd(mFilepath);){

            //MediaPlayerに読み込んだ音楽ファイルを指定
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());

            //音量調整を端末のボタンで行えるようにする
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            //音楽に合わせ、シークバーの上限値を設定
            mSeekBar.setMax(mMediaPlayer.getDuration());

            fileCheck = true;
        }catch (IOException el){
            el.printStackTrace();
        }

        return fileCheck;
    }

    private void audioPlay(){
        if(mMediaPlayer == null){
            //audioファイルを読み出し
            if(audioSetup()){
                Toast.makeText(getApplication(), "Reload audio file", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplication(), "Error: Cannot Read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            //繰り返し再生する
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            //リソース解放
            mMediaPlayer.release();
            audioSetup();
        }

        //ループ再生にする
        mMediaPlayer.setLooping(true);
        //再生する
        mMediaPlayer.start();
        //シークバー
        changeSeekBar();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //終了を検知するリスナー
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                audioStop();
            }
        });
    }

    private void audioStop(){
        if(mMediaPlayer !=null){
            //再生終了
            mMediaPlayer.stop();
            //リセット
            mMediaPlayer.reset();
            //リソース解放
            mMediaPlayer.release();
            mMediaPlayer = null;

            //音楽停止の際に録音ボタンを有効にする
            mRecord_button.setEnabled(true);
        }


    }

    private void audioRec() {
        //パーミッション
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //外部ストレージアクセスのパーミッション
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "外部ストレージアクセスは許可されている");
            } else {
                Log.d("PERMISSION", "外部ストレージアクセスは許可されていない");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "レコードオーディオは許可されている");
            } else {
                Log.d("PERMISSION", "レコードオーディオは許可されていない");
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            }


            if (mMediaPlayer == null) {
                switch (mRecStatus) {
                    case 0:
                        mRecStatus = 1;
                        mRecord_button.setText("録音停止");
                        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                        //保存先を指定
                        String filePath = Environment.getExternalStorageDirectory() + "/voice.aac";
                        mMediaRecorder.setOutputFile(filePath);

                        //録音準備
                        try {
                            mMediaRecorder.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMediaRecorder.start();
                    case 1:
                        mRecStatus = 0;
                        mRecord_button.setText("録音");
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                }


            }
        }
    }


    private void changeSeekBar(){
        if(mMediaPlayer!=null){
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            if(mMediaPlayer.isPlaying()){
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                        Toast.makeText(getApplication(),"Change Seek Bar Method called again",Toast.LENGTH_LONG).show();
                    }
                };
                mHandler.postDelayed(mRunnable,1000);
            }
        }
    }

}
