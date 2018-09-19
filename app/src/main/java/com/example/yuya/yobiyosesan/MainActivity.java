package com.example.yuya.yobiyosesan;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MediaPlayer mp = null;
    Button play_button;
    Button stop_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.sample);

        play_button = (Button) findViewById(R.id.btnPlay);
        play_button.setOnClickListener(this);
    }

    public void onClick(View v){

    }

}
