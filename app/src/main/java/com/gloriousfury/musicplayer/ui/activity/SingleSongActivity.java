package com.gloriousfury.musicplayer.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
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

public class SingleSongActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);
        ButterKnife.bind(this);
        mediaPlayerService = new MediaPlayerService(this);
        currentMediaPlayer =  mediaPlayerService.getMediaPlayerInstance();
        mediaSession = new MediaSessionCompat(this, "AudioPlayer");
        currentMediaPlayer.setOnCompletionListener(this);
        currentMediaPlayer.setOnErrorListener(this);
        seekBar.setOnSeekBarChangeListener(this);
//        currentMediaPlayer.setOnPreparedListener(this);




//        long totalDuration = currentMediaPlayer.getDuration();
//        long currentDuration = currentMediaPlayer.getCurrentPosition();
//

        // Updating progress bar
        updateProgressBar();

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
        MediaPlayer currentMediaPlayer =mediaPlayerService.getMediaPlayerInstance();

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
            long totalDuration = currentMediaPlayer.getDuration();
            long currentDuration = currentMediaPlayer.getCurrentPosition();

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

    private void updateMetaData(Audio activeAudio, MediaPlayer mediaPlayer) {
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
        mediaPlayer.setOnCompletionListener(this);

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
        int totalDuration = currentMediaPlayer.getDuration();
        int currentPosition = Timer.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        currentMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }


//    @Override
//    public void onCompletion(MediaPlayer mediaPlayer) {
//        currentMediaPlayer =mediaPlayerService.getMediaPlayerInstance();
//
//
//        audioList = storage.loadAudio();
//        audioIndex = storage.loadAudioIndex();
//
//        if (audioIndex == audioList.size() - 1) {
//            //if last in playlist
//            audioIndex = 0;
////            new MediaPlayerService().skipToNext(audioList, audioIndex, this, currentMediaPlayer);
//            activeAudio = audioList.get(audioIndex);
//            updateMetaData(activeAudio, currentMediaPlayer);
//        } else {
//            //get next in playlist
//            audioIndex++;
////          new MediaPlayerService(this).skipToNext(audioList, audioIndex, this, currentMediaPlayer);
////            mediaPlayerService.skipToNext(audioList, audioIndex, this, currentMediaPlayer);
//            activeAudio = audioList.get(audioIndex);
//            updateMetaData(activeAudio, currentMediaPlayer);
//        }
//
//
//    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //Invoked when playback of a media source has completed.

        StorageUtil storage = new StorageUtil(this);
        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        if(audioIndex<=audioList.size()){
            activeAudio = audioList.get(audioIndex);
//            Toast.makeText(this,"I came here but some reason didn't play",Toast.LENGTH_LONG).show();
            mediaPlayerService.skipToNext(audioList, audioIndex, this, currentMediaPlayer);
            Toast.makeText(this,String.valueOf(storage.loadAudioIndex()),Toast.LENGTH_LONG).show();
            updateMetaData(activeAudio,mediaPlayer);
        }else {
            Toast.makeText(this,"The list said I shouldn't play",Toast.LENGTH_LONG).show();

            mediaPlayerService.stopMedia();

            //stop the service
            mediaPlayerService.stopSelf();

        }
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
//
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
