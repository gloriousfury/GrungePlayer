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
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class AlbumsFragment extends Fragment {


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
    ArrayList<Albums> albumsArray;
    ArrayList<AlbumLists> albumList;
    AlbumsList_Adapter adapter;
    AlbumsList_Adapter albumsList_adapter;
    boolean serviceBound = false;
    Cursor cursor;
    private static final int TASK_LOADER_ID = 0;
    StorageUtil storage;
    ArrayList<AlbumLists> retrievedAlbumList;

    public AlphabetIndexer mAlphabetIndexer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);
        storage = new StorageUtil(getActivity());
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        retrievedAlbumList = storage.loadAllAlbums();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        adapter = new AlbumsList_Adapter(getActivity(), retrievedAlbumList);

        recyclerView.setAdapter(adapter);

        Context context = getActivity();


        return v;
    }


    public ArrayList<AlbumLists> prepareData1(ArrayList<Albums> album_list) {

        ArrayList<AlbumLists> exampleList = new ArrayList<>();

        String[] title = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (int i = 0; i < title.length; i++) {
            String alphabet = title[i];


            AlbumLists exampleItem = new AlbumLists();


            ArrayList<Albums> newArrayList = new ArrayList<>();
            ArrayList<Albums> ashArrayList = new ArrayList<>();
            for (int k = 0; k < album_list.size(); k++) {

                if (album_list.get(k).getAlbum() != null) {
                    String firstLetter = album_list.get(k).getAlbum().substring(0, 1);


                    Albums newAlbums;

                    if (firstLetter.equalsIgnoreCase(alphabet)) {
                        newAlbums = album_list.get(k);
                        newArrayList.add(newAlbums);
                    } else {
                        newAlbums = album_list.get(k);
                        ashArrayList.add(newAlbums);

                    }
                }

            }
            exampleItem.setAlbums(newArrayList);
            exampleItem.setAlphabet(title[i]);

            exampleList.add(exampleItem);


        }

        return exampleList;
    }
}