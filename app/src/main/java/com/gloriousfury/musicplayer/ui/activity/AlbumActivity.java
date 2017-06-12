package com.gloriousfury.musicplayer.ui.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AllSongsAdapter;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;

public class AlbumActivity extends AppCompatActivity {

    @BindView(R.id.artist)
    TextView artist;


    @BindView(R.id.album_name)
    TextView albumTitle;

    @BindView(R.id.img_play_pause)
    ImageView playPauseView;

    @BindView(R.id.song_background)
    ImageView songBackground;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    String ALBUM_TITLE = "album_title";
    String ALBUM_ARTIST = "ALBUM_ARTIST";

    ArrayList<Audio> albumAudioList;
    AllSongsAdapter adapter;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;
    StorageUtil storage;
    boolean serviceBound = false;
    private MediaPlayerService player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_album);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle getAlbumData = getIntent().getExtras();

        if (getAlbumData != null) {
            String AlbumTitle = getAlbumData.getString(ALBUM_TITLE);
            String AlbumArtist = getAlbumData.getString(ALBUM_ARTIST);

            albumTitle.setText(AlbumTitle);
            artist.setText(AlbumArtist);
            requestAlbumDetails(AlbumTitle);

            Toast.makeText(this, AlbumArtist + " " + AlbumTitle, Toast.LENGTH_LONG).show();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(layoutManager);


            adapter = new AllSongsAdapter(this, albumAudioList);


            recyclerView.setAdapter(adapter);


        }





    }


    @OnClick(R.id.img_play_pause)
    public void playPause() {
        currentMediaPlayer = MediaPlayerService.getMediaPlayerInstance();

        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(this);
            storage.storeAudio(albumAudioList);
            storage.storeAudioIndex(0);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        //check this for when a song is already playing and you launch an album
        else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(this);
            storage.storeAudio(albumAudioList);
            storage.storeAudioIndex(0);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }

    }

    private ArrayList<Audio> requestAlbumDetails(String AlbumName) {

        albumAudioList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {android.provider.MediaStore.Audio.Albums._ID,
                android.provider.MediaStore.Audio.Albums.ALBUM};

        Cursor cursor = contentResolver.query(
                uri, columns, null,
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {


                // I want to list down song in album Rolling Papers (Deluxe Version)

                String[] column = {MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                };

                String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";

                String whereVal[] = {AlbumName};

                String orderBy = android.provider.MediaStore.Audio.Media.TITLE;

                cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        column, where, whereVal, orderBy);

                if (cursor.moveToFirst()) {
                    do {
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));


                        Long albumId = cursor.getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        int duration = cursor.getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));


                        albumAudioList.add(new Audio(data, title, artist, duration, albumId));
                        Log.v("Vipul",
                                cursor.getString(cursor
                                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                    } while (cursor.moveToNext());
                }


                adapter = new AllSongsAdapter(this, albumAudioList);


                recyclerView.setAdapter(adapter);

            }
        }
        return albumAudioList;
    }



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(AlbumActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}
