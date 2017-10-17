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
import com.gloriousfury.musicplayer.adapter.AllSongsAdapter;
import com.gloriousfury.musicplayer.adapter.PlaylistAdapter;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Playlist;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class PlaylistFragments extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Playlist>> {


    public PlaylistFragments() {

    }

    public static PlaylistFragments newInstance() {
        PlaylistFragments fragment = new PlaylistFragments();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RelativeLayout settingsLayout;
    ImageView settingsImage;
    RecyclerView recyclerView;
    ArrayList<Audio> audioList = new ArrayList<>();
    ArrayList<Playlist> playlists = new ArrayList<>();
    PlaylistAdapter adapter;
    boolean serviceBound = false;
    Cursor cursor;
    private static final int TASK_LOADER_ID = 5;
    String TAG = "PlaylistFragments";
    StorageUtil storage;

    public AlphabetIndexer mAlphabetIndexer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);
        storage = new StorageUtil(getContext());
        playlists = storage.loadAllPlaylist();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlaylistAdapter(getActivity(), playlists);

        if (playlists == null) {
            getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        }

        recyclerView.setAdapter(adapter);
        return v;
    }


    @Override
    public Loader<ArrayList<Playlist>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ArrayList<Playlist>>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor cursor = null;


            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {

                playlists = storage.loadAllPlaylist();
//                retrievedAlbumsList = storage.loadAllAlbums();
//                Toast.makeText(LibraryActivity.this, String.valueOf(retrievedAudioList.size()), Toast.LENGTH_LONG).show();

                if (playlists != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(playlists);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public ArrayList<Playlist> loadInBackground() {


                return getPlayists();
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(ArrayList<Playlist> data) {

                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Playlist>> loader, ArrayList<Playlist> data) {
        storage.storePlaylist(data);
        adapter.setPlaylistListData(data);

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Playlist>> loader) {

    }


    public ArrayList<Playlist> getPlayists() {
        playlists = new ArrayList<Playlist>();
        // query external audio
        String[] projection = {
                android.provider.MediaStore.Audio.Playlists._ID,
                android.provider.MediaStore.Audio.Playlists.NAME};

//        String[] projectionPl = {
//                android.provider.MediaStore.Audio.Media._ID,
//                android.provider.MediaStore.Audio.Media.TITLE};

        ContentResolver resolver = getActivity().getContentResolver();

        Uri playlistsUri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor playlistsCursor = null;
        playlistsCursor = resolver.query(playlistsUri, projection, null,
                null, null);
        int idColumn = playlistsCursor
                .getColumnIndex(android.provider.MediaStore.Audio.Playlists._ID);
        int nameColumn = playlistsCursor
                .getColumnIndex(android.provider.MediaStore.Audio.Playlists.NAME);


        if (playlistsCursor != null && playlistsCursor.getCount() > 0) {
            playlists = new ArrayList<>();

            while (playlistsCursor.moveToNext()) {
                int playlist_id = playlistsCursor.getInt(idColumn);
                String playlist_name = playlistsCursor.getString(nameColumn);

                Cursor plCursor = resolver.query(
                        android.provider.MediaStore.Audio.Playlists.Members
                                .getContentUri("external", playlist_id), null,
                        null, null, null);
//                int idColumna = plCursor
//                        .getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
//                int nameColumna = plCursor
//                        .getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);

                if (plCursor != null && plCursor.getCount() > 0) {
                    audioList = new ArrayList<>();
                    while (plCursor.moveToNext()) {
                        String data = plCursor.getString(plCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
                        String title = plCursor.getString(plCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
                        String album = plCursor.getString(plCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));
                        String artist = plCursor.getString(plCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));
                        Long albumId = plCursor.getLong(plCursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID));

                        int duration = plCursor.getInt(plCursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION));

                        Uri sArtworkUri = Uri
                                .parse("content://media/external/audio/albumart");
                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);


                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    getActivity().getContentResolver(), albumArtUri);
//                            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                            bitmap = BitmapFactory.decodeResource(getResources(),
                                    R.mipmap.ic_launcher);
                        } catch (IOException e) {

                            e.printStackTrace();
                        }


                        // Save to audioList
                        audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()
                        ));
                    }
                }

                // Save to audioList
                playlists.add(new Playlist(playlist_name, playlist_id, audioList.size(), audioList));

            }


        }

        // iterate over results if valid
//        if (playlistsCursor != null && playlistsCursor.moveToFirst()) {
//            // get columns
//            int idColumn = playlistsCursor
//                    .getColumnIndex(android.provider.MediaStore.Audio.Playlists._ID);
//            int nameColumn = playlistsCursor
//                    .getColumnIndex(android.provider.MediaStore.Audio.Playlists.NAME);
//            // add songs to list

//            do {
//                long id = playlistsCursor.getLong(idColumn);
//
//                Playlist pl = new Playlist(
//                        playlistsCursor.getString(nameColumn), id);

//                Cursor plCursor = resolver.query(
//                        android.provider.MediaStore.Audio.Playlists.Members
//                                .getContentUri("external", id), projectionPl,
//                        null, null, null);

//                if (plCursor != null && plCursor.moveToFirst()) {
//                    // get columns
//                    int idColumna = plCursor
//                            .getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
//                    int nameColumna = plCursor
//                            .getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
//                    // add songs to list
//                    do {
//                        long ida = plCursor.getLong(idColumna);
//
//                        String title = plCursor.getString(nameColumna);
//
//                        for (Song song : songs) {
//
//                            if (song.id == ida) {
//                                pl.addSong(song);
//                            }
//                        }
//
//                    } while (plCursor.moveToNext());
//                }
//                playlists.add(pl);
//
//            }
//            } while (playlistsCursor.moveToNext());
        playlistsCursor.close();
        return playlists;
    }

}
