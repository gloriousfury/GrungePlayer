package com.gloriousfury.musicplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AllSongsAdapter;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.ui.activity.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class AllSongsFragment extends Fragment implements View.OnClickListener {


    public static AllSongsFragment newInstance() {
        AllSongsFragment fragment = new AllSongsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RelativeLayout settingsLayout;
    ImageView settingsImage;
    RecyclerView recyclerView;
    ArrayList<Audio> audioList;
    boolean serviceBound = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        AllSongsAdapter adapter = new AllSongsAdapter(getActivity(), loadAudio());


        recyclerView.setAdapter(adapter);
        return v;
    }


    private ArrayList<Audio> loadAudio() {

        ContentResolver contentResolver = getActivity().getContentResolver();

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
                            getActivity().getContentResolver(), albumArtUri);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher);
                } catch (IOException e) {

                    e.printStackTrace();
                }


                // Save to audioList
                audioList.add(new Audio(data, title, album, artist, duration,albumId,albumArtUri));
            }
        }


        cursor.close();
        return audioList;
    }


    @Override
    public void onClick(View v) {


    }

}
