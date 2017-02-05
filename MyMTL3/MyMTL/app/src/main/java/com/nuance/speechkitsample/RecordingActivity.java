package com.nuance.speechkitsample;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import java.util.Random;
import java.io.*;
import java.util.*;


import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

import static com.nuance.speechkitsample.R.id.logs;

//Created by chrisissa on 2017-02-04.

public class RecordingActivity extends AppCompatActivity {
    MediaPlayer mySound;
    int[] audioClips = {R.raw.pont_jacques_cartier, R.raw.la_ronde, R.raw.polytechnique, R.raw.vieux_port, R.raw.lac_aux_castors};
    private TextView logs;
    Hashtable<Integer, String> hashTable = new Hashtable<Integer, String>();
    //NLUActivity json = new NLUActivity();
    //String result = json.sub;                  Are those necessary???!!!

    int rnd = new Random().nextInt(audioClips.length);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySound = MediaPlayer.create(this, audioClips[rnd]);
        logs = (TextView)findViewById(R.id.logs);

        hashTable.put(audioClips[0], "Pont_Jacques-Cartier");
        hashTable.put(audioClips[1], "La_Ronde");
        hashTable.put(audioClips[2], "Polytechnique");
        hashTable.put(audioClips[3], "Vieux_Port");
        hashTable.put(audioClips[4], "Lac_Castor");
        //if(result.equals(hashTable.get(audioClips[3]))) {
        //}
        logs.append("YOU HAVE THE CORRECT ANSWER");
        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(RecordingActivity.this);
        builder1.setMessage("YOU HAVE THE CORRECT ANSWER");
        builder1.setCancelable(true);

        AlertDialog alert1 = builder1.create();
        alert1.show();*/



    }

    public void playMusic(View view) {
        mySound.start();
        mySound = MediaPlayer.create(this, audioClips[rnd]);
    }



        //if(result.equals(hashTable.get(audioClips[3]))) {
            /*Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);*/
        //}

    //}
}
