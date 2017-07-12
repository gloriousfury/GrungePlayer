package com.gloriousfury.musicplayer.ui.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;

public class ArtistActivity extends AppCompatActivity {


    @BindView(R.id.artist)
    TextView artist;


    @BindView(R.id.no_of_albums)
    TextView noOfAlbums;


//    @BindView(R.id.toolbar)
//    Toolbar toolbar;


//    @BindView(R.id.img_play_pause)
//    ImageView playPauseView;

    @BindView(R.id.song_background)
    ImageView songBackground;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    String ARTIST_ITEM = "artist_item";

    ArrayList<Albums> albumAudioList = new ArrayList<>();
    SongNormalAdapter adapter;
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
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Grunge Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storage = new StorageUtil(this);
        mediaPlayerService = new MediaPlayerService(this);


        Bundle getArtistData = getIntent().getExtras();

        if (getArtistData != null) {
            Artist singleArtist = getArtistData.getParcelable(ARTIST_ITEM);
            String artist_name = singleArtist.getArtistName();
            String album_art_uri = singleArtist.getAlbumArtUri();
            long artist_id = singleArtist.getArtistId();

            if (album_art_uri != null) {
                Uri albumArtUri = Uri.parse(album_art_uri);
                Picasso.with(this).load(albumArtUri).into(songBackground);
                Toast.makeText(this, albumArtUri.toString(), Toast.LENGTH_LONG);
            } else {
                Toast.makeText(this, "Album Art is non existence", Toast.LENGTH_LONG);

            }

            artist.setText(artist_name);

            requestAlbumDetails(artist_id);

//            Toast.makeText(this, AlbumArtist + " " + AlbumTitle, Toast.LENGTH_LONG).show();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(layoutManager);


            adapter = new SongNormalAdapter(this, albumAudioList);


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

    private ArrayList<Albums> requestAlbumDetails(long Id) {

        albumAudioList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Artists.Albums.getContentUri("external", Id);
        String[] columns = {MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM};

        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String artist_name = MediaStore.Audio.Albums.ARTIST;
        String album_art_string = null;
//
//
        final String[] projection = {_id, albumart, album_name, tracks};

        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {


                // I want to list down song in album Rolling Papers (Deluxe Version)

                String album = cursor.getString(cursor.getColumnIndex(album_name));
                Long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(_id));
                String album_artist = cursor.getString(cursor.getColumnIndex(artist_name));
//               int album_art = cursor.getInt(cursor.getColumnIndex(artist));
                int noOf_Tracks = cursor.getInt(cursor.getColumnIndex(tracks));

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                album_art_string = albumArtUri.toString();


                albumAudioList.add(new Albums(album, album_artist, noOf_Tracks, albumId, album_art_string));
            }


        }

        adapter.setAlbumListData(albumAudioList);
        return albumAudioList;
    }


//    public void onEventMainThread(AppMainServiceEvent event) {
//        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
//        Intent i = event.getMainIntent();
//
//
//        if (event.getEventType() == AppMainServiceEvent.ONCOMPLETED_RESPONSE) {
//            if (i != null) {
//
////                Audio recievedAudio = i.getParcelableExtra(AppMainServiceEvent.RESPONSE_DATA);
////
////                audioList = storage.loadAllAudio();
//
//                Toast statu = Toast.makeText(this, "I came here, just so you know", Toast.LENGTH_LONG);
//                statu.show();
//                audioIndex = storage.loadAudioIndex();
////                changeAdapterData();
//
////                adapter = new AlbumSongsAdapter(this, albumAudioList, audioIndex);
////
////
////                recyclerView.setAdapter(adapter);
////
////
//
//                Toast.makeText(this, String.valueOf(audioIndex), Toast.LENGTH_LONG).show();
//                adapter.setAdapterData(audioIndex);
//
//
//            } else {
//
//                Toast statu = Toast.makeText(this, "Cant Retrieve data at the moment, Try again", Toast.LENGTH_LONG);
//                statu.show();
//            }
//
//
//        }
//    }


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

            Toast.makeText(ArtistActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}
