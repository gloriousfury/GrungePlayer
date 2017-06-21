package com.gloriousfury.musicplayer.service;

import android.content.Intent;

/**
 * Created by ValueMinds on 3/7/2016.
 */
public class AppMainServiceEvent {

    public static final int ONCOMPLETED_RESPONSE = 1101;
    public static final int CATEGORIES_RESPONSE = 1001;
    public static final int COURSES_RESPONSE = 1002;
    public static final int COURSE_RESPONSE = 1003;
    public static final int REGISTER_RESPONSE = 1004;
    public static final int SCHEDULES_RESPONSE = 1005;
    public static final int TRAINING_RESPONSE = 1006;
    public static final int UPDATES_RESPONSE = 1007;
    public static final int TRAINERS_RESPONSE = 1008;
    public static final int TOKEN_RESPONSE = 1009;
    public static final int REGISTER_CORPER_RESPONSE = 1010;
    public static final int USER_TRAININGS_RESPONSE = 1011;
    public static final int USER_EVENTS_RESPONSE = 1012;
    public static final int USER_UPDATES_RESPONSE = 1013;
    public static final int USER_MESSAGES_RESPONSE = 1014;
    public static final int MESSAGE_REPLY_RESPONSE = 1015;
    public static final int USER_TALK_TO_SAED_RESPONSE = 1016;
    public static final int GET_REPLIES_RESPONSE= 1017;

    public static final int APPLY_FOR_SCHEDULE_RESPONSE= 1018;
    public static final int GET_CHANNELS= 1019;
    public static final int GET_NEWS= 1020;
    public static final int UPDATE_ACCOUNT_DETAILS = 1021;
    public static final int VERIFY_EMAIL = 1022;
    public static final int VERIFY_PHONE_NO = 1022;
    public final static int CONFIRM_PHONE_NO = 1023;
    public final static int CONFIRM_EMAIL = 1024;
    public final static int VERIFICATION_STATUS = 1026;

    public final static int GET_ACCOUNT_ID = 1027;
    public final static int GET_ACCOUNT_ID_2 = 1028;
    public final static int GET_TRAINER_LIST = 1029;
    public final static int GET_TRAINER_LIST_INCATEGORY = 1030;
    public final static int GET_TOPICS = 1031;
    public final static int GET_FORUM_CATEGORIES = 1032;

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
