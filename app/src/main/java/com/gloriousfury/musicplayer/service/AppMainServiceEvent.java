package com.gloriousfury.musicplayer.service;

import android.content.Intent;

/**
 * Created by ValueMinds on 3/7/2016.
 */
public class AppMainServiceEvent {

    public static final int ONCOMPLETED_RESPONSE = 1101;
    public static final int PLAYBACK_CHANGE = 1102;
    public static final int PLAYBACK_CHANGE1 = 1103;
    public static final int CATEGORIES_RESPONSE = 1001;
    public static final int ONDATALOADED = 1002;
    public static final int ONDATA_RECIEVED_ALLSONGS = 1003;

    public static String RESPONSE_DATA = "response_data";
    public static String RESPONSE_MESSAGE = "response_message";
    private int eventType;
    private Intent mainIntent;


    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Intent getMainIntent() {
        return mainIntent;
    }

    public void setMainIntent(Intent mainIntent) {
        this.mainIntent = mainIntent;
    }
}
