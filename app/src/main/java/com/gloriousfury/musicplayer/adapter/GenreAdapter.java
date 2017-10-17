package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Genre;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.GenreActivity;
import com.gloriousfury.musicplayer.utils.Timer;
import com.squareup.picasso.Picasso;
import com.stfalcon.multiimageview.MultiImageView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    Context context;
    private ArrayList<Genre> genre_list;
    private MediaPlayerService player;
    String GENRE_ITEM = "genre_item";
    int TYPE_ONE = 1;
    int TYPE_TWO = 2;
    int TYPE_THREE = 3;

    public GenreAdapter(Context context, ArrayList<Genre> genre_list) {
        this.context = context;
        this.genre_list = genre_list;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, noOfSongs;
        ImageView background;


        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
//            title = (TextView) view.findViewById(menu_item);


            title = (TextView) view.findViewById(R.id.genre_title);
//            noOfSongs= (TextView) view.findViewById(R.id.no_of_albums);
            background = (ImageView) view.findViewById(R.id.img_background);
//            background.setScaleType(ImageView.ScaleType.CENTER_CROP);


        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();

            Genre singleGenre = genre_list.get(adapterposition);

            Intent openGenreActivity = new Intent(context, GenreActivity.class);
            openGenreActivity.putExtra(GENRE_ITEM, singleGenre);

            context.startActivity(openGenreActivity);

        }
    }


    public class GenreType1ViewHolder extends GenreAdapter.ViewHolder implements View.OnClickListener {
        ImageView background;
        TextView noOfSongs;

        public GenreType1ViewHolder(final View view) {
            super(view);

            view.setClickable(true);
            view.setOnClickListener(this);
//            album_title = (TextView) view.findViewById(R.id.letter);
            background = (ImageView) view.findViewById(R.id.img_background1);

            noOfSongs = (TextView) view.findViewById(R.id.no_of_songs);
        }


        @Override
        public void onClick(View v) {


        }
    }


    public class GenreType2ViewHolder extends GenreAdapter.ViewHolder implements View.OnClickListener {
        ImageView background, background2, background3;
        TextView noOfSongs;

        public GenreType2ViewHolder(final View view) {
            super(view);

            view.setClickable(true);
            view.setOnClickListener(this);
            background = (ImageView) view.findViewById(R.id.img_background1);
            background2 = (ImageView) view.findViewById(R.id.img_background2);


            noOfSongs = (TextView) view.findViewById(R.id.no_of_songs);
        }


        @Override
        public void onClick(View v) {


        }
    }


    public class GenreType3ViewHolder extends GenreAdapter.ViewHolder implements View.OnClickListener {
        ImageView background, background2, background3;
        TextView noOfSongs;

        public GenreType3ViewHolder(final View view) {
            super(view);

            view.setClickable(true);
            view.setOnClickListener(this);

            background = (ImageView) view.findViewById(R.id.img_background1);
            background2 = (ImageView) view.findViewById(R.id.img_background2);
            background3 = (ImageView) view.findViewById(R.id.img_background3);

            noOfSongs = (TextView) view.findViewById(R.id.no_of_songs);


        }


        @Override
        public void onClick(View v) {


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre, parent, false);

        switch (viewType) {

            case 1:

                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre, parent, false);


                return new GenreType1ViewHolder(v);
            case 2:

                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre2, parent, false);


                return new GenreType2ViewHolder(v);
            case 3:

                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre3, parent, false);


                return new GenreType3ViewHolder(v);


            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_genre, parent, false);

                return new GenreType1ViewHolder(v);

        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.getItemViewType() == TYPE_ONE) {
            GenreType1ViewHolder Holder = (GenreType1ViewHolder) holder;
            Holder.setIsRecyclable(false);
            Holder.title.setText(genre_list.get(position).getGenreTitle());
            ArrayList<Audio> imageList = genre_list.get(position).getAudioImageList();
            String albumArtUriString = imageList.get(0).getAlbumArtUriString();

            if (albumArtUriString != null) {

//                File file = new File(URI.create(albumArtUriString).getPath());
//                if (file.exists()) {
                //Do something
                Uri albumArtUri = Uri.parse(albumArtUriString);
                Picasso.with(context).load(albumArtUri).into(Holder.background);
//                } else {
//
//
//                    Picasso.with(context).load(R.drawable.background_cover).into(Holder.background);
//                }

            } else {
                Toast.makeText(context,"You wont believe it, It was null", Toast.LENGTH_SHORT).show();
                Picasso.with(context).load(R.drawable.background_cover).into(Holder.background);

            }


        } else if (holder.getItemViewType() == TYPE_TWO) {
            GenreType2ViewHolder Holder = (GenreType2ViewHolder) holder;

            Holder.setIsRecyclable(false);
            Holder.title.setText(genre_list.get(position).getGenreTitle());
            ArrayList<Audio> imageList = genre_list.get(position).getAudioImageList();


            String albumArtUriString = imageList.get(0).getAlbumArtUriString();
            String albumArtUriString2 = imageList.get(1).getAlbumArtUriString();

            if (albumArtUriString != null) {
//            Holder.background.setImageBitmap(bitmap);
                Picasso.with(context).load(getUriFromString(albumArtUriString)).into(Holder.background);

            }
            if (albumArtUriString2 != null) {

                Picasso.with(context).load(getUriFromString(albumArtUriString2)).into(Holder.background2);
            }

//            else {
//
//
//                Picasso.with(context).load(R.drawable.background_cover).into(Holder.background);
//
//
//            }

        } else if (holder.getItemViewType() == TYPE_THREE) {
            GenreType3ViewHolder Holder = (GenreType3ViewHolder) holder;

            Holder.setIsRecyclable(false);
            Holder.title.setText(genre_list.get(position).getGenreTitle());
            ArrayList<Audio> imageList = genre_list.get(position).getAudioImageList();

            Bitmap bitmap;


            String albumArtUriString = imageList.get(0).getAlbumArtUriString();
//        bitmap = getBitmap(albumArtUriString);
            if (albumArtUriString != null) {

                Uri albumArtUri = Uri.parse(albumArtUriString);
//            Holder.background.setImageBitmap(bitmap);
                Picasso.with(context).load(albumArtUriString).into(Holder.background);

            } else {


                Picasso.with(context).load(R.drawable.background_cover).into(Holder.background);


            }

        }


