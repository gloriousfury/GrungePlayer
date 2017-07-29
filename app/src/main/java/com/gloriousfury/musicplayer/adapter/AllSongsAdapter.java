package com.gloriousfury.musicplayer.adapter;

/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.ui.activity.LibraryActivity;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.gloriousfury.musicplayer.utils.Timer;
import com.gloriousfury.musicplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;
import static com.gloriousfury.musicplayer.ui.fragment.AllSongsFragment.actionMode;


public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.ViewHolder> {
    Context context;
    private ArrayList<Audio> song_list;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String SONG = "single_audio";
    String CLICK_CHECKER = "click_checker";
    int TYPE_HEADER = 1;
    private SparseBooleanArray selectedItems;

    public AllSongsAdapter(Context context, ArrayList<Audio> song_list) {
        this.context = context;
        this.song_list = song_list;
        selectedItems = new SparseBooleanArray();

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

            if (actionMode == null) {


                if (Utils.getInstance() != null) {
                    Utils.getInstance().playAudio(getAdapterPosition(), song_list);

                } else {
                    new Utils(context).playAudio(getAdapterPosition(), song_list);
                }
                int adapterposition = getAdapterPosition();
                String checker = null;
                Audio singleSong = song_list.get(adapterposition);
//            ((SingleRecipeActivity) context).onDescriptionSelected(stepParameters, getAdapterPosition());
                Intent openSingleSongActivity = new Intent(context, SingleSongActivity.class);
                openSingleSongActivity.putExtra(CLICK_CHECKER, checker);
                openSingleSongActivity.putExtra(SONG, singleSong);

                context.startActivity(openSingleSongActivity);

            }
        }


    }


    public class HeaderViewHolder extends ViewHolder implements View.OnClickListener {
        TextView temp;
        TextView album_title;

        public HeaderViewHolder(final View view) {
            super(view);

            view.setClickable(true);
            view.setOnClickListener(this);
//            album_title = (TextView) view.findViewById(R.id.letter);


        }


        @Override
        public void onClick(View v) {


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_allsongs, parent, false);
//        return new ViewHolder(view);

        View v;


        switch (viewType) {

            case 1:

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shuffle_header, parent, false);

                return new HeaderViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_allsongs, parent, false);

                return new ViewHolder(v);

        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder Holder = (HeaderViewHolder) holder;
            //  holder.temp.setText(mDataSet[position]);

        } else {
            holder.song_title.setText(song_list.get(position).getTitle());
            holder.artist.setText(song_list.get(position).getArtist());


            int backgroundColor = (ContextCompat.getColor(context, R.color.colorPrimaryDark));


            String duration = String.valueOf(Timer.milliSecondsToTimer(song_list.get(position).getDuration()));
            holder.duration.setText(duration);
            if (song_list.get(position).getAlbumArtUriString() != null) {
                Uri albumArtUri = Uri.parse(song_list.get(position).getAlbumArtUriString());
                Picasso.with(context).load(albumArtUri).resize(120, 120).into(holder.song_background);
            } else {
                Picasso.with(context).load(R.drawable.ic_default_music_option2).into(holder.song_background);

            }

            holder.itemView.setActivated(selectedItems.get(position, false));

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

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (song_list.get(position).getTitle().contentEquals("Header")) {
            viewType = TYPE_HEADER;
        } else {
            viewType = 2;
        }

        return viewType;
    }

    public void setAudioListData(ArrayList<Audio> songArraylist) {
        if (songArraylist != null) {
            this.song_list = songArraylist;
            notifyDataSetChanged();

        }

    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
//            player = binder.getService();
//            serviceBound = true;
//           Utils.serviceBound =true;
//
//
//            Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            serviceBound = false;
//        }
//    };


//    private void playAudio(int audioIndex) {
//        //Check is service is active
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            StorageUtil storage = new StorageUtil(context);
//            storage.storeAudio(song_list);
//            storage.storeAudioIndex(audioIndex);
//
////            Toast.makeText(context, String.valueOf(storage.loadAudioIndex()), Toast.LENGTH_LONG).show();
//            Intent playerIntent = new Intent(context, MediaPlayerService.class);
//            context.startService(playerIntent);
//            context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        } else {
//            //Store the new audioIndex to SharedPreferences
//            StorageUtil storage = new StorageUtil(context);
//            storage.storeAudioIndex(audioIndex);
//
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            context.sendBroadcast(broadcastIntent);
//        }
//    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }



}


