package com.gloriousfury.musicplayer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AlbumAdapter;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

import static com.gloriousfury.musicplayer.utils.Utils.dpToPx;


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
    ArrayList<Albums> albumList = new ArrayList<>();
    AlbumAdapter adapter;
    boolean serviceBound = false;
    StorageUtil storage;
    ArrayList<Albums> retrievedAlbumList = new ArrayList<>();
    EventBus bus = EventBus.getDefault();
    String TAG = "AlbumFragments";
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general, container, false);
        storage = new StorageUtil(getActivity());
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        retrievedAlbumList = storage.loadAllAlbums();

//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//
//        recyclerView.setLayoutManager(layoutManager);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());


        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new Utils.GridSpacingItemDecoration(2, dpToPx(1,getContext()), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(context,
//                LinearLayoutManager.VERTICAL, R.drawable.divider_white));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new AlbumAdapter(getActivity(), retrievedAlbumList);

        if (retrievedAlbumList != null) {
            adapter.setAudioListData(addAlphabets(sortList(retrievedAlbumList)));


        } else {
            adapter.setAudioListData(albumList);

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);


        }
        recyclerView.setAdapter(adapter);

        Context context = getActivity();


        return v;
    }


    public boolean checkIfDataIsReady() {

        retrievedAlbumList = storage.loadAllAlbums();
        if (retrievedAlbumList != null) {

            return true;
        } else {
            Log.d(TAG, "I came here instead");
            return false;
        }
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
//        Albums firstAlbum = new Albums();
//        firstAlbum.setAlbum(String.valueOf(list.get(0).getAlbum().charAt(0)));
//        firstAlbum.setType(1);
//        customList.add(firstAlbum);
        for (i = 0; i < list.size() - 1; i++) {
            Albums teamMember = new Albums();
            char name1 = list.get(i).getAlbum().toUpperCase().charAt(0);
            char name2 = list.get(i + 1).getAlbum().toUpperCase().charAt(0);
            if (name1 == name2) {
                list.get(i).setType(2);
                customList.add(list.get(i));
            } else {
                list.get(i).setType(2);
                customList.add(list.get(i));
//                teamMember.setAlbum(String.valueOf(name2));
////                Toast.makeText(getActivity(),String.valueOf(name2),Toast.LENGTH_SHORT).show();
//                teamMember.setType(1);
//                customList.add(teamMember);
            }
        }
        list.get(i).setType(2);
        customList.add(list.get(i));
        return customList;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bus.register(this);
    }

    public void onEventMainThread(AppMainServiceEvent event) {
        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
        Intent i = event.getMainIntent();


        if (event.getEventType() == AppMainServiceEvent.ONDATALOADED) {
//            if (i != null) {
            retrievedAlbumList = storage.loadAllAlbums();
            adapter.setAudioListData(addAlphabets(sortList(retrievedAlbumList)));
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);


        }
    }


}