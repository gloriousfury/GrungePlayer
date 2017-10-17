package com.gloriousfury.musicplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
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
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.GenreAdapter;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Genre;

import com.gloriousfury.musicplayer.model.Genre;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class GenreFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Genre>> {


    public GenreFragment() {

    }

    public static GenreFragment newInstance() {
        GenreFragment fragment = new GenreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RelativeLayout settingsLayout;
    ImageView settingsImage;
    RecyclerView recyclerView;
    ArrayList<Genre> genreList;
    ArrayList<Audio> albumArtArray;

    GenreAdapter adapter;

    boolean serviceBound = false;
    Cursor cursor;
    private static final int TASK_LOADER_ID = 7;
    String TAG = "GenreFragments";
    StorageUtil storage;

    public AlphabetIndexer mAlphabetIndexer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);
        storage = new StorageUtil(getContext());
//        genreList = storage.loadAllGenre();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        adapter = new GenreAdapter(getActivity(), genreList);

        if (genreList == null) {
            getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
            Log.e(TAG, "I came here");
        }

        recyclerView.setAdapter(adapter);
        return v;
    }


    @Override
    public Loader<ArrayList<Genre>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ArrayList<Genre>>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor cursor = null;


            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {

//                genreList = storage.loadAllGenre();
//                retrievedAlbumsList = storage.loadAllAlbums();
//                Toast.makeText(LibraryActivity.this, String.valueOf(retrievedAudioList.size()), Toast.LENGTH_LONG).show();

                if (genreList != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(genreList);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public ArrayList<Genre> loadInBackground() {


                return getGenreList();
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(ArrayList<Genre> data) {

                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Genre>> loader, ArrayList<Genre> data) {
//        storage.storeGenre(data);
        Log.e(TAG, "I came here");
        adapter.setGenreListData(data);

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Genre>> loader) {

    }


    public ArrayList<Genre> getGenreList() {

        int index;
        long genreId;
        Uri uri;
        Cursor genrecursor;
        Cursor tempcursor;
        int noOfSongs;
        String[] proj1 = {MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID};
        String[] proj2 = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ALBUM_ID};
        ContentResolver resolver = getActivity().getContentResolver();

        genrecursor = resolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, proj1, null, null, null);
        genreList = new ArrayList<>();
        if (genrecursor.moveToFirst()) {

            do {
                index = genrecursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                Log.i("Tag-Genre name", genrecursor.getString(index));
                String genre_name = genrecursor.getString(index);


                index = genrecursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                genreId = Long.parseLong(genrecursor.getString(index));


                Uri uri2 = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);

                cursor = resolver.query(uri2, proj2, null, null, null);
                noOfSongs = cursor.getCount();
                Log.i("Tag-Number of songs ", cursor.getCount() + "");


                albumArtArray = new ArrayList<>();
//
//                if (cursor.moveToFirst()) {
//                    do {
////                        index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
////                        Long albumId = cursor.getLong(cursor
////                                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
////                        Uri sArtworkUri = Uri
////                                .parse("content://media/external/audio/albumart");
////                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
////TODO Can create new model item for this
//
                if (cursor.getCount() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        if (i <= cursor.getCount()) {
                            cursor.moveToPosition(i);
                            index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                            Long albumId = cursor.getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                            Uri sArtworkUri = Uri
                                    .parse("content://media/external/audio/albumart");
                            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                            String albumArtUriString = null;


                            File file = new File(URI.create(albumArtUri.toString()).getPath());
                            if (file.exists()) {

                                albumArtUriString = albumArtUri.toString();
                                Log.i("AlbumArtString", albumArtUriString.toString());
                            } else {
                                albumArtUriString = null;

                            }

                            Audio singleAudio = new Audio();
                            singleAudio.setAlbumArtUriString(albumArtUriString);
                            albumArtArray.add(singleAudio);
                        }
                    }

                } else {
                    cursor.moveToPosition(0);
                    index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    Long albumId = cursor.getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    Uri sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                    String albumArtUriString = null;


                    File file = new File(URI.create(albumArtUri.toString()).getPath());
                    if (file.exists()) {

                        albumArtUriString = albumArtUri.toString();
                    } else {
                        albumArtUriString = null;

                    }


                    Audio singleAudio = new Audio();
                    singleAudio.setAlbumArtUriString(albumArtUriString);
                    albumArtArray.add(singleAudio);


                }
//                        Log.i("Tag-Song name", cursor.getString(index));

                Genre singleGenre = new Genre();
                singleGenre.setGenreTitle(genre_name);
                singleGenre.setId(genreId);
                singleGenre.setAudioImageList(albumArtArray);
                singleGenre.setNoOfSongs(noOfSongs);
                genreList.add(singleGenre);


            } while (genrecursor.moveToNext());

        }
        genrecursor.close();
//        Log.e(TAG, String.valueOf(genreList.size()) + "  artists");
        return genreList;
    }

}
