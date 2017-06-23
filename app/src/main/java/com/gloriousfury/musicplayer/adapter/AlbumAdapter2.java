package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.AlbumActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AlbumAdapter2 extends RecyclerView.Adapter<AlbumAdapter2.ViewHolder> {
    Context context;
    private ArrayList<Albums> album_list;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String ALBUM_TITLE = "album_title";
    String ALBUM_ARTIST = "ALBUM_ARTIST";
    int TYPE_ALPHABET = 1;
    int TYPE_ALBUM = 2;


    public AlbumAdapter2(Context context, ArrayList<Albums> album_list) {
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

    public class AlphabetViewHolder extends ViewHolder implements View.OnClickListener {
        TextView temp;
        TextView album_title;

        public AlphabetViewHolder(final View view) {
            super(view);

            view.setClickable(true);
            view.setOnClickListener(this);
            album_title = (TextView) view.findViewById(R.id.letter);


        }


        @Override
        public void onClick(View v) {


        }
    }


    public class AlbumViewHolder extends ViewHolder implements View.OnClickListener {
        TextView album_title, artist;
        ImageView song_background;

        public AlbumViewHolder(final View view) {
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


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;


        switch (viewType) {

            case 1:

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.album_header, parent, false);

                return new AlphabetViewHolder(v);


            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_albums, parent, false);

                return new AlbumViewHolder(v);

            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_albums, parent, false);

                return new AlbumViewHolder(v);


        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        if (holder.getItemViewType() == TYPE_ALPHABET) {
            AlphabetViewHolder Holder = (AlphabetViewHolder) holder;
            //  holder.temp.setText(mDataSet[position]);
            if(album_list.get(position).getAlbum()!= null) {
                Holder.album_title.setText(album_list.get(position).getAlbum());
            }else{

                Holder.album_title.setText("#");
            }

        } else if (holder.getItemViewType() == TYPE_ALBUM) {
            AlbumViewHolder Holder = (AlbumViewHolder) holder;
            Holder.album_title.setText(album_list.get(position).getAlbum());
            Holder.artist.setText(album_list.get(position).getArtist());
            Uri albumArtUri = Uri.parse(album_list.get(position).getAlbumArtUriString());
            Picasso.with(context).load(albumArtUri).resize(120, 120).into(Holder.song_background);
//        Picasso.with(context).load(album_list.get(position).getProductImage()).into(holder.productImage);


        }


    }


    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (album_list.get(position).getType() == TYPE_ALPHABET) {
            viewType = TYPE_ALPHABET;
        } else if (album_list.get(position).getType() == TYPE_ALBUM) {
            viewType = TYPE_ALBUM;
        }

        return viewType;
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


