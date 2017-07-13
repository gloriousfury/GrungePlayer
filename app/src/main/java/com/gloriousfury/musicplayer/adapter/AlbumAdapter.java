package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.AlbumActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    Context context;
    private ArrayList<Albums> album_list;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String ALBUM_TITLE = "album_title";
    String ALBUM_ARTIST = "ALBUM_ARTIST";
    String ALBUM_ART_URI = "album_art_uri";
    int TYPE_ALPHABET = 1;
    int TYPE_ALBUM = 2;


    public AlbumAdapter(Context context, ArrayList<Albums> album_list) {
        this.context = context;
        this.album_list = album_list;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView album_title, artist, noOfSongs;
        ImageView song_background;
        RelativeLayout backgroundRelativeLayout;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
//            title = (TextView) view.findViewById(menu_item);

            album_title = (TextView) view.findViewById(R.id.album_name);
            artist = (TextView) view.findViewById(R.id.artist);
            song_background = (ImageView) view.findViewById(R.id.song_background);

//            noOfSongs = (TextView) view.findViewById(R.id.no_of_songs);
            backgroundRelativeLayout = (RelativeLayout) view.findViewById(R.id.rel_info_layout);


        }

        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();
            Intent openAlbumActivity = new Intent(context, AlbumActivity.class);
            openAlbumActivity.putExtra(ALBUM_TITLE, album_list.get(getAdapterPosition()).getAlbum());
            openAlbumActivity.putExtra(ALBUM_ARTIST, album_list.get(getAdapterPosition()).getArtist());
            openAlbumActivity.putExtra(ALBUM_ART_URI, album_list.get(getAdapterPosition()).getAlbumArtUriString());
            context.startActivity(openAlbumActivity);
        }
    }
//
//    public class AlphabetViewHolder extends ViewHolder implements View.OnClickListener {
//        TextView temp;
//        TextView album_title;
//
//        public AlphabetViewHolder(final View view) {
//            super(view);
//
//            view.setClickable(true);
//            view.setOnClickListener(this);
//            album_title = (TextView) view.findViewById(R.id.letter);
//
//
//        }
//
//
//        @Override
//        public void onClick(View v) {
//
//
//        }
//    }


//    public class AlbumViewHolder extends ViewHolder implements View.OnClickListener {
//        TextView album_title, artist,noOfSongs;
//        ImageView song_background;
//
//        public AlbumViewHolder(final View view) {
//            super(view);
//
//            view.setClickable(true);
//            view.setOnClickListener(this);
////            title = (TextView) view.findViewById(menu_item);
//
//            album_title = (TextView) view.findViewById(R.id.album_name);
//            artist = (TextView) view.findViewById(R.id.artist);
//            song_background = (ImageView) view.findViewById(R.id.song_background);
//
//            noOfSongs = (TextView) view.findViewById(R.id.no_of_songs);
//        }
//
//
//        @Override
//        public void onClick(View v) {
//
//            Intent openAlbumActivity = new Intent(context,AlbumActivity.class);
//            openAlbumActivity.putExtra(ALBUM_TITLE, album_list.get(getAdapterPosition()).getAlbum());
//            openAlbumActivity.putExtra(ALBUM_ARTIST, album_list.get(getAdapterPosition()).getArtist());
//            openAlbumActivity.putExtra(ALBUM_ART_URI, album_list.get(getAdapterPosition()).getAlbumArtUriString());
//            context.startActivity(openAlbumActivity);
//        }
//    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;


        switch (viewType) {

//            case 1:
//
//                v = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.album_header, parent, false);
//
//                return new AlphabetViewHolder(v);
//

            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_albums, parent, false);

                return new ViewHolder(v);

            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_albums, parent, false);

                return new ViewHolder(v);


        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        if (holder.getItemViewType() == TYPE_ALPHABET) {
//            AlphabetViewHolder Holder = (AlphabetViewHolder) holder;
            //  holder.temp.setText(mDataSet[position]);
            if (album_list.get(position).getAlbum() != null) {
//                Holder.album_title.setText(album_list.get(position).getAlbum());
            } else {

//                Holder.album_title.setText("#");
            }

        } else if (holder.getItemViewType() == TYPE_ALBUM) {
            ViewHolder Holder = (ViewHolder) holder;
            Holder.album_title.setText(album_list.get(position).getAlbum());
            Holder.artist.setText(album_list.get(position).getArtist());
//            Holder.noOfSongs.setText(album_list.get(position).getnoOfSongs() + " Songs");
            Bitmap bitmap = null;
            Uri albumArtUri = null;

            if (album_list.get(position).getAlbumArtUriString() != null) {

                albumArtUri = Uri.parse(album_list.get(position).getAlbumArtUriString());
                Picasso.with(context).load(albumArtUri).into(Holder.song_background);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        //work with the palette here
                        int colorInt = ContextCompat.getColor(context, R.color.white);
                        setViewSwatch(holder.backgroundRelativeLayout, palette.getMutedSwatch());
                    }
                });

//
//                bitmap = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_default_music_image));
//
//                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//                    @Override
//                    public void onGenerated(Palette palette) {
//                        //work with the palette here
//
//                        setViewSwatch(holder.backgroundRelativeLayout, palette.getDarkMutedSwatch());
//                    }
//                });

            } else {
                Picasso.with(context).load(R.drawable.ic_default_music_image).into(Holder.song_background);
                int brownColor = ContextCompat.getColor(context, R.color.darker_brown);
                holder.backgroundRelativeLayout.setBackgroundColor(brownColor);

            }
        }


    }


    public void setAudioListData(ArrayList<Albums> albumArraylist) {
        if (albumArraylist != null) {
            this.album_list = albumArraylist;
            notifyDataSetChanged();

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


    public static int getResId(String variableName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public void setViewSwatch(RelativeLayout view, Palette.Swatch swatch) {
        if (swatch != null) {
//            view.setTextColor(swatch.getTitleTextColor());
            view.setBackgroundColor(swatch.getRgb());
            view.setVisibility(View.VISIBLE);
        } else {
//            view.setVisibility(View.GONE);
        }
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}


