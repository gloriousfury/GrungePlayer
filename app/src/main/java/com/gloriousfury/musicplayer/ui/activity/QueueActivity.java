package com.gloriousfury.musicplayer.ui.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
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
import com.gloriousfury.musicplayer.adapter.QueueAdapter;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.service.MediaPlayerService;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class QueueActivity extends AppCompatActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    QueueAdapter adapter;

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
        setContentView(R.layout.activity_queue);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Queue");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        storage = new StorageUtil(this);
        audioList = storage.loadAudio();


//            Toast.makeText(this, AlbumArtist + " " + AlbumTitle, Toast.LENGTH_LONG).show();
//            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


        adapter = new QueueAdapter(this, audioList);


        recyclerView.setAdapter(adapter);


    }


//
//    @OnClick(R.id.img_play_pause)
//    public void playPause() {
//        currentMediaPlayer = mediaPlayerService.getMediaPlayerInstance();
//
//       Utils.getInstance().playAudio(0, albumAudioList);
//
//    }


    public void onEventMainThread(AppMainServiceEvent event) {
        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
        Intent i = event.getMainIntent();


        if (event.getEventType() == AppMainServiceEvent.ONCOMPLETED_RESPONSE) {
            if (i != null) {

//                Audio recievedAudio = i.getParcelableExtra(AppMainServiceEvent.RESPONSE_DATA);
//
//                audioList = storage.loadAllAudio();

//                Toast statu = Toast.makeText(this, "I came here, just so you know", Toast.LENGTH_LONG);
//                statu.show();
//                audioIndex = storage.loadAudioIndex();
//                changeAdapterData();

//                adapter = new AlbumSongsAdapter(this, albumAudioList, audioIndex);
//
//
//                recyclerView.setAdapter(adapter);
//
//

//                Toast.makeText(this, String.valueOf(audioIndex), Toast.LENGTH_LONG).show();
//                adapter.setAdapterData(audioIndex);


            } else {

                Toast statu = Toast.makeText(this, "Cant Retrieve data at the moment, Try again", Toast.LENGTH_LONG);
                statu.show();
            }


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);


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


}
