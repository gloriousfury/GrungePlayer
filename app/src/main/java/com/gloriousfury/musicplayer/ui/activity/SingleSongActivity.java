package com.gloriousfury.musicplayer.ui.activity;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.StorageUtil;
import com.gloriousfury.musicplayer.service.MediaPlayerService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleSongActivity extends AppCompatActivity {

    @BindView(R.id.artist)
    TextView artist;


    @BindView(R.id.song_title)
    TextView songTitle;

    @BindView(R.id.img_play_pause)
    ImageView playPauseView;

    @BindView(R.id.img_rewind)
    ImageView rewindView;

    @BindView(R.id.img_fast_foward)
    ImageView fastFowardView;

    @BindView(R.id.shuffle)
    ImageView shuffleView;

    @BindView(R.id.repeat)
    ImageView repeatView;
    String SONG_TITLE = "song_title";
    String SONG_ARTIST = "song_artist";
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio; //an object of the currently playing audio
    StorageUtil storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);
        ButterKnife.bind(this);

        Bundle getSongData = getIntent().getExtras();

        if(getSongData!=null){
            String song_title = getSongData.getString(SONG_TITLE);
            String song_artist = getSongData.getString(SONG_ARTIST);


            songTitle.setText(song_title);
            artist.setText(song_artist);

         storage  = new StorageUtil(getApplicationContext());


        }






    }


    @OnClick(R.id.img_play_pause)
    public void ButtonClick() {
        Toast.makeText(this,"ButterKnife worked", Toast.LENGTH_LONG).show();
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
        MediaPlayer currentMediaPlayer = MediaPlayerService.getMediaPlayerInstance();

        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        new MediaPlayerService().skipToPrevious(audioList,audioIndex,this,currentMediaPlayer);


    }




}
