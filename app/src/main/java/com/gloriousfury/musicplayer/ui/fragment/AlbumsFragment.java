package com.gloriousfury.musicplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AlbumsList_Adapter;
import com.gloriousfury.musicplayer.adapter.AllSongsAdapter;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Audio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class AlbumsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    public AlbumsFragment() {

    }

    public static AlbumsFragment newInstance() {
        AlbumsFragment fragment = new AlbumsFragment();
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
    ArrayList<AlbumLists> albumList;
    AlbumsList_Adapter adapter;
    AlbumsList_Adapter albumsList_adapter;
    boolean serviceBound = false;
    Cursor cursor;
    private static final int TASK_LOADER_ID = 0;

    public AlphabetIndexer mAlphabetIndexer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        adapter = new AlbumsList_Adapter(getActivity());


        recyclerView.setAdapter(adapter);

        Context context = getActivity();
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        return v;
    }


    private ArrayList<AlbumLists> loadAlbums() {

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
                audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()));
            }
        }


        cursor.close();
        return prepareData1(audioList);

    }



    public ArrayList<AlbumLists> prepareData1(ArrayList<Audio> audio_list) {

        ArrayList<AlbumLists> exampleList = new ArrayList<>();

        String[] title = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (int i = 0; i < title.length; i++) {
            String alphabet = title[i];


            AlbumLists exampleItem = new AlbumLists();


            ArrayList<Audio> newArrayList = new ArrayList<>();
            ArrayList<Audio> ashArrayList = new ArrayList<>();
            for (int k = 0; k < audioList.size(); k++) {

                if (audioList.get(k).getAlbum() != null) {
                    String firstLetter = audioList.get(k).getAlbum().substring(0, 1);


                    Audio newAudio;

                    if (firstLetter.equalsIgnoreCase(alphabet)) {
                        newAudio = audioList.get(k);
                        newArrayList.add(newAudio);
                    } else {
                        newAudio = audioList.get(k);
                        ashArrayList.add(newAudio);

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor cursor = null;
            ArrayList<AlbumLists> albumList = new ArrayList<>();

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (cursor != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(cursor);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data
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
                        audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()));
                    }
                }


                return cursor;
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {

                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        albumList = prepareData1(audioList);
        adapter = new AlbumsList_Adapter(getActivity(),albumList);
        recyclerView.setAdapter(adapter);
//        adapter.setAlbumData(albumList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setAlbumData(null);
    }
}