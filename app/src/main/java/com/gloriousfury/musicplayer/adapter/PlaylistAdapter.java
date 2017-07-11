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
import com.gloriousfury.musicplayer.model.Playlist;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.utils.Timer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    private ArrayList<Playlist> playlist;
    boolean serviceBound = false;
    private MediaPlayerService player;



    public PlaylistAdapter(Context context, ArrayList<Playlist> playlist) {
        this.context = context;
        this.playlist = playlist;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, noOfSongs;
        ImageView song_background;


        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
//            title = (TextView) view.findViewById(menu_item);


            title = (TextView) view.findViewById(R.id.playlist_title);
            noOfSongs= (TextView) view.findViewById(R.id.playlist_no_ofsongs);
         



        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();

//            Playlist singleSong = playlist.get(adapterposition);
//            playPlaylist(adapterposition);
//            Intent openSingleSongActivity = new Intent(context, SingleSongActivity.class);
//            openSingleSongActivity.putExtra(SONG, singleSong);
//
//            context.startActivity(openSingleSongActivity);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.title.setText(playlist.get(position).getPlaylistTitle());
        holder.noOfSongs.setText(String.valueOf(playlist.get(position).getPlaylistNoOfSongs() +" Songs"));
////

    }

    @Override
    public int getItemCount() {

        if (playlist != null) {
            return playlist.size();
        } else {

            return 0;
        }

    }

    public void setPlaylistListData(ArrayList<Playlist> playlistArraylist) {
        if (playlistArraylist != null) {
            this.playlist = playlistArraylist;
            notifyDataSetChanged();

        }

    }

}


