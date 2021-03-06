package com.gloriousfury.musicplayer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.utils.MusicController;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.utils.PlaybackStatus;
import com.gloriousfury.musicplayer.utils.Timer;
import com.gloriousfury.musicplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.gloriousfury.musicplayer.ui.activity.MainActivity.Broadcast_PLAY_NEW_AUDIO;

public class SingleSongActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, MediaController.MediaPlayerControl {

    @BindView(R.id.artist)
    TextView artist;

    @BindView(R.id.close_activity)
    ImageView closeActivity;
    @BindView(R.id.song_title)
    TextView songTitle;

    @BindView(R.id.img_play_pause)
    ImageView playPauseView;

    @BindView(R.id.song_background)
    ImageView songBackground;


    @BindView(R.id.songCurrentDuration)
    TextView songCurrentDuration;

    @BindView(R.id.songTotalDuration)
    TextView songTotalDuration;


    @BindView(R.id.img_rewind)
    ImageView rewindView;

    @BindView(R.id.img_fast_foward)
    ImageView fastFowardView;

    @BindView(R.id.shuffle)
    ImageView shuffleView;

    @BindView(R.id.repeat)
    ImageView repeatView;

    @BindView(R.id.songProgressBar)
    SeekBar seekBar;

    @BindView(R.id.queue_view)
    RelativeLayout queueLayout;

    String SONG_TITLE = "song_title";
    String SONG_ARTIST = "song_artist";
    String ALBUM_ART_URI = "song_album_art_uri";
    String TAG = "singlesong";
    Audio audio;
    String SONG = "single_audio";
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio; //an object of the currently playing audio
    StorageUtil storage;
    PlaybackStatus playbackStatus;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;
    MediaSessionCompat mediaSession;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    long totalDuration;
    long currentDuration;
    private MusicController controller;
    boolean serviceBound = true;
    EventBus bus = EventBus.getDefault();
    boolean shuffleState;
    String CLICK_CHECKER = "click_checker";
    String checker;
    long currentPosition;
    Intent responseIntent = new Intent();
    AppMainServiceEvent event = new AppMainServiceEvent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song_test);
        ButterKnife.bind(this);
        mediaPlayerService = MediaPlayerService.getMediaPlayerServiceInstance();
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
        songTitle.setSelected(true);
//        shuffleState = storage.getShuffleSettings();
        seekBar.setOnSeekBarChangeListener(this);


        // set Progress bar values
        seekBar.setProgress(0);
        seekBar.setMax(100);


        // Updating progress bar


        if (currentMediaPlayer.isPlaying()) {
            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));


        } else if (!currentMediaPlayer.isPlaying()) {
            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_play_circle_filled_white_black_24dp));

        }

        Intent getSongData = getIntent();
        Bundle getData = getSongData.getExtras();

        if (getData != null) {
            checker = getData.getString(CLICK_CHECKER);
            if (checker != null) {
                serviceBound = false;
                Utils.serviceBound = false;
                playPauseView.setImageDrawable(ContextCompat
                        .getDrawable(SingleSongActivity.this, R.drawable.ic_play_circle_filled_white_black_24dp));

            } else {
                Utils.serviceBound = true;
                serviceBound = true;
            }

            audio = getSongData.getParcelableExtra(SONG);
            activeAudio = audio;
            String song_title = audio.getTitle();
            String song_artist = audio.getArtist();
            totalDuration = audio.getDuration();
            Uri albumArtUri = null;
            if (audio.getAlbumArtUriString() != null) {
                albumArtUri = Uri.parse(audio.getAlbumArtUriString());
            }
            songTitle.setText(song_title);
            artist.setText(song_artist);
            if (albumArtUri != null) {

                Picasso.with(this).load(albumArtUri).into(songBackground);


            } else {
                Picasso.with(this).load(R.drawable.background_cover).into(songBackground);

            }

            storage = new StorageUtil(getApplicationContext());
            if (!currentMediaPlayer.isPlaying() && !serviceBound) {
                currentPosition = storage.loadPlayBackPosition();
                setSeekBarProgress(currentPosition);
                songCurrentDuration.setText(String.valueOf(Timer.milliSecondsToTimer(currentPosition)));
//                Toast.makeText(this, String.valueOf(currentPosition), Toast.LENGTH_LONG).show();
            } else {

                updateProgressBar();
            }

        } else {

            activeAudio = mediaPlayerService.getActiveAudio();
            updateMetaData(activeAudio);

        }


    }

    @OnClick(R.id.close_activity)
    public void closeActivity() {
        onBackPressed();
    }

    @OnClick(R.id.img_fast_foward)
    public void playNextSong() {
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        activeAudio = mediaPlayerService.getActiveAudio();
        shuffleState = storage.getShuffleSettings();
        if (shuffleState) {

            mediaPlayerService.shuffleToNext(audioList, audioIndex);
        } else {
            mediaPlayerService.skipToNext(audioList, audioIndex, this, currentMediaPlayer);
        }

    }


    @OnClick(R.id.img_rewind)
    public void playPreviousSong() {
        MediaPlayer currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        mediaPlayerService.skipToPrevious(audioList, audioIndex, this, currentMediaPlayer);


    }


    @OnClick(R.id.img_play_pause)
    public void playPause() {
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
        if (currentMediaPlayer.isPlaying()) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_play_circle_filled_white_black_24dp));

