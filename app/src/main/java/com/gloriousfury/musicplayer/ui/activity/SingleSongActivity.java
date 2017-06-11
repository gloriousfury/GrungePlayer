package com.gloriousfury.musicplayer.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleSongActivity extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener  {

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
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio; //an object of the currently playing audio
    StorageUtil storage;
    PlaybackStatus playbackStatus;
    MediaPlayerService mediaPlayerService;
    MediaPlayer currentMediaPlayer;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);
        ButterKnife.bind(this);
        mediaPlayerService = new MediaPlayerService();
        currentMediaPlayer = MediaPlayerService.getMediaPlayerInstance();

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

        Bundle getSongData = getIntent().getExtras();

        if(getSongData!=null){
            String song_title = getSongData.getString(SONG_TITLE);
            String song_artist = getSongData.getString(SONG_ARTIST);

            Uri albumArtUri = Uri.parse(getSongData.getString(ALBUM_ART_URI));


            songTitle.setText(song_title);
            artist.setText(song_artist);
            if(albumArtUri!= null){

                Picasso.with(this).load(albumArtUri).into(songBackground);


            }

         storage  = new StorageUtil(getApplicationContext());


        }






    }


    @OnClick(R.id.img_fast_foward)
    public void playNextSong() {
        MediaPlayer currentMediaPlayer = MediaPlayerService.getMediaPlayerInstance();

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        new MediaPlayerService().skipToNext(audioList,audioIndex,this,currentMediaPlayer);


    }


    @OnClick(R.id.img_rewind)
    public void playPreviousSong() {
        MediaPlayer currentMediaPlayer = MediaPlayerService.getMediaPlayerInstance();

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        new MediaPlayerService().skipToPrevious(audioList,audioIndex,this,currentMediaPlayer);


    }


    @OnClick(R.id.img_play_pause)
    public void playPause() {
        currentMediaPlayer = MediaPlayerService.getMediaPlayerInstance();
        if (currentMediaPlayer.isPlaying()) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this,R.drawable.ic_play_circle_filled_white_black_24dp));

            mediaPlayerService.pauseMedia(currentMediaPlayer);

        } else if (!currentMediaPlayer.isPlaying()) {

            playPauseView.setImageDrawable(ContextCompat
                    .getDrawable(SingleSongActivity.this,R.drawable.ic_pause_circle_filled_black_24dp));
            mediaPlayerService.resumeMedia(currentMediaPlayer);
        }


    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = currentMediaPlayer.getDuration();
            long currentDuration = currentMediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDuration.setText(""+ Timer.milliSecondsToTimer(totalDuration));

            Toast.makeText(SingleSongActivity.this,songTotalDuration.getText().toString(),Toast.LENGTH_LONG).show();
            // Displaying time completed playing
            songCurrentDuration.setText(""+Timer.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(Timer.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
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





}
