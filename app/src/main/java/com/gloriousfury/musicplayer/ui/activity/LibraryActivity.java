package com.gloriousfury.musicplayer.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.ui.fragment.ScrollFragmentContainer;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.utils.Timer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class LibraryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<ArrayList<Audio>>, View.OnClickListener, GestureDetector.OnGestureListener {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ArrayList<Audio> audioList;
    private int audioIndex = -1;
    ArrayList<Albums> albumList;
    ArrayList<Audio> retrievedAudioList = new ArrayList<>();
    ArrayList<Albums> retrievedAlbumsList = new ArrayList<>();
    StorageUtil storage;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;
    Audio activeAudio, nextAudio;
    private static final int TASK_LOADER_ID = 0;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_MEDIA_PERMISSION = 2;
    boolean serviceBound = true;
    EventBus bus = EventBus.getDefault();
    String TAG = "LibraryActivity";
    int totalDuration;
    long currentDuration;
    private Handler mHandler = new Handler();
    String SONG = "single_audio";
    Intent responseIntent = new Intent();
    AppMainServiceEvent event = new AppMainServiceEvent();

    @BindView(R.id.artist)
    TextView artist;


    @BindView(R.id.song_title)
    TextView songTitle;


//    @BindView(R.id.img_fast_foward)
//    ImageView nextSong;

//    @BindView(R.id.img_rewind)
//    ImageView previousSong;


    @BindView(R.id.img_play_pause)
    ImageView playPauseView;
    @BindView(R.id.relative_layout_mini_player)
    LinearLayout miniPlayerView;

    @BindView(R.id.queuelist_view)
    RecyclerView queueRecycler;

    @BindView(R.id.song_background)
    ImageView songBackground;
    @BindView(R.id.songProgressBar)
    SeekBar seekBar;
    GestureDetector gestureScanner;
    boolean expanded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        gestureScanner = new GestureDetector(this);
        storage = new StorageUtil(this);
        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        mediaPlayerService = new MediaPlayerService(this);
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
        songBackground.setOnClickListener(this);
        playPauseView.setOnClickListener(this);
//        nextSong.setOnClickListener(this);
//        previousSong.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new ScrollFragmentContainer()).commit();

        miniPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureScanner.onTouchEvent(event);
            }
        });
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

        if (checkPermissions()) {
            startTasks();

        } else {
            checkPermissions();

        }
    }

    private void startTasks() {

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    public boolean checkPermissions() {

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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL) ==
                PackageManager.PERMISSION_GRANTED
        )) {
            startTasks();
            return true;

        } else {
            return false;
        }

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
                String album_art_string = null;
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

                        album_art_string = albumArtUri.toString();
                        ContentResolver cr = getContentResolver();
                        String[] projectionArt = {MediaStore.MediaColumns.DATA};
                        Cursor cur = cr.query(Uri.parse(album_art_string), projectionArt, null, null, null);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                String filePath = cur.getString(0);

                                if (new File(filePath).exists()) {
                                    album_art_string = albumArtUri.toString();
                                    // do something if it exists
                                } else {
                                    album_art_string = null;
                                    // File was not found
                                }
                            } else {
                                album_art_string = null;
                                // Uri was ok but no entry found.
                            }
                            cur.close();
                        } else {
                            album_art_string = null;
                            // content Uri was invalid or some other error occurred
                        }


//
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(
//                                    getContentResolver(), albumArtUri);
//                            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
//
//                        } catch (FileNotFoundException exception) {
//                            exception.printStackTrace();
//                            bitmap = BitmapFactory.decodeResource(getResources(),
//                                    R.mipmap.ic_launcher);
//                        } catch (IOException e) {
//
//                            e.printStackTrace();
//                        }


                        // Save to audioList
                        audioList.add(new Audio(data, title, album, artist, duration, albumId, album_art_string));

                    }
                }

                cursor.close();
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


//        responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, activeAudio);
        Log.d(TAG, "I sent in sometihing here");
        event.setMainIntent(responseIntent);
        event.setEventType(AppMainServiceEvent.ONDATALOADED);
        EventBus.getDefault().post(event);

    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Audio>> loader) {
        audioList = null;
    }


    public ArrayList<Albums> getListOfAlbums() {

        String where = null;
        String album_art_string = null;
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
                album_art_string = albumArtUri.toString();


                ContentResolver cr = getContentResolver();
                String[] projectionArt = {MediaStore.MediaColumns.DATA};
                Cursor cur = cr.query(Uri.parse(album_art_string), projectionArt, null, null, null);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        String filePath = cur.getString(0);

                        if (new File(filePath).exists()) {
                            album_art_string = albumArtUri.toString();
                            // do something if it exists
                        } else {
                            album_art_string = null;
                            // File was not found
                        }
                    } else {
                        album_art_string = null;
                        // Uri was ok but no entry found.
                    }
                    cur.close();
                } else {
                    // content Uri was invalid or some other error occurred
                }


//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(
//                            getContentResolver(), albumArtUri);
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
//
//                } catch (FileNotFoundException exception) {
//                    exception.printStackTrace();
//                    bitmap = BitmapFactory.decodeResource(getResources(),
//                            R.mipmap.ic_launcher);
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }


                // Save to audioList
                albumList.add(new Albums(album, album_artist, noOfTracks, albumId, album_art_string));

            }
        }


        storage.storeAllAlbums(albumList);

        cursor.close();

        return albumList;
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

        activeAudio = recievedAudio;
        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();

        songTitle.setText(recievedAudio.getTitle());
        artist.setText(recievedAudio.getArtist());


        Log.d(TAG, recievedAudio.getAlbumArtUriString());
        seekBar.setProgress(0);
        seekBar.setMax(100);
        seekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

