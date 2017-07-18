package com.gloriousfury.musicplayer.ui.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AlbumSongsAdapter;
import com.gloriousfury.musicplayer.adapter.SongNormalAdapter;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Artist;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Playlist;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class PlaylistActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.playlist_title)
    TextView playlistTitle;


    @BindView(R.id.no_of_songs)
    TextView noOfSongs;


//    @BindView(R.id.toolbar)
//    Toolbar toolbar;


//    @BindView(R.id.img_play_pause)
//    ImageView playPauseView;

    @BindView(R.id.song_background)
    ImageView songBackground;

//    @BindView(R.id.img_back_button)
//    ImageView btnGoBack;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    String PLAYLIST_ITEM = "playlist_item";

    ArrayList<Albums> albumAudioList = new ArrayList<>();
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
        setContentView(R.layout.activity_playlist_test);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        storage = new StorageUtil(this);
        mediaPlayerService = new MediaPlayerService(this);
//        btnGoBack.setOnClickListener(this);

        Bundle getPlaylistData = getIntent().getExtras();

        if (getPlaylistData != null) {
            Playlist singlePlaylist = getPlaylistData.getParcelable(PLAYLIST_ITEM);
            String playlist_name = singlePlaylist.getPlaylistTitle();
//            String album_art_uri = singlePlaylist.getAlbumArtUri();
            long playlist_id = singlePlaylist.getPlaylistId();

//            if (album_art_uri != null) {
//                Uri albumArtUri = Uri.parse(album_art_uri);
//                Picasso.with(this).load(albumArtUri).into(songBackground);
//                Toast.makeText(this, albumArtUri.toString(), Toast.LENGTH_LONG);
//            } else {
//                Toast.makeText(this, "Album Art is non existence", Toast.LENGTH_LONG);
//
//            }

            playlistTitle.setText(playlist_name);
            collapsingToolbarLayout.setTitle(playlist_name);
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

//            Toast.makeText(this, artist_id + " ", Toast.LENGTH_LONG).show();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(layoutManager);

            audioList = getPlayists(playlist_id);
            adapter = new AlbumSongsAdapter(this,audioList);

            recyclerView.setAdapter(adapter);


        }


    }


//    @OnClick(R.id.img_play_pause)
//    public void playPause() {
//        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
//
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            StorageUtil storage = new StorageUtil(this);
//            storage.storeAudio(albumAudioList);
//            storage.storeAudioIndex(0);
//
//            Intent playerIntent = new Intent(this, MediaPlayerService.class);
//            startService(playerIntent);
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        }
//        //check this for when a song is already playing and you launch an album
//        else {
//            //Store the new audioIndex to SharedPreferences
//            StorageUtil storage = new StorageUtil(this);
//            storage.storeAudio(albumAudioList);
//            storage.storeAudioIndex(0);
//
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            sendBroadcast(broadcastIntent);
//        }
//
//    }






    public ArrayList<Audio> getPlayists(long playlistId) {
        audioList = new ArrayList<>();
        // query external audio
        ContentResolver contentResolver = getContentResolver();

        Uri uri =MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                int duration = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);




                // Save to audioList
                audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()
                ));
            }
        }
        noOfSongs.setText(String.valueOf(audioList.size() +" Songs"));
        cursor.close();
        return audioList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
////        bus.register(this);
//
//
//        if (audioList != null && audioIndex != -1) {
//            audioList = storage.loadAudio();
//            audioIndex = storage.loadAudioIndex();
//            activeAudio = audioList.get(audioIndex);
//
////
////            if(audioList)
////            nextAudio = audioList.get(audioIndex + 1);
//
////            changeMiniPlayer(activeAudio, nextAudio);
//
//        }
//
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        bus.unregister(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        bus.unregister(this);
//    }


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
        if (audioList.size() >= 0) {
            for (int i = 0; i < audioList.size() - 1; i++) {

                duration += audioList.get(i).getDuration();
            }

        } else if (audioList.size() == 1) {
            duration = audioList.get(0).getDuration();
        }


        if (duration > 3600000) {
            String hms = String.format("%d Hr, %d mins", TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1));
//                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));

            return hms;
        } else {
            String hms = String.format("%d mins", TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1));
//
            return hms;
        }


    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(PlaylistActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back_button:
                onBackPressed();
                break;


        }


    }
}
