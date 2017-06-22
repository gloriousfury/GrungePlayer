package com.gloriousfury.musicplayer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
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
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class SingleSongActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, MediaPlayer.OnErrorListener, MediaController.MediaPlayerControl {

    @BindView(R.id.artist)
    TextView artist;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);
        ButterKnife.bind(this);
        mediaPlayerService = new MediaPlayerService(this);
        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

        seekBar.setOnSeekBarChangeListener(this);



        // set Progress bar values
        seekBar.setProgress(0);
        seekBar.setMax(100);

        Toast.makeText(this, String.valueOf(mediaPlayerService.getCurrentDur()),Toast.LENGTH_LONG).show();

        // Updating progress bar
        updateProgressBar();
        setController();

//        if (currentMediaPlayer.isPlaying()) {
//            playPauseView.setImageDrawable(ContextCompat
//                    .getDrawable(SingleSongActivity.this,R.drawable.ic_pause_circle_filled_black_24dp));
//
//
//        } else if (!currentMediaPlayer.isPlaying()) {
//            playPauseView.setImageDrawable(ContextCompat
//                    .getDrawable(SingleSongActivity.this,R.drawable.ic_play_circle_filled_white_black_24dp));
//
//        }

        Intent getSongData = getIntent();

        if (getSongData != null) {

            audio = getSongData.getParcelableExtra(SONG);
            String song_title = audio.getTitle();
            String song_artist = audio.getArtist();

            Uri albumArtUri = Uri.parse(audio.getAlbumArtUriString());


            songTitle.setText(song_title);
            artist.setText(song_artist);
            if (albumArtUri != null) {

                Picasso.with(this).load(albumArtUri).into(songBackground);


            }

            storage = new StorageUtil(getApplicationContext());


        }


    }

    @OnClick(R.id.img_fast_foward)
    public void playNextSong() {
        MediaPlayer currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        mediaPlayerService.skipToNext(audioList, audioIndex, this, currentMediaPlayer);


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

            mediaPlayerService.pauseMedia(currentMediaPlayer);

        } else if (!currentMediaPlayer.isPlaying()) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this, R.drawable.ic_pause_circle_filled_black_24dp));
            mediaPlayerService.resumeMedia(currentMediaPlayer);
            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
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


            long totalDuration = mediaPlayerService.getDur();
            long currentDuration = mediaPlayerService.getCurrentDur();

            // Displaying Total Duration time
            songTotalDuration.setText(String.valueOf(Timer.milliSecondsToTimer(totalDuration)));


            // Displaying time completed playing
            songCurrentDuration.setText(String.valueOf(Timer.milliSecondsToTimer(currentDuration)));

            // Updating progress bar
            int progress = (int) (Timer.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);


        }
    };


    private void setController() {
        //set the controller up

        controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.relative_layout_anchor));
        controller.setEnabled(true);


    }


    //play next
    private void playNext() {
        mediaPlayerService.playNext();
        controller.show(0);
    }

    //play previous
    private void playPrev() {
        mediaPlayerService.playPrev();
        controller.show(0);
    }

    private void updateMetaData(Audio activeAudio) {
//        seekBar.setProgress(0);
//        seekBar.setMax(100);
//
//        updateProgressBar();
//        long totalDuration = mediaPlayer.getDuration();
//        long currentDuration = mediaPlayer.getCurrentPosition();
//        activeAudio = audioList.get(audioIndex);
//        // Displaying Total Duration time
//        songTotalDuration.setText("" + Timer.milliSecondsToTimer(totalDuration));
//        // Displaying time completed playing
//        songCurrentDuration.setText("" + Timer.milliSecondsToTimer(currentDuration));


        Toast.makeText(this, "I came here o" + activeAudio.getTitle(), Toast.LENGTH_LONG).show();
//        mediaPlayer.setOnCompletionListener(this);

        String song_title = activeAudio.getTitle();
        String song_artist = activeAudio.getArtist();

        Uri albumArtUri = Uri.parse(activeAudio.getAlbumArtUriString());


        songTitle.setText(song_title);
        artist.setText(song_artist);
        if (albumArtUri != null) {

            Picasso.with(this).load(albumArtUri).into(songBackground);


        }

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
        int totalDuration = mediaPlayerService.getDur();
        int currentPosition = Timer.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        currentMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }




    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
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
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
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

    //    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
//        mediaPlayer = mediaPlayerService.getMediaPlayerInstance();
//        totalDuration = mediaPlayer.getDuration();
//        currentDuration = mediaPlayer.getCurrentPosition();
//        seekBar.setProgress(0);
//        seekBar.setMax(100);
//        updateProgressBar();
//    }

}
