package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Playlist;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.AlbumActivity;

import java.util.ArrayList;


public class SongNormalAdapter extends RecyclerView.Adapter<SongNormalAdapter.ViewHolder> {
    Context context;
    private ArrayList<Albums> albumLists;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String ALBUM_TITLE = "album_title";
    String ALBUM_ARTIST = "ALBUM_ARTIST";
    String ALBUM_ART_URI = "album_art_uri";


    public SongNormalAdapter(Context context, ArrayList<Albums> albumLists) {
        this.context = context;
        this.albumLists = albumLists;


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
         
            Intent openAlbumActivity = new Intent(context, AlbumActivity.class);
            openAlbumActivity.putExtra(ALBUM_TITLE, albumLists.get(getAdapterPosition()).getAlbum());
            openAlbumActivity.putExtra(ALBUM_ARTIST, albumLists.get(getAdapterPosition()).getArtist());
            openAlbumActivity.putExtra(ALBUM_ART_URI, albumLists.get(getAdapterPosition()).getAlbumArtUriString());
            context.startActivity(openAlbumActivity);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.title.setText(albumLists.get(position).getAlbum());
        holder.noOfSongs.setText(String.valueOf(albumLists.get(position).getnoOfSongs() +" Songs"));
////

    }

    @Override
    public int getItemCount() {

        if (albumLists != null) {
            return albumLists.size();
        } else {

            return 0;
        }

    }

    public void setAlbumListData(ArrayList<Albums> albumListsArraylist) {
        if (albumListsArraylist != null) {
            this.albumLists = albumListsArraylist;
            notifyDataSetChanged();

        }

    }

}


