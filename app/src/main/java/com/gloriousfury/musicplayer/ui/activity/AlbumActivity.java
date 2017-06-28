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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AlbumSongsAdapter;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;

public class AlbumActivity extends AppCompatActivity {


    @BindView(R.id.artist)
    TextView artist;


    @BindView(R.id.no_of_songs)
    TextView noOfSongs;

    @BindView(R.id.play_time)
    TextView playTime;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

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
    String ALBUM_ART_URI = "album_art_uri";

    ArrayList<Audio> albumAudioList;
    AlbumSongsAdapter adapter;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;
    StorageUtil storage;
    boolean serviceBound = false;
    private MediaPlayerService player;
    String TAG = "AlbumActivity";
    EventBus bus = EventBus.getDefault();
    ArrayList<Audio> audioList;
    private int audioIndex = -1;
    Audio activeAudio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_album);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Grunge Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storage = new StorageUtil(this);
        mediaPlayerService = new MediaPlayerService(this);


        Bundle getAlbumData = getIntent().getExtras();

        if (getAlbumData != null) {
            String AlbumTitle = getAlbumData.getString(ALBUM_TITLE);
            String AlbumArtist = getAlbumData.getString(ALBUM_ARTIST);
            String AlbumUri = getAlbumData.getString(ALBUM_ART_URI);

            if (AlbumUri != null) {
                Uri albumArtUri = Uri.parse(AlbumUri);
                Picasso.with(this).load(albumArtUri).into(songBackground);
                Toast.makeText(this, albumArtUri.toString(), Toast.LENGTH_LONG);
            } else {
                Toast.makeText(this, "Album Art is non existence", Toast.LENGTH_LONG);

            }
            albumTitle.setText(AlbumTitle);
            artist.setText(AlbumArtist);

            requestAlbumDetails(AlbumTitle);

            Toast.makeText(this, AlbumArtist + " " + AlbumTitle, Toast.LENGTH_LONG).show();
//            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(layoutManager);


            adapter = new AlbumSongsAdapter(this, albumAudioList);


            recyclerView.setAdapter(adapter);


        }


    }


    @OnClick(R.id.img_play_pause)
    public void playPause() {
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

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


                adapter = new AlbumSongsAdapter(this, albumAudioList);

                if(albumAudioList.size()>1) {
                    noOfSongs.setText(String.valueOf(albumAudioList.size()) + " Songs");


                }else{
                    noOfSongs.setText(String.valueOf(albumAudioList.size()) + " Song");


                }
                playTime.setText(getPlayTime(albumAudioList));


                recyclerView.setAdapter(adapter);

            }
        }
        return albumAudioList;
    }


    public void onEventMainThread(AppMainServiceEvent event) {
        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
        Intent i = event.getMainIntent();


        if (event.getEventType() == AppMainServiceEvent.ONCOMPLETED_RESPONSE) {
            if (i != null) {

//                Audio recievedAudio = i.getParcelableExtra(AppMainServiceEvent.RESPONSE_DATA);
//
//                audioList = storage.loadAllAudio();

                Toast statu = Toast.makeText(this, "I came here, just so you know", Toast.LENGTH_LONG);
                statu.show();
                audioIndex = storage.loadAudioIndex();
//                changeAdapterData();

//                adapter = new AlbumSongsAdapter(this, albumAudioList, audioIndex);
//
//
//                recyclerView.setAdapter(adapter);
//
//

                Toast.makeText(this, String.valueOf(audioIndex), Toast.LENGTH_LONG).show();
                adapter.setAdapterData(audioIndex);


            } else {

                Toast statu = Toast.makeText(this, "Cant Retrieve data at the moment, Try again", Toast.LENGTH_LONG);
                statu.show();
            }


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);


        if (audioList != null && audioIndex != -1) {
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();
            activeAudio = audioList.get(audioIndex);

//
//            if(audioList)
//            nextAudio = audioList.get(audioIndex + 1);

//            changeMiniPlayer(activeAudio, nextAudio);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeAdapterData() {

        recyclerView.setVisibility(View.INVISIBLE);


    }

    public String getPlayTime(ArrayList<Audio> audioList) {

        int duration = 0;
        for (int i = 0; i < audioList.size() - 1; i++) {

            duration += audioList.get(i).getDuration();
        }

        if(duration>3600000) {
            String hms = String.format("%d Hr, %d mins", TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1));
//                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));

            return hms;
        }else{
            String hms = String.format("%d mins",  TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1));
//
            return  hms;
        }


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
