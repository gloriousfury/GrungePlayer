package com.gloriousfury.musicplayer.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
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

   private Context context;
    private ArrayList<Audio> song_list = new ArrayList<>();
    private int audioIndex;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;

    public Utils(Context context){
        this.context = context;
    }
    public Utils(Context context,ArrayList<Audio> song_list, int audioIndex ){
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

            Toast.makeText(context, "Service Bound at being bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    public void playAudio(int audioIndex) {
        //Check is service is active
        MediaPlayerService mediaPlayerService =  new MediaPlayerService(context);
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(context);
            storage.storeAudio(song_list);
            storage.storeAudioIndex(audioIndex);

            Toast.makeText(context, String.valueOf(storage.loadAudioIndex()), Toast.LENGTH_LONG).show();
            Intent playerIntent = new Intent(context, MediaPlayerService.class);
            context.startService(playerIntent);
            context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else if (mediaPlayerService.isPng()) {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(context);
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            context.sendBroadcast(broadcastIntent);
        }else{

            StorageUtil storage = new StorageUtil(context);
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            context.sendBroadcast(broadcastIntent);



        }
    }

    public static boolean isServiceBound(){
        return serviceBound;
    }

}