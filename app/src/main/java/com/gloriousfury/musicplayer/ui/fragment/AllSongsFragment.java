package com.gloriousfury.musicplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.adapter.AllSongsAdapter;
import com.gloriousfury.musicplayer.model.Artist;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.service.AppMainServiceEvent;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.gloriousfury.musicplayer.utils.OnSongClickListener;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


import de.greenrobot.event.EventBus;


/**
 * Created by OLORIAKE KEHINDE on 11/16/2016.
 */

public class AllSongsFragment extends Fragment implements RecyclerView.OnItemTouchListener,
        OnSongClickListener {


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
    ArrayList<Audio> audioList = null;
    boolean serviceBound = false;
    StorageUtil storage;
    String TAG = "LibraryActivity";
    ProgressBar progressBar;
    EventBus bus = EventBus.getDefault();
    private static final String LIFECYCLE_AUDIOLIST_CALLBACKS_KEY = "audioList";
    //    private static final String LIFECYCLE_PAGE_NO_KEY = "page_no";
    String ARTIST_ITEM = "artist_item";
    static AllSongsAdapter adapter;
    GestureDetectorCompat gestureScanner;
    static ActionMode actionMode;
    String SONG = "single_audio";
    String CLICK_CHECKER = "click_checker";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_allsongs, container, false);
        gestureScanner = new GestureDetectorCompat(getActivity(), new RecyclerViewOnGestureListener());
        storage = new StorageUtil(getContext());

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        adapter = new AllSongsAdapter(getActivity(), audioList);
        String currentActivity = getActivity().getClass().getSimpleName();
        Log.e(TAG, currentActivity);

        if (currentActivity.contentEquals("LibraryActivity")) {


            if (savedInstanceState == null || !savedInstanceState.containsKey(LIFECYCLE_AUDIOLIST_CALLBACKS_KEY)) {
                if (audioList != null) {
                    adapter.setAudioListData(audioList);
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (audioList == null) {
                    progressBar.setVisibility(View.VISIBLE);
                    audioList = storage.loadAllAudio();
                    adapter.setAudioListData(audioList);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    adapter.setAudioListData(audioList);
                    recyclerView.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Not loaded yet", Toast.LENGTH_LONG).show();

                }

            } else {
                Log.e(TAG, "I came to the savedInstance, point");
                audioList = savedInstanceState.getParcelableArrayList(LIFECYCLE_AUDIOLIST_CALLBACKS_KEY);
                if (audioList == null) {
                    Log.e(TAG, "Baba, it is null o");
                } else {

                    Log.e(TAG, String.valueOf(audioList.size()) + ": This is the size ");
                    adapter.setAudioListData(audioList);
                }
//                page_no = savedInstanceState.getInt(LIFECYCLE_PAGE_NO_KEY);
//                sortingParameter = savedInstanceState.getString(LIFECYCLE_SORTING_PARAMETER_KEY);
//                actionBar.setTitle(savedInstanceState.getString(LIFECYCLE_ACTIONBAR_TITLE));
//                moviesAdapter.setMoviesData(movieList);
            }

            recyclerView.setAdapter(adapter);
        } else if (currentActivity.contentEquals("ArtistActivity")) {
            Bundle getData = getActivity().getIntent().getExtras();
            if (getData != null) {

                Artist singleArtist = getData.getParcelable(ARTIST_ITEM);
                String artist_name = singleArtist.getArtistName();
                Log.e(TAG, currentActivity + "I came to artist activity " + artist_name);
//            String album_art_uri = singleArtist.getAlbumArtUri();
                long artist_id = singleArtist.getArtistId();
                progressBar.setVisibility(View.VISIBLE);
                audioList = loadAudioWithArtistName(artist_name);
                adapter = new AllSongsAdapter(getActivity(), audioList);

            }
            recyclerView.setAdapter(adapter);
        }

        recyclerView.addOnItemTouchListener(this);

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
                audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()
                ));
            }
        }


        cursor.close();


        return audioList;
    }

    private ArrayList<Audio> loadAudioWithArtistName(String artistName) {
        Log.e(TAG, "I came her o");
        ContentResolver contentResolver = getActivity().getContentResolver();
        String where = MediaStore.Audio.Albums.ARTIST + "=?";

        String whereVal[] = {artistName};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, where, whereVal, sortOrder);

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
                audioList.add(new Audio(data, title, album, artist, duration, albumId, albumArtUri.toString()
                ));
            }
        }


        cursor.close();

        progressBar.setVisibility(View.INVISIBLE);
        return audioList;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIFECYCLE_AUDIOLIST_CALLBACKS_KEY, audioList);
//        outState.putInt(LIFECYCLE_PAGE_NO_KEY, page_no);
//        outState.putString(LIFECYCLE_SORTING_PARAMETER_KEY, sortingParameter);
//        outState.putString(LIFECYCLE_ACTIONBAR_TITLE, actionBarTitle);
        super.onSaveInstanceState(outState);
    }

    public void onEventMainThread(AppMainServiceEvent event) {
        Log.d(TAG, "onEventMainThread() called with: " + "event = [" + event + "]");
        Intent i = event.getMainIntent();


        if (event.getEventType() == AppMainServiceEvent.ONDATALOADED) {
//            if (i != null) {
            audioList = storage.loadAllAudio();
            adapter.setAudioListData(audioList);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);


        }
    }

    public void showAudioData() {


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

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureScanner.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onSongSelectedClick(Bundle stepParameters, int position) {


    }


    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (actionMode != null) {

                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

                int idx = recyclerView.getChildPosition(view);
                myToggleSelection(idx);
            } else {
                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

                int idx = recyclerView.getChildPosition(view);
                String checker = null;
                Audio singleSong = audioList.get(idx);
                Utils appUtils = new Utils(getActivity());
                if (Utils.getInstance() != null) {
                    Utils.getInstance().playAudio(idx, audioList);

                } else {
                    new Utils(getActivity()).playAudio(idx, audioList);
                }
//            ((SingleRecipeActivity) context).onDescriptionSelected(stepParameters, getAdapterPosition());
                Intent openSingleSongActivity = new Intent(getActivity(), SingleSongActivity.class);
                openSingleSongActivity.putExtra(CLICK_CHECKER, checker);
                openSingleSongActivity.putExtra(SONG, singleSong);

                getActivity().startActivity(openSingleSongActivity);


            }
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            Log.e(TAG, "I came to the LongPress side");
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            AppCompatActivity activity = (AppCompatActivity) getActivity();


            if (actionMode != null) {
                return;
            }
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = activity.startSupportActionMode(mActionModeCallback);

            int idx = recyclerView.getChildPosition(view);
            myToggleSelection(idx);


            super.onLongPress(e);
        }
    }

    private void myToggleSelection(int idx) {
        adapter.toggleSelection(idx);
//        String title = getString( R.string.selected_count,adapter.getSelectedItemCount());
        actionMode.setTitle(" Issa 5");
    }


    public static ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
//            switch(item.getItemId())
//            {
//                case R.id.menu_delete:
//                    return true;
//
//            }
//
            return false;
        }


        //// Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.delete_only, menu);
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            actionMode = null;
            adapter.clearSelections();
        }

        //// Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return false;
        }
    };

}
