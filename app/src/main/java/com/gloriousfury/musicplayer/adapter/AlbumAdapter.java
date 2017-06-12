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
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.ui.activity.AlbumActivity;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    Context context;
    private ArrayList<Albums> album_list;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String ALBUM_TITLE = "album_title";
    String ALBUM_ARTIST = "ALBUM_ARTIST";


    public AlbumAdapter(Context context, ArrayList<Albums> album_list) {
        this.context = context;
        this.album_list = album_list;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView album_title, artist;
        ImageView song_background;


        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
//            title = (TextView) view.findViewById(menu_item);


            album_title = (TextView) view.findViewById(R.id.album_name);
            artist = (TextView) view.findViewById(R.id.artist);


            song_background = (ImageView) view.findViewById(R.id.song_background);


        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();

            Intent openSingleSongActivity = new Intent(context, AlbumActivity.class);
            openSingleSongActivity.putExtra(ALBUM_TITLE, album_list.get(getAdapterPosition()).getAlbum());
            openSingleSongActivity.putExtra(ALBUM_ARTIST, album_list.get(getAdapterPosition()).getArtist());
            context.startActivity(openSingleSongActivity);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_albums, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.album_title.setText(album_list.get(position).getAlbum());
        holder.artist.setText(album_list.get(position).getArtist());
        Uri albumArtUri = Uri.parse(album_list.get(position).getAlbumArtUriString());
        Picasso.with(context).load(albumArtUri).resize(120, 120).into(holder.song_background);
//        Picasso.with(context).load(album_list.get(position).getProductImage()).into(holder.productImage);

    }

    @Override
    public int getItemCount() {

        if (album_list != null) {
            return album_list.size();
        } else {

            return 0;
        }

    }


}


