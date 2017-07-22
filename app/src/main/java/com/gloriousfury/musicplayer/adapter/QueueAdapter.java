package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.gloriousfury.musicplayer.utils.Timer;
import com.gloriousfury.musicplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {
    Context context;
    private ArrayList<Audio> song_list;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String SONG = "single_audio";
    String CLICK_CHECKER = "click_checker";
    int TYPE_HEADER = 1;


    public QueueAdapter(Context context, ArrayList<Audio> song_list) {
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


            song_title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.artist);
            duration = (TextView) view.findViewById(R.id.song_duration);


            song_background = (ImageView) view.findViewById(R.id.song_background);


        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();
            String checker = null;
            Audio singleSong = song_list.get(adapterposition);
//            Utils appUtils = new Utils(context);

            new Utils(context).playAudio(getAdapterPosition(), song_list);

            Intent openSingleSongActivity = new Intent(context, SingleSongActivity.class);
            openSingleSongActivity.putExtra(CLICK_CHECKER, checker);
            openSingleSongActivity.putExtra(SONG, singleSong);

            context.startActivity(openSingleSongActivity);

        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_queue, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            holder.song_title.setText(song_list.get(position).getTitle());
            holder.artist.setText(song_list.get(position).getArtist());


            int backgroundColor = (ContextCompat.getColor(context, R.color.colorPrimaryDark));


            String duration = String.valueOf(Timer.milliSecondsToTimer(song_list.get(position).getDuration()));
            holder.duration.setText(duration);
//            if (song_list.get(position).getAlbumArtUriString() != null) {
//                Uri albumArtUri = Uri.parse(song_list.get(position).getAlbumArtUriString());
//                Picasso.with(context).load(albumArtUri).resize(120, 120).into(holder.song_background);
//            } else {
//                Picasso.with(context).load(R.drawable.ic_default_music_option1).into(holder.song_background);
//
//            }



    }

    @Override
    public int getItemCount() {

        if (song_list != null) {
            return song_list.size();
        } else {

            return 0;
        }

    }


    public void setAudioListData(ArrayList<Audio> songArraylist) {
        if (songArraylist != null) {
            this.song_list = songArraylist;
            notifyDataSetChanged();

        }

    }


}


