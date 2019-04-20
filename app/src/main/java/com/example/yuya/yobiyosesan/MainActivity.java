package com.example.yuya.yobiyosesan;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    Button play_button;
    Button stop_button;
    MediaPlayer mediaPlayer;
    String mFilepath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ラジオボタンの初期設定
        RadioGroup rbGroup = (RadioGroup) findViewById(R.id.rbGroup);
        rbGroup.check(R.id.rbMusic1);
        mFilepath = "music1.mp3";

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
        play_button = (Button) findViewById(R.id.btnPlay);
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlay();
            }
        });

        //音楽停止
        stop_button = (Button) findViewById(R.id.btnStop);
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioStop();
            }
        });
    }

    private boolean audioSetup(){
        boolean fileCheck = false;

        //インスタンスを生成
        mediaPlayer = new MediaPlayer();

        //assetsからmp3ファイルを読み込み
        try(AssetFileDescriptor assetFileDescriptor = getAssets().openFd(mFilepath);){

            //MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());

            //音量調整を端末のボタンで行えるようにする
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            fileCheck = true;
        }catch (IOException el){
            el.printStackTrace();
        }

        return fileCheck;
    }

    private void audioPlay(){
        if(mediaPlayer == null){
            //audioファイルを読み出し
            if(audioSetup()){
                Toast.makeText(getApplication(), "Reload audio file", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplication(), "Error: Cannot Read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            //繰り返し再生する
            mediaPlayer.stop();
            mediaPlayer.reset();
            //リソース解放
            mediaPlayer.release();
            audioSetup();
        }

        //ループ再生にする
        mediaPlayer.setLooping(true);
        //再生する
        mediaPlayer.start();
        //終了を検知するリスナー
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                audioStop();
            }
        });
    }

    private void audioStop(){
        if(mediaPlayer !=null){
            //再生終了
            mediaPlayer.stop();
            //リセット
            mediaPlayer.reset();
            //リソース解放
            mediaPlayer.release();
            mediaPlayer = null;
        }


    }

}
