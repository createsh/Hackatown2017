package com.example.sihyeon.music;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


import java.util.Random;
public class MainActivity extends AppCompatActivity {

    MediaPlayer mySound;
    private CountDownTimer cdTimer;

    int[] audioClibs = {R.raw.arianagrande, R.raw.jumpstreet, R.raw.justinbieber, R.raw.justinbieber, R.raw.letitgo, R.raw.starwars, R.raw.trump, R.raw.whysoserious};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mySound = MediaPlayer.create(this, getRandom(audioClibs));



    }

    public void playMusic(View view) {
        startCountDownTimer();
        mySound.start();




    }


    public void pauseMusic(View view) {
        mySound.pause();
        cdTimer.cancel();
        mySound = MediaPlayer.create(this, getRandom(audioClibs));





    }

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public void startCountDownTimer(){
        cdTimer = new CountDownTimer(10000, 1000) {
            EditText mTextField=(EditText)findViewById(R.id.editText);
            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);

            }

            public void onFinish() {
                mTextField.setText("done!");
            }


        }.start();

    }
}
