package com.nuance.speechkitsample;

import android.net.Uri;

import com.nuance.speechkit.PcmFormat;

/**
 * All Nuance Developers configuration parameters can be set here.
 *
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class Configuration {

    //All fields are required.
    //Your credentials can be found in your Nuance Developers portal, under "Manage My Apps".
    public static final String APP_KEY = "fd0b9ba58e7b66ec9238862faa4ee1a7343cc6a4702e40904086f0893320fd302b98d6b9133ca5a6742cd51e7ab6c6aef28c5362bb93f2fb1ae2844d02817e2d";
    public static final String APP_ID = "NMDPTRIAL_chris_jorjio_issa_mail_mcgill_ca20170204133418";
    public static final String SERVER_HOST = "nmsps.dev.nuance.com";
    public static final String SERVER_PORT = "443";

    public static final String LANGUAGE = "eng-USA";

    public static final Uri SERVER_URI = Uri.parse("nmsps://" + APP_ID + "@" + SERVER_HOST + ":" + SERVER_PORT);

    //Only needed if using NLU
    public static final String CONTEXT_TAG = "M5175_A2435_V1";

    public static final PcmFormat PCM_FORMAT = new PcmFormat(PcmFormat.SampleFormat.SignedLinear16, 16000, 1);
    public static final String LANGUAGE_CODE = (Configuration.LANGUAGE.contains("!") ? "eng-USA" : Configuration.LANGUAGE);

}



