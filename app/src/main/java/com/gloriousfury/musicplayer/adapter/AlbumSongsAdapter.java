package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.utils.Timer;
import com.gloriousfury.musicplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;


public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder> {
    Context context;
    private ArrayList<Audio> song_list;
    private Audio activeAudio = new Audio();
    boolean serviceBound;
    private MediaPlayerService player;
    String SONG = "single_audio";
    String SONG_TITLE = "song_title";
    String SONG_ARTIST = "song_artist";
    String ALBUM_ART_URI = "song_album_art_uri";
    int activeAudioIndex = -1;
    int formerAudioIndex = -1;
    String actionMode = null;
    MediaPlayerService  mediaPlayerService;



    public AlbumSongsAdapter(Context context, ArrayList<Audio> song_list) {
        this.context = context;
        this.song_list = song_list;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView song_title, artist, duration;
        ImageView song_background;


        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
//            title = (TextView) view.findViewById(menu_item);

            mediaPlayerService = new MediaPlayerService(context);

            if(mediaPlayerService.isPng()){
                Utils.serviceBound = true;
            }else{
                Utils.serviceBound = false;

            }
            song_title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.artist);
            duration = (TextView) view.findViewById(R.id.song_duration);
            song_background = (ImageView) view.findViewById(R.id.song_background);


        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();

            activeAudioIndex = adapterposition;
            Audio singleSong = song_list.get(adapterposition);
            activeAudio = singleSong;
            song_title.setTextSize(20);
            playAudio(activeAudioIndex);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_albumsongs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String title = song_list.get(position).getTitle();

        holder.song_title.setText(title);
        holder.artist.setText(song_list.get(position).getArtist());
        String duration = String.valueOf(Timer.milliSecondsToTimer(song_list.get(position).getDuration()));
        holder.duration.setText(duration);


        if (activeAudio.getTitle()!= null) {
            if (title.contentEquals(activeAudio.getTitle())) {
                holder.song_title.setTextSize(20);

            } else {
                holder.song_title.setTextSize(15);

            }
        }

    }


    @Override
    public int getItemCount() {

        if (song_list != null) {
            return song_list.size();
        } else {

            return 0;
        }

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MediaPlayerService  mediaPlayerService = new MediaPlayerService(context);

            if(mediaPlayerService.isPng()) {
                Utils.serviceBound = true;
            }else{
              Utils.serviceBound = false;
            }
        }
    };


    private void playAudio(int audioIndex) {
        //Check is service is active
        serviceBound = Utils.isServiceBound();
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(context);
            storage.storeAudio(song_list);
            storage.storeAudioIndex(audioIndex);

            Toast.makeText(context, String.valueOf(storage.loadAudioIndex()), Toast.LENGTH_LONG).show();
            Intent playerIntent = new Intent(context, MediaPlayerService.class);
            context.startService(playerIntent);
            context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(context);
            storage.storeAudio(song_list);
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            context.sendBroadcast(broadcastIntent);
        }
    }


    public void setAdapterData(int audioIndex) {
        Toast.makeText(context, "Set Adapter was called", Toast.LENGTH_LONG).show();
        formerAudioIndex = activeAudioIndex;
        activeAudioIndex = audioIndex;
        notifyItemChanged(formerAudioIndex);
        activeAudio = song_list.get(activeAudioIndex);
        notifyItemChanged(activeAudioIndex);


    }


}


