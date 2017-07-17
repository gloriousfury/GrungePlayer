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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.gloriousfury.musicplayer.ui.fragment.AlbumsFragment;
import com.gloriousfury.musicplayer.ui.fragment.AllSongsFragment;
import com.gloriousfury.musicplayer.ui.fragment.BasicFragment;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;

public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {


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

    @BindView(R.id.img_back_button)
    ImageView btnGoBack;


//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;

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
    ArtistDetailsSectionPagerAdapter detailsSectionPagerAdpter;
    TabLayout tablayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_testing);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Grunge Player");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storage = new StorageUtil(this);
        mediaPlayerService = new MediaPlayerService(this);
        btnGoBack.setOnClickListener(this);

        tablayout = (TabLayout) findViewById(R.id.artist_details_tab);
        viewPager = (ViewPager) findViewById(R.id.artist_details_container);


        detailsSectionPagerAdpter = new ArtistDetailsSectionPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(detailsSectionPagerAdpter);

        tablayout.setupWithViewPager(viewPager);



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

//            requestAlbumDetails(artist_id, artist_name);
//
//            Toast.makeText(this, artist_id + " ", Toast.LENGTH_LONG).show();
//            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//            recyclerView.setHasFixedSize(true);
//
//            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//
//            recyclerView.setLayoutManager(layoutManager);
//
//
//            adapter = new SongNormalAdapter(this, albumAudioList);
//
//
//            recyclerView.setAdapter(adapter);


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

    private ArrayList<Albums> requestAlbumDetails(long Id, String artistName) {

        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String artist_name = MediaStore.Audio.Albums.ARTIST;

        final String[] projection = {_id, albumart, artist_name, album_name, tracks};
//
        String where = MediaStore.Audio.Albums.ARTIST + "=?";

        String whereVal[] = {artistName};

        String orderBy = MediaStore.Audio.Albums.ALBUM;


        Uri uri1 = MediaStore.Audio.Artists.Albums.getContentUri("external", Id);
        Cursor cursor = getContentResolver().query(uri1, projection, where,
                whereVal, MediaStore.Audio.Albums.ARTIST + " ASC");
//
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(album_name));
                Long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(_id));
                String album_artist = cursor.getString(cursor.getColumnIndex(artist_name));
//               int album_art = cursor.getInt(cursor.getColumnIndex(artist));
                int noOf_Tracks = cursor.getInt(cursor.getColumnIndex(tracks));

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                String album_art_string = albumArtUri.toString();


                albumAudioList.add(new Albums(album,album, noOf_Tracks, albumId, album_art_string));


//                Toast.makeText(this, String.valueOf(albumAudioList.size()) + " ", Toast.LENGTH_SHORT);
//
//                    }

            }
        }

//        adapter.setAlbumListData(albumAudioList);

        noOfAlbums.setText(String.valueOf(albumAudioList.size()) + " Albums");
        cursor.close();


        return albumAudioList;
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


    private class ArtistDetailsSectionPagerAdapter extends FragmentPagerAdapter {

        private ArtistDetailsSectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AlbumsFragment.newInstance();
            } else if (position == 1) {
                return BasicFragment.newInstance();
            } else if (position == 2) {
                return AllSongsFragment.newInstance();
            } else {
                return BasicFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Albums";
                case 1:
                    return "Bio";
                case 2:
                    return "Songs";

            }
            return null;
        }
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

//        recyclerView.setVisibility(View.INVISIBLE);


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back_button:
                onBackPressed();
                break;


        }


    }
}
