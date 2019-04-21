package com.example.yuya.yobiyosesan;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
    MediaPlayer mMediaPlayer;
    String mFilepath;
    SeekBar mSeekBar;
    Runnable mRunnable;
    Handler mHandler= new Handler();



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
    }

    private boolean audioSetup(){
        boolean fileCheck = false;

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