//        holder.background.addImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music_image));
//        holder.background.addImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music_image));
//        holder.background.addImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music_image));
//        placeBitmap(imageList, holder.background);


//        holder.noOfSongs.setText(String.valueOf(genre_list.get(position).getNoOfAlbums() + " Albums"));
//        if (genre_list.get(position).getAlbumArtUri() != null) {
//
//            Uri albumArtUri = Uri.parse(genre_list.get(position).getAlbumArtUri());
//            Picasso.with(context).load(albumArtUri).resize(120, 120).into(holder.playlist_background);
//        } else {
//            Picasso.with(context).load(R.drawable.ic_default_music_image).into(holder.playlist_background);
//
//        }

    }

    @Override
    public int getItemCount() {

        if (genre_list != null) {
            return genre_list.size();
        } else {

            return 0;
        }

    }

    public void setGenreListData(ArrayList<Genre> genre_listArraylist) {
        if (genre_listArraylist != null) {
            this.genre_list = genre_listArraylist;
            notifyDataSetChanged();

        }

    }


    public Bitmap getBitmap(String albumArtUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), Uri.parse(albumArtUri));
            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher);
        } catch (IOException e) {

            e.printStackTrace();
        }

        return bitmap;
    }


    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        int noOfSongs = genre_list.get(position).getNoOfSongs();
        if (noOfSongs <= 1) {
            viewType = TYPE_ONE;
        } else if (noOfSongs > 2 && noOfSongs <= 3) {
            viewType = TYPE_TWO;
        } else if (noOfSongs > 3) {
            viewType = TYPE_THREE;
        } else {
            viewType = TYPE_ONE;
        }

        return viewType;
    }


    public Uri getUriFromString(String uriString) {
        Uri albumArtUri = Uri.parse(uriString);
        return albumArtUri;
    }


}


