package com.gloriousfury.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Albums;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.model.Playlist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StorageUtil {

    private final String STORAGE = " com.gloriousfury.MusicPlayer.STORAGE";

    private final String ALL_MUSIC_STORAGE = " com.gloriousfury.MusicPlayer.ALL_MUSIC_STORAGE";
    private final String ALL_MUSIC_STORAGE_KEY= "allAudioArrayList";

    private final String ALL_ALBUMS_STORAGE = " com.gloriousfury.MusicPlayer.ALL_ALBUMS_STORAGE";
    private final String ALL_ALBUMS_STORAGE_KEY= "allAlbumsArrayList";

    private final String ALL_PLAYLIST_STORAGE = " com.gloriousfury.MusicPlayer.ALL_PLAYLIST_STORAGE";
    private final String ALL_PLAYLIST_STORAGE_KEY= "allPlaylistArrayList";

    private final String PLAYBACK_POSITION = "PlayBackPosition";

    public final String SHUFFLE_KEY = "shuffle";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storeAudio(ArrayList<Audio> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }


    public void storeAllAudio(ArrayList<Audio> arrayList) {
        preferences = context.getSharedPreferences(ALL_MUSIC_STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(ALL_MUSIC_STORAGE_KEY, json);
        editor.apply();
    }


    public void storeAllAlbums(ArrayList<Albums> arrayList) {
        preferences = context.getSharedPreferences(ALL_ALBUMS_STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(ALL_ALBUMS_STORAGE_KEY, json);
        editor.apply();
    }


    public ArrayList<Albums> loadAllAlbums() {
        preferences = context.getSharedPreferences(ALL_ALBUMS_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(ALL_ALBUMS_STORAGE_KEY, null);
        Type type = new TypeToken<ArrayList<Albums>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public ArrayList<Audio> loadAudio() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<ArrayList<Audio>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<Audio> loadAllAudio() {
        preferences = context.getSharedPreferences(ALL_MUSIC_STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(ALL_MUSIC_STORAGE_KEY, null);
        Type type = new TypeToken<ArrayList<Audio>>() {
        }.getType();
        return gson.fromJson(json, type);
    }



    public void storePlaylist(ArrayList<Playlist> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(ALL_PLAYLIST_STORAGE_KEY, json);
        editor.apply();
    }


    public ArrayList<Playlist> loadAllPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(ALL_PLAYLIST_STORAGE_KEY, null);
        Type type = new TypeToken<ArrayList<Playlist>>() {
        }.getType();
        return gson.fromJson(json, type);
    }




    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void storePlayBackPostition(long playBackPos) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PLAYBACK_POSITION, playBackPos);
        editor.apply();
    }

    public long loadPlayBackPosition() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getLong(PLAYBACK_POSITION, 0);//return -1 if no data found
    }



    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void   setShuffle(boolean shuffleSettings) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHUFFLE_KEY,shuffleSettings);
        editor.apply();
    }

    public boolean  getShuffleSettings() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(SHUFFLE_KEY,false);
    }

}