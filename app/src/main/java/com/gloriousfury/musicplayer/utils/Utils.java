package com.gloriousfury.musicplayer.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Playlist;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;

public class Utils {


    public static boolean serviceBound = false;
    private MediaPlayerService player;

    private static Context context;
    private ArrayList<Audio> song_list = new ArrayList<>();
    private int audioIndex;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;
    String DONOT_PLAY_CHECKER = "do_not_play_checker";

    public Utils(Context context) {
        this.context = context;
    }


    private final static Utils ourInstance = new Utils(context);
    public static Utils getInstance() {
        return ourInstance;
    }


    public Utils(Context context, ArrayList<Audio> song_list, int audioIndex) {
        this.context = context;
        this.song_list = song_list;
        this.audioIndex = audioIndex;
    }


    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            Utils.serviceBound = true;


//            Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    public void playAudio(int audioIndex, ArrayList<Audio> song_list) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(context);
            storage.storeAudio(song_list);
            storage.storeAudioIndex(audioIndex);
//            Audio audio = song_list.get(audioIndex);
//
//            Toast.makeText(context, audio.getTitle().toString(), Toast.LENGTH_LONG);

//            Toast.makeText(context, String.valueOf(storage.loadAudioIndex()), Toast.LENGTH_LONG).show();
            Intent playerIntent = new Intent(context, MediaPlayerService.class);
            context.startService(playerIntent);
            context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(context);
            storage.storeAudioIndex(audioIndex);
            storage.storeAudio(song_list);
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            context.sendBroadcast(broadcastIntent);
        }
    }


    public static boolean isServiceBound() {
        return serviceBound;
    }

    public void startAudioService(int audioIndex, ArrayList<Audio> song_list) {
        StorageUtil storage = new StorageUtil(context);
        //Check is service is active

        //Store Serializable audioList to SharedPreferences
//
        storage.storeAudio(song_list);
        storage.storeAudioIndex(audioIndex);

//            Toast.makeText(context, String.valueOf(song_list.size()), Toast.LENGTH_LONG).show();
        Intent playerIntent = new Intent(context, MediaPlayerService.class);
        playerIntent.putExtra(DONOT_PLAY_CHECKER, "donotplay");
        context.startService(playerIntent);
        context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;


    }


    //gridview decoration
    public static int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    //gridview decoration
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}