//            currentMediaPlayer.pause();

            mediaPlayerService.pauseMedia(currentMediaPlayer);
            mediaPlayerService.buildNotification(PlaybackStatus.PAUSED);
            responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, true);
            event.setMainIntent(responseIntent);
            event.setEventType(AppMainServiceEvent.PLAYBACK_CHANGE1);
            EventBus.getDefault().post(event);



//            mediaPlayerService.buildNotification(PlaybackStatus.PAUSED);
//            mediaPlayerService.changeNotificationState(PlaybackStatus.PAUSED);

        } else if (!currentMediaPlayer.isPlaying() && checker != null) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));
            audioIndex = storage.loadAudioIndex();
            currentPosition = storage.loadPlayBackPosition();
//            currentMediaPlayer.start();
//            mediaPlayerService.resumeMedia1(currentMediaPlayer, (int) currentPosition);
//            mediaPlayerService.changeNotificationState(PlaybackStatus.PLAYING);
//            mediaPlayerService.buildNotification(PlaybackStatus.PLAYING);
            seekBar.setProgress(0);
            seekBar.setMax(100);
            checker = "definately not null";
            // Updating progress bar
            updateProgressBar();
//            Utils utilInstance = new Utils(this);
//            utilInstance.playAudio(audioIndex);
//            playPauseView.setImageDrawable(ContextCompat
//                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));

//            // set Progress bar values
//            seekBar.setProgress(0);
//            seekBar.setMax(100);
//
//            // Updating progress bar
//            updateProgressBar();
        } else if (!currentMediaPlayer.isPlaying()) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));
//            currentMediaPlayer.start();
            mediaPlayerService.resumeMedia(currentMediaPlayer);
            mediaPlayerService.buildNotification(PlaybackStatus.PLAYING);
            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);
            responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, false);
            event.setMainIntent(responseIntent);
            event.setEventType(AppMainServiceEvent.PLAYBACK_CHANGE1);
            EventBus.getDefault().post(event);

            // Updating progress bar
            updateProgressBar();
        } else {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));
            mediaPlayerService.resumeMedia1(currentMediaPlayer,mediaPlayerService.getCurrentDur());
            // set Progress bar values
