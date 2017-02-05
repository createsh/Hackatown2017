package com.nuance.speechkitsample;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.AudioPlayer;
import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Interpretation;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Random;

import static android.os.SystemClock.sleep;

/**
 * This Activity is built to demonstrate how to perform NLU (Natural Language Understanding).
 *
 * This Activity is very similar to ASRActivity. Much of the code is duplicated for clarity.
 *
 * NLU is the transformation of text into meaning.
 *
 * When performing speech recognition with SpeechKit, you have a variety of options. Here we demonstrate
 * Context Tag and Language.
 *
 * The Context Tag is assigned in the system configuration upon deployment of an NLU model.
 * Combined with the App ID, it will be used to find the correct NLU version to query.
 *
 * Languages can also be configured. Supported languages can be found here:
 * http://developer.nuance.com/public/index.php?task=supportedLanguages
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class NLUActivity extends DetailActivity implements View.OnClickListener {
    String result;
    String sub;             // GLOBAL VARIABLE
    int matchThis;
    int counter;
    private Audio startEarcon;
    private Audio stopEarcon;
    private Audio errorEarcon;
    private boolean timerHasStarted = false;
    private ImageView mImageView;

    private RadioGroup detectionType;
    private String nluContextTag; // CHANGED
    private String language; // CHANGED

    private TextView logs;
    private Button clearLogs;

    private Button toggleReco;
    private CountDownTimer cdTimer;
    private ProgressBar volumeBar;

    private Session speechSession;
    private Transaction recoTransaction;
    private State state = State.IDLE;
    private CountDownTimer cdTimER;
    private int n;
    /* THIS IS WHERE WE ENTER THE RECORDING ACTIVITY THINGS*/
    MediaPlayer mySound;
    int[] audioClips = {R.raw.pont_jacques_cartier, R.raw.la_ronde, R.raw.polytechnique, R.raw.vieux_port, R.raw.lac_aux_castors, R.raw.centre_bell, R.raw.centre_des_sciences_de_montreal, R.raw.la_banquise, R.raw.mcgill, R.raw.parc_jean_drapeau};
    Hashtable<Integer, String> hashTable = new Hashtable<Integer, String>();

    Hashtable<String,Integer> pictureHash = new Hashtable<String,Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nlu);
            detectionType = (RadioGroup) findViewById(R.id.detection_type_picker);
            //nluContextTag = (EditText)findViewById(R.id.nlu_context_tag);
            //nluContextTag.setText(Configuration.CONTEXT_TAG);
            //language = (EditText)findViewById(R.id.language);
            //language.setText(Configuration.LANGUAGE_CODE);

            nluContextTag = Configuration.CONTEXT_TAG;
            language = Configuration.LANGUAGE_CODE;         // EDITED

            logs = (TextView) findViewById(R.id.logs);
            clearLogs = (Button) findViewById(R.id.clear_logs);
            clearLogs.setOnClickListener(this);
            Random rand = new Random();


            //logs.append(hashTable.get(audioClips[n]));

            n = rand.nextInt(9) + 0;

            /* THIS IS WHERE WE WRITE THE RECORDINGACTIVITY THINGS */
            logs = (TextView) findViewById(R.id.logs);
            hashTable.put(audioClips[0], "Pont_Jacques_Cartier");
            hashTable.put(audioClips[1], "La_Ronde");
            hashTable.put(audioClips[2], "Polytechnique");
            hashTable.put(audioClips[3], "Vieux_Port");
            hashTable.put(audioClips[4], "Lac_Castor");
            hashTable.put(audioClips[5], "Centre_Bell");
            hashTable.put(audioClips[6], "Centre_des_Sciences");
            hashTable.put(audioClips[7], "La_Banquise");
            hashTable.put(audioClips[8], "McGill");
            hashTable.put(audioClips[9], "Parc_Jean_Drapeau");
            mySound = MediaPlayer.create(this, audioClips[n]);
            mySound.start();
            toggleReco = (Button) findViewById(R.id.toggle_reco);
            toggleReco.setOnClickListener(this);
            //cdTimer.start(); timerHasStarted = true;

            volumeBar = (ProgressBar) findViewById(R.id.volume_bar);
            //Create a session
            speechSession = Session.Factory.session(this, Configuration.SERVER_URI, Configuration.APP_KEY);
            loadEarcons();
            setState(State.IDLE);

        // sound -> button -> timer -> timer hits 0 -> reset mySound -> replay
    }

    /*public void playMusic(View view) {
        mySound.start();
        //matchThis = audioClips[rnd];
        matchThis = audioClips[3];
        logs.append("ASJKDASJHDAKSDHASHKDASJHKDHKASDLHAJKDHASJDi");
        logs.append(hashTable.get(matchThis));
        mySound = MediaPlayer.create(this, matchThis);
    }*/

    @Override
    public void onClick(View v) {
        if(v == clearLogs) {
            logs.setText("");
        } else if(v == toggleReco) {
            toggleReco();
        }
    }

    /* Reco transactions */

    private void toggleReco() {
        switch (state) {
            case IDLE:
                recognize();
                break;
            case LISTENING:
                stopRecording();
                break;
            case PROCESSING:
                cancel();
                break;
        }
    }

    /**
     * Start listening to the user and streaming their voice to the server.
     */
    private void recognize() {
        //Setup our Reco transaction options.
        Transaction.Options options = new Transaction.Options();
        options.setDetection(resourceIDToDetectionType(detectionType.getCheckedRadioButtonId()));
        options.setLanguage(new Language(language));
        options.setEarcons(startEarcon, stopEarcon, errorEarcon, null);

        //Add properties to appServerData for use with custom service. Leave empty for use with NLU.
        JSONObject appServerData = new JSONObject();
        //Start listening
        recoTransaction = speechSession.recognizeWithService(nluContextTag, appServerData, options, recoListener);
    }

    private Transaction.Listener recoListener = new Transaction.Listener() {
        @Override
        public void onStartedRecording(Transaction transaction) {
            //logs.append("\nonStartedRecording");

            //We have started recording the users voice.
            //We should update our state and start polling their volume.
            setState(State.LISTENING);
            startAudioLevelPoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {
            //logs.append("\nonFinishedRecording");

            //We have finished recording the users voice.
            //We should update our state and stop polling their volume.
            setState(State.PROCESSING);
            stopAudioLevelPoll();
        }

        @Override
        public void onServiceResponse(Transaction transaction, org.json.JSONObject response) {
            try {
                // 2 spaces for tabulations.
                logs.append("\nonServiceResponse: " + response.toString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // We have received a service response. In this case it is our NLU result.
            // Note: this will only happen if you are doing NLU (or using a service)
            setState(State.IDLE);
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {
            logs.append("\n Key word(s): " + recognition.getText() + "\n");

            //We have received a transcription of the users voice from the server.
            setState(State.IDLE);
        }

        @Override
        public void onInterpretation(Transaction transaction, Interpretation interpretation) {
            try {
                //logs.append("\nonInterpretation: " + interpretation.getResult().toString(2));
                result = interpretation.getResult().toString(2);
                /*String[] temp = result.split(":;'?=()!\\[\\]-]+ ");
                for(int i = 0; i < temp.length; i++) {
                    if (temp[i].equals("Vieux")) {
                        logs.append("YOOOOOOOOOOOO");
                    }
                }*/
                int index = result.indexOf("value");
                /*if(result.substring(index-1, index+5).equals("")) {

                }*/
                int i = index;
                while (!result.substring(i+9,i+10).equals("\"")){
                    i++;
                }

                sub= result.substring(index+9, i+9);
                //logs.append("\n" + sub);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            /* THIS IS WHERE THE MATCHING HAPPENS*/

            String temp = hashTable.get(audioClips[n]);
            if(sub.equals(temp)) {
                logs.append("Vous avez la bonne réponse!" + "\n");
                counter+=1;
                // THIS DOES NOT DO ANYTHING...for now
                if(counter==2) {
                    mImageView = (ImageView) findViewById(R.id.photo);
                    mImageView.setImageResource(R.drawable.poutine_reward);
                }
            } else {
                String[] errorString = temp.split("_");
                String finalErrorString = "";
                for (int i = 0; i < errorString.length; i++) {
                    finalErrorString+= errorString[i] + " ";
                }
                logs.append("La bonne réponse est " + finalErrorString + "\n");
            }

            //sleep(5000);


            // We have received a service response. In this case it is our NLU result.
            // Note: this will only happen if you are doing NLU (or using a service)
            setState(State.IDLE);
        }

        @Override
        public void onSuccess(Transaction transaction, String s) {
            //logs.append("\nonSuccess");

            //Notification of a successful transaction. Nothing to do here.
        }

        @Override
        public void onError(Transaction transaction, String s, TransactionException e) {
            logs.append("\nonError: " + e.getMessage() + ". " + s);

            //Something went wrong. Check Configuration.java to ensure that your settings are correct.
            //The user could also be offline, so be sure to handle this case appropriately.
            //We will simply reset to the idle state.
            setState(State.IDLE);
        }
    };

    /**
     * Stop recording the user
     */
    private void stopRecording() {
        recoTransaction.stopRecording();
    }

    /**
     * Cancel the Reco transaction.
     * This will only cancel if we have not received a response from the server yet.
     */
    private void cancel() {
        recoTransaction.cancel();
    }

    /* Audio Level Polling */

    private Handler handler = new Handler();

    /**
     * Every 50 milliseconds we should update the volume meter in our UI.
     */
    private Runnable audioPoller = new Runnable() {
        @Override
        public void run() {
            float level = recoTransaction.getAudioLevel();
            volumeBar.setProgress((int)level);
            handler.postDelayed(audioPoller, 50);
        }
    };

    /**
     * Start polling the users audio level.
     */
    private void startAudioLevelPoll() {
        audioPoller.run();
    }

    /**
     * Stop polling the users audio level.
     */
    private void stopAudioLevelPoll() {
        handler.removeCallbacks(audioPoller);
        volumeBar.setProgress(0);
    }


    /* State Logic: IDLE -> LISTENING -> PROCESSING -> repeat */

    private enum State {
        IDLE,
        LISTENING,
        PROCESSING
    }

    /**
     * Set the state and update the button text.
     */
    private void setState(State newState) {
        state = newState;
        switch (newState) {
            case IDLE:
                toggleReco.setText(getResources().getString(R.string.recognize_with_service));
                break;
            case LISTENING:
                toggleReco.setText(getResources().getString(R.string.listening));
                break;
            case PROCESSING:
                toggleReco.setText(getResources().getString(R.string.processing));
                break;
        }
    }

    /* Earcons */

    private void loadEarcons() {
        //Load all of the earcons from disk
        startEarcon = new Audio(this, R.raw.sk_start, Configuration.PCM_FORMAT);
        stopEarcon = new Audio(this, R.raw.sk_stop, Configuration.PCM_FORMAT);
        errorEarcon = new Audio(this, R.raw.sk_error, Configuration.PCM_FORMAT);
    }

    /* Helpers */

    private DetectionType resourceIDToDetectionType(int id) {
        if(id == R.id.long_endpoint) {
            return DetectionType.Long;
        }
        if(id == R.id.short_endpoint) {
            return DetectionType.Short;
        }
        if(id == R.id.none) {
            return DetectionType.None;
        }
        return null;
    }

    public void startCountDownTimer(){
        //cdTimer = new CountDownTimer(5000, 1000) {
            //EditText mTextField=(EditText)findViewById(R.id.timer);
            //public void onTick(long millisUntilFinished) {
               // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);

            //}

           // public void onFinish() {
            //    mTextField.setText("done!");
            //    timerHasStarted = false;
            //}


        }//.start();

}
