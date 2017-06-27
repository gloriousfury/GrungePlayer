package com.gloriousfury.musicplayer.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.ui.fragment.ScrollFragmentContainer;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class LibraryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<ArrayList<Audio>> {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ArrayList<Audio> audioList;
    private int audioIndex = -1;
    ArrayList<Albums> albumList;
    ArrayList<Audio> retrievedAudioList = new ArrayList<>();
    ArrayList<Albums> retrievedAlbumsList = new ArrayList<>();
    StorageUtil storage;

    Audio activeAudio, nextAudio;
    private static final int TASK_LOADER_ID = 0;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_MEDIA_PERMISSION = 2;
    boolean serviceBound = true;
    EventBus bus = EventBus.getDefault();
    String TAG = "LibraryActivity";

    @BindView(R.id.artist)
    TextView artist;

//
//    @BindView(R.id.next_artist)
//    TextView nextArtist;


    @BindView(R.id.song_title)
    TextView songTitle;

//    @BindView(R.id.next_song_title)
//    TextView  nextSongTitle;

    @BindView(R.id.img_play_pause)
    ImageView playPauseView;

    @BindView(R.id.song_background)
    ImageView songBackground;
//
//    @BindView(R.id.next_artist_view)
//    RelativeLayout nextSongView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.MEDIA_CONTENT_CONTROL) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL},
                    REQUEST_MEDIA_PERMISSION);


        }


        storage = new StorageUtil(this);
        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new ScrollFragmentContainer()).commit();
//
//
//        if (audioList != null && audioIndex != -1) {
//            activeAudio = audioList.get(audioIndex);
//
//            nextAudio = audioList.get(audioIndex + 1);
//
//            changeMiniPlayer(activeAudio, nextAudio);
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    @Override
    public Loader<ArrayList<Audio>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ArrayList<Audio>>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor cursor = null;


            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                retrievedAudioList = storage.loadAllAudio();
                retrievedAlbumsList = storage.loadAllAlbums();
//                Toast.makeText(LibraryActivity.this, String.valueOf(retrievedAudioList.size()), Toast.LENGTH_LONG).show();

                if (retrievedAudioList != null && retrievedAlbumsList != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(retrievedAudioList);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public ArrayList<Audio> loadInBackground() {
                // Will implement to load data
                getListOfAlbums();
                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data
                ContentResolver contentResolver = getContentResolver();

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
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


                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), albumArtUri);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                            bitmap = BitmapFactory.decodeResource(getResources(),
                                    R.mipmap.ic_launcher);
                        } catch (IOException e) {

                            e.printStackTrace();
                        }


                        // Save to audioList
                        audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()));

                    }
                }


                return audioList;
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(ArrayList<Audio> data) {

                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Audio>> loader, ArrayList<Audio> data) {
        storage.storeAllAudio(data);
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Audio>> loader) {
        audioList = null;
    }


    public ArrayList<Albums> getListOfAlbums() {

        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = {_id, album_name, artist, albumart, tracks};
        Cursor cursor = getContentResolver().query(uri, columns, where,
                null, null);


        // add playlsit to

        if (cursor != null && cursor.getCount() > 0) {

            albumList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(album_name));
                Long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(_id));
                String album_artist = cursor.getString(cursor.getColumnIndex(artist));
//               int album_art = cursor.getInt(cursor.getColumnIndex(artist));
                int noOfTracks = cursor.getInt(cursor.getColumnIndex(tracks));

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);


                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), albumArtUri);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher);
                } catch (IOException e) {

                    e.printStackTrace();
                }


                // Save to audioList
                albumList.add(new Albums(album, album_artist, noOfTracks, albumId, albumArtUri.toString()));

            }
        }


        storage.storeAllAlbums(albumList);

        cursor.close();

        return albumList;
    }


    public ArrayList<AlbumLists> prepareData(ArrayList<Albums> album_list) {

        ArrayList<AlbumLists> exampleList = new ArrayList<>();

        String[] title = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (int i = 0; i < title.length; i++) {
            String alphabet = title[i];


            AlbumLists exampleItem = new AlbumLists();


            ArrayList<Albums> newArrayList = new ArrayList<>();
            ArrayList<Albums> ashArrayList = new ArrayList<>();
            for (int k = 0; k < album_list.size(); k++) {

                if (album_list.get(k).getAlbum() != null) {
                    String firstLetter = album_list.get(k).getAlbum().substring(0, 1);


                    Albums newAlbums;

                    if (firstLetter.equalsIgnoreCase(alphabet)) {
                        newAlbums = album_list.get(k);
                        newArrayList.add(newAlbums);
                    } else {
                        newAlbums = album_list.get(k);
                        ashArrayList.add(newAlbums);

                    }
                }

            }
            exampleItem.setAlbums(newArrayList);
            exampleItem.setAlphabet(title[i]);

            exampleList.add(exampleItem);


        }

        return exampleList;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }else  if(id == R.id.cart){
//            Intent cartActivity = new Intent(this, CartActivity.class);
//            startActivity(cartActivity);
//
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onEventMainThread(AppMainServiceEvent event) {
        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
        Intent i = event.getMainIntent();


        if (event.getEventType() == AppMainServiceEvent.ONCOMPLETED_RESPONSE) {
            if (i != null) {

                Audio recievedAudio = i.getParcelableExtra(AppMainServiceEvent.RESPONSE_DATA);
                updateMetaData(recievedAudio);

            } else {

                Toast statu = Toast.makeText(this, "Cant Retrieve data at the moment, Try again", Toast.LENGTH_LONG);
                statu.show();
            }


        }
    }

    private void updateMetaData(Audio recievedAudio) {

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();

        songTitle.setText(recievedAudio.getTitle());
        artist.setText(recievedAudio.getArtist());

        Uri albumArtUri = Uri.parse(recievedAudio.getAlbumArtUriString());

        if (albumArtUri != null) {

            Picasso.with(this).load(albumArtUri).into(songBackground);


        }


    }

//    public void changeMiniPlayer(Audio activeAudio, Audio nextAudio) {
//
//
//        songTitle.setText(activeAudio.getTitle());
//        artist.setText(activeAudio.getArtist());
//
//        Uri albumArtUri = Uri.parse(activeAudio.getAlbumArtUriString());
//
//        if (albumArtUri != null) {
//
//            Picasso.with(this).load(albumArtUri).into(songBackground);
//
//
//        }


//        nextSongTitle.setText(nextAudio.getTitle());
//        nextArtist.setText(nextAudio.getArtist());
//
//        Uri nextAlbumArtUri = Uri.parse(nextAudio.getAlbumArtUriString());

//        if (albumArtUri != null) {
//
//            Picasso.with(this).load(albumArtUri).into(songBackground);
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


}