//            seekBar.setProgress(0);
//            seekBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        }


    }

    @OnClick(R.id.shuffle)
    public void shuffleSongs() {
        shuffleState = storage.getShuffleSettings();
        if (shuffleState) {
            shuffleView.setImageResource(R.drawable.ic_shuffle_black_24dp);
            storage.setShuffle(false);
        } else {
            shuffleView.setImageResource(R.drawable.ic_shuffle_pink_24dp);
            storage.setShuffle(true);
        }

    }

    @OnClick(R.id.queue_view)
    public void startQueueActivity() {

        Intent queueActivity = new Intent(this,QueueActivity.class);
        startActivity(queueActivity);


    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {


            long currentDuration = mediaPlayerService.getCurrentDur();
            setSeekBarProgress(currentDuration);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);


        }
    };

    public void setSeekBarProgress(long currentDuration) {
        long totalDuration = activeAudio.getDuration();


//            if (!currentMediaPlayer.isPlaying()) {
//                songCurrentDuration.setText("0.00");
//                Toast.makeText(SingleSongActivity.this, "I came here o", Toast.LENGTH_LONG).show();

//            } else {
        // Displaying Total Duration time
        songTotalDuration.setText(String.valueOf(Timer.milliSecondsToTimer(totalDuration)));
//                Toast.makeText(SingleSongActivity.this, "I didnt come here o", Toast.LENGTH_LONG).show();
//            }
        // Displaying time completed playing
        songCurrentDuration.setText(String.valueOf(Timer.milliSecondsToTimer(currentDuration)));

        // Updating progress bar
        int progress = (int) (Timer.getProgressPercentage(currentDuration, totalDuration));
        //Log.d("Progress", ""+progress);
        seekBar.setProgress(progress);

    }

    private void updateMetaData(Audio activeAudio) {
        seekBar.setProgress(0);
        seekBar.setMax(100);
//

        long totalDuration = activeAudio.getDuration();
        long currentDuration = mediaPlayerService.getCurrentDur();
        this.activeAudio = activeAudio;
//        // Displaying Total Duration time
        songTotalDuration.setText("" + Timer.milliSecondsToTimer(totalDuration));
//        // Displaying time completed playing
        songCurrentDuration.setText("" + Timer.milliSecondsToTimer(currentDuration));

        updateProgressBar();
//        mediaPlayer.setOnCompletionListener(this);

        String song_title = activeAudio.getTitle();
        String song_artist = activeAudio.getArtist();


        Uri albumArtUri = null;
        if (activeAudio.getAlbumArtUriString() != null) {
            albumArtUri = Uri.parse(activeAudio.getAlbumArtUriString());
            Log.e(TAG, activeAudio.getAlbumArtUriString());
        }
        if (albumArtUri != null) {

            Picasso.with(this).load(albumArtUri).into(songBackground);


        } else {
            Picasso.with(this).load(R.drawable.background_cover).into(songBackground);

        }
        songTitle.setText(song_title);
        artist.setText(song_artist);

    }

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = activeAudio.getDuration();
        int currentPosition = Timer.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        currentMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
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

    @Override
    public void pause() {
        mediaPlayerService.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayerService.seek(pos);
    }

    @Override
    public void start() {
        mediaPlayerService.go();
    }


    @Override
    public int getDuration() {
        if (mediaPlayerService != null &&
                serviceBound &&
                mediaPlayerService.isPng())
            return mediaPlayerService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayerService != null && serviceBound && mediaPlayerService.isPng())
            return mediaPlayerService.getPosn();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayerService != null && serviceBound)
            return mediaPlayerService.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (bus == null) {
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
        if (currentMediaPlayer.isPlaying()) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));
        } else {
            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_play_circle_filled_white_black_24dp));

        }


        bus.register(this);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        bus.unregister(this);
    }


    private void playOldAudio(int audioIndex) {
        //Check is service is active
        if (checker != null) {
            //Store Serializable audioList to SharedPreferences
            mediaPlayerService.playOldAudio();


        }
    }


    public void onEventMainThread(AppMainServiceEvent event) {
        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
        Intent i = event.getMainIntent();


        if (event.getEventType() == AppMainServiceEvent.ONCOMPLETED_RESPONSE) {
            if (i != null) {

                Audio recievedAudio = i.getParcelableExtra(AppMainServiceEvent.RESPONSE_DATA);
                updateMetaData(recievedAudio);
                Toast statu = Toast.makeText(this, "Came to the onComplete", Toast.LENGTH_LONG);

            } else {

                Toast statu = Toast.makeText(this, "Cant Retrieve data at the moment, Try again", Toast.LENGTH_LONG);
                statu.show();
            }


        } else if (event.getEventType() == AppMainServiceEvent.PLAYBACK_CHANGE) {
            if (i != null) {

                Log.e(TAG,"I came to singleactivity playback");

                boolean playbackStage = i.getBooleanExtra(AppMainServiceEvent.RESPONSE_DATA,false);
                Log.e(TAG,"I came to singleactivity playback" + playbackStage);
                changePlayPauseImage(playbackStage);

            } else {

                Toast statu = Toast.makeText(this, "Cant Retrieve data at the moment, Try again", Toast.LENGTH_LONG);
                statu.show();
            }


        }
    }


    public void changePlayPauseImage(boolean playbackStatus) {
        if (playbackStatus) {
            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));

        } else if (!playbackStatus) {
            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_play_circle_filled_white_black_24dp));
        } else {

        }


    }

}
