package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Artist;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.ArtistActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    Context context;
    private ArrayList<Artist> artist_list;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String ALBUM_TITLE = "album_title";
    String  NO_OF_ALBUMS= "artist";
    String ARTIST_ITEM = "artist_item";



    public ArtistAdapter(Context context, ArrayList<Artist> artist_list) {
        this.context = context;
        this.artist_list = artist_list;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, noOfSongs;
       CircleImageView playlist_background;


        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
//            title = (TextView) view.findViewById(menu_item);


            title = (TextView) view.findViewById(R.id.artist_name);
            noOfSongs= (TextView) view.findViewById(R.id.no_of_albums);
            playlist_background = (CircleImageView) view.findViewById(R.id.img_background);



        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();

            Artist singleArtist = artist_list.get(adapterposition);
//            playArtist(adapterposition);
            Intent openArtistActivity = new Intent(context, ArtistActivity.class);
            openArtistActivity.putExtra(ARTIST_ITEM, singleArtist);

            context.startActivity(openArtistActivity);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_artists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.title.setText(artist_list.get(position).getArtistName());
        holder.noOfSongs.setText(String.valueOf(artist_list.get(position).getNoOfAlbums() +" Albums"));
        if (artist_list.get(position).getAlbumArtUri() != null) {

            Uri albumArtUri = Uri.parse(artist_list.get(position).getAlbumArtUri());
            Picasso.with(context).load(albumArtUri).resize(120, 120).into(holder.playlist_background);
        }else{
            Picasso.with(context).load(R.drawable.ic_default_music_image).into(holder.playlist_background);

        }

    }

    @Override
    public int getItemCount() {

        if (artist_list != null) {
            return artist_list.size();
        } else {

            return 0;
        }

    }

    public void setArtistListData(ArrayList<Artist> artist_listArraylist) {
        if (artist_listArraylist != null) {
            this.artist_list = artist_listArraylist;
            notifyDataSetChanged();

        }

    }

}


