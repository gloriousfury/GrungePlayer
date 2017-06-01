package com.gloriousfury.musicplayer.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;

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



        }






    }


    @OnClick(R.id.img_play_pause)
    public void ButtonClick() {
        Toast.makeText(this,"ButterKnife worked", Toast.LENGTH_LONG).show();
    }
}