//                 Updating progress bar
        updateProgressBar();

        Uri albumArtUri = Uri.parse(recievedAudio.getAlbumArtUriString());

        if (albumArtUri != null) {

            Picasso.with(this).load(albumArtUri).into(songBackground);


        }


    }

    public void changeMiniPlayer(Audio activeAudio) {
        if (isPlaying()) {
            totalDuration = activeAudio.getDuration();
            playPauseView.setImageResource(R.drawable.ic_pause_black_24dp);
//            int currentPosition = Timer.progressToTimer(seekBar.getProgress(), totalDuration);
//
//            // forward or backward to certain seconds
//            currentMediaPlayer.seekTo(currentPosition);

            updateProgressBar();

        } else if (!isPlaying() && serviceBound) {
            playPauseView.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        }

        songTitle.setText(activeAudio.getTitle());
        artist.setText(activeAudio.getArtist());
        Uri albumArtUri = null;
        if (activeAudio.getAlbumArtUriString() != null) {
            albumArtUri = Uri.parse(activeAudio.getAlbumArtUriString());
        }
        if (albumArtUri != null) {

            Picasso.with(this).load(albumArtUri).into(songBackground);


        }


    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

//            long totalDuration = mediaPlayerService.getDur();
            long currentDuration = mediaPlayerService.getCurrentDur();
            // Updating progress bar
            int progress = (int) (Timer.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        if (audioList == null) {
            miniPlayerView.setVisibility(View.GONE);
            miniPlayerView.invalidate();
        } else if (audioList != null && audioIndex != -1) {
            if (audioList.size() == 0) {
                miniPlayerView.setVisibility(View.GONE);
                miniPlayerView.invalidate();
            } else {
                miniPlayerView.setVisibility(View.VISIBLE);
                audioList = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                activeAudio = audioList.get(audioIndex);

                changeMiniPlayer(activeAudio);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioList == null) {
            miniPlayerView.setVisibility(View.GONE);
            miniPlayerView.invalidate();
        } else if (audioList != null && audioIndex != -1) {
            if (audioList.size() == 0) {
                miniPlayerView.setVisibility(View.GONE);
                miniPlayerView.invalidate();
            } else {
                miniPlayerView.setVisibility(View.VISIBLE);
                audioList = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                activeAudio = audioList.get(audioIndex);

                changeMiniPlayer(activeAudio);
            }

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_play_pause:
                currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
                if (mediaPlayerService.isPng()) {

                    playPauseView.setImageDrawable(ContextCompat
                            .getDrawable(LibraryActivity.this, R.drawable.ic_play_arrow_black_24dp));

                    mediaPlayerService.pauseMedia(currentMediaPlayer);

                } else if (!mediaPlayerService.isPng()) {

                    if (!serviceBound) {
                        playPauseView.setImageDrawable(ContextCompat
                                .getDrawable(LibraryActivity.this, R.drawable.ic_pause_black_24dp));
                        mediaPlayerService.resumeMedia(currentMediaPlayer);

                        //TODO BUILD EXTERNAL PLAYER

                        serviceBound = true;
                    } else {
                        playPauseView.setImageDrawable(ContextCompat
                                .getDrawable(LibraryActivity.this, R.drawable.ic_pause_black_24dp));
                        mediaPlayerService.resumeMedia(currentMediaPlayer);

                    }
                }
//                 set Progress bar values


                break;

            case R.id.img_fast_foward:

//                currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

                audioList = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                mediaPlayerService.skipToNext(audioList, audioIndex, this, currentMediaPlayer);
                break;

            case R.id.img_rewind:

//             currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

                audioList = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                mediaPlayerService.skipToPrevious(audioList, audioIndex, this, currentMediaPlayer);
                break;

            case R.id.song_background:

//                Intent openSingleSongActivity = new Intent(this, SingleSongActivity.class);
//                openSingleSongActivity.putExtra(SONG, activeAudio);
//
//                startActivity(openSingleSongActivity);
//                Toast.makeText(this, "I just don't want to respond",Toast.LENGTH_LONG).show();
                break;

        }
    }


    public boolean isPlaying() {

        if (mediaPlayerService != null && serviceBound)
            return mediaPlayerService.isPng();
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.i("MiniPlayer view", "The onDown override was called");
        if (expanded) {
            queueRecycler.animate()
                    .translationY(0)
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            queueRecycler.setVisibility(View.GONE);

                        }
                    });
            expanded =false;


        }

        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

        Log.i("MiniPlayer view", "The long press override was called");
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        Log.i("MiniPlayer view", "The fling override was called");

        if (!expanded) {
            queueRecycler.setVisibility(View.VISIBLE);
            queueRecycler.setAlpha(0.0f);

// Start the animation
            queueRecycler.animate()
                    .translationY(queueRecycler.getHeight())
                    .alpha(1.0f);
            expanded = true;
        }


        return false;
    }

    //Very important to the fling event
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        handled = gestureScanner.onTouchEvent(ev);
        return handled;
    }
}
