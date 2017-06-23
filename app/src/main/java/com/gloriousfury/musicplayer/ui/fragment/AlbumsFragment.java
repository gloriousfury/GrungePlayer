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
import com.gloriousfury.musicplayer.adapter.AlbumAdapter;
import com.gloriousfury.musicplayer.adapter.AlbumAdapter2;
import com.gloriousfury.musicplayer.adapter.AlbumsList_Adapter;
import com.gloriousfury.musicplayer.adapter.AllSongsAdapter;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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
    ArrayList<AlbumLists> albumListArray;
    ArrayList<Albums> albumList;
    AlbumAdapter2 adapter;
    AlbumsList_Adapter albumsList_adapter;
    boolean serviceBound = false;
    Cursor cursor;
    private static final int TASK_LOADER_ID = 0;
    StorageUtil storage;
    ArrayList<Albums> retrievedAlbumList;

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


        adapter = new AlbumAdapter2(getActivity(),addAlphabets(sortList(retrievedAlbumList)));

        recyclerView.setAdapter(adapter);

        Context context = getActivity();


        return v;
    }




    ArrayList<Albums> sortList(ArrayList<Albums> list) {
        Collections.sort(list, new Comparator<Albums>() {
            @Override
            public int compare(Albums album1, Albums album2) {
                return album1.getAlbum().compareTo(album2.getAlbum());
            }
        });
        return list;
    }


    ArrayList<Albums> addAlphabets(ArrayList<Albums> list) {
        int i = 0;
        ArrayList<Albums> customList = new ArrayList<Albums>();
        Albums firstAlbum = new Albums();
        firstAlbum.setAlbum(String.valueOf(list.get(0).getAlbum().charAt(0)));
        firstAlbum.setType(1);
        customList.add(firstAlbum);
        for (i = 0; i < list.size() - 1; i++) {
            Albums teamMember = new Albums();
            char name1 = list.get(i).getAlbum().charAt(0);
            char name2 = list.get(i + 1).getAlbum().charAt(0);
            if (name1 == name2) {
                list.get(i).setType(2);
                customList.add(list.get(i));
            } else {
                list.get(i).setType(2);
                customList.add(list.get(i));
                teamMember.setAlbum(String.valueOf(name2));
                Toast.makeText(getActivity(),String.valueOf(name2),Toast.LENGTH_SHORT).show();
                teamMember.setType(1);
                customList.add(teamMember);
            }
        }
        list.get(i).setType(2);
        customList.add(list.get(i));
        return customList;
    }






    
    
    
    

}