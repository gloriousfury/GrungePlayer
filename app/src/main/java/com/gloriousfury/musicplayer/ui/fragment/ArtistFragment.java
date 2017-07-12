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

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.ArtistAdapter;
import com.gloriousfury.musicplayer.adapter.ArtistAdapter;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Artist;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Artist;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class ArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Artist>> {


    public ArtistFragment() {

    }

    public static ArtistFragment newInstance() {
        ArtistFragment fragment = new ArtistFragment();
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
    ArrayList<Artist> artistList;
    ArtistAdapter adapter;
    boolean serviceBound = false;
    Cursor cursor;
    private static final int TASK_LOADER_ID = 6;
    String TAG = "ArtistFragments";
    StorageUtil storage;

    public AlphabetIndexer mAlphabetIndexer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);
        storage = new StorageUtil(getContext());
//        artistList = storage.loadAllArtist();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        adapter = new ArtistAdapter(getActivity(), artistList);

        if (artistList == null) {
            getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
            Log.e(TAG, "I came here");
        }

        recyclerView.setAdapter(adapter);
        return v;
    }


    @Override
    public Loader<ArrayList<Artist>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ArrayList<Artist>>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor cursor = null;


            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {

//                artistList = storage.loadAllArtist();
//                retrievedAlbumsList = storage.loadAllAlbums();
//                Toast.makeText(LibraryActivity.this, String.valueOf(retrievedAudioList.size()), Toast.LENGTH_LONG).show();

                if (artistList != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(artistList);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public ArrayList<Artist> loadInBackground() {


                return getArtistList();
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(ArrayList<Artist> data) {

                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Artist>> loader, ArrayList<Artist> data) {
//        storage.storeArtist(data);
        Log.e(TAG, "I came here");
        adapter.setArtistListData(data);

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Artist>> loader) {

    }


    public ArrayList<Artist> getArtistList() {

        String where = null;

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Artists._ID;
        final String artist_name = MediaStore.Audio.Artists.ARTIST;
        final String noOfTracks = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;
        final String noOfAlbums = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
//        final String Albums = MediaStore.Audio.Artists.Albums.ALBUM_ART;


        final String[] columns = {_id, artist_name, noOfAlbums, noOfTracks};
        Cursor cursor = getActivity().getContentResolver().query(uri, columns, null,
                null, null);


        // add playlsit to

        if (cursor != null && cursor.getCount() > 0) {

            artistList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String artist = cursor.getString(cursor.getColumnIndex(artist_name));
                Long Id = cursor.getLong(cursor
                        .getColumnIndexOrThrow(_id));
                int no_of_Albums = cursor.getInt(cursor.getColumnIndex(noOfTracks));
//                String album_art = cursor.getString(cursor.getColumnIndex(Albums));
                int no_Of_Tracks = cursor.getInt(cursor.getColumnIndex(noOfTracks));

//
                final String _id1 = MediaStore.Audio.Albums._ID;
                final String albumart1 = MediaStore.Audio.Albums.ALBUM_ART;
                final String tracks1 = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
                String album_art_string = null;
//
//
                final String[] projection = {_id1, albumart1, tracks1};
//
                Uri uri1 = MediaStore.Audio.Artists.Albums.getContentUri("external", Id);
                Cursor cursor2 = getActivity().getContentResolver().query(uri1, projection, null,
                        null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
//
                if (cursor2 != null && cursor2.getCount() > 0) {


                    cursor2.moveToPosition(0);
////
//                        String album = cursor.getString(cursor.getColumnIndex(album_name1));
                    Long albumId = cursor2.getLong(cursor2
                            .getColumnIndexOrThrow(_id1));
//                        String album_artist = cursor.getString(cursor.getColumnIndex(artist));
////               int album_art = cursor.getInt(cursor.getColumnIndex(artist));
//                        int noOf_Tracks = cursor.getInt(cursor.getColumnIndex(tracks1));

                    Uri sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                    album_art_string = albumArtUri.toString();
//
//                    }


                }
                cursor2.close();


                artistList.add(new Artist(artist, Id, no_Of_Tracks, no_of_Albums, album_art_string));
            }


//        storage.storeAllAlbums(albumList);


        }
        cursor.close();
        Log.e(TAG, String.valueOf(artistList.size()) + "  artists");
        return artistList;

    }
}
