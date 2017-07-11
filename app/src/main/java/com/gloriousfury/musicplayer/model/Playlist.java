package com.gloriousfury.musicplayer.model;


import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {


    private long playlistId;
    private String playlistTitle;
    private int noOfSongs;
    private ArrayList<Audio> playListMembers;

    public Playlist() {


    }
    public Playlist(String playlistTitle, long playlistId) {

        this.playlistTitle = playlistTitle;
        this.playlistId = playlistId;

    }

    public Playlist(String playlistTitle, long playlistId, int noOfSongs,ArrayList<Audio> playListMembers) {

        this.playlistTitle = playlistTitle;
        this.playlistId = playlistId;
        this.noOfSongs = noOfSongs;
        this.playListMembers = playListMembers;

    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }


    public int getPlaylistNoOfSongs() {
        return noOfSongs;
    }

    public void setnoOfSongs(int  noOfSongs) {
        this.noOfSongs = noOfSongs;
    }



    public ArrayList<Audio> getplayListMembers() {
        return playListMembers;
    }

    public void setplayListMembers(ArrayList<Audio> playListMembers) {
        this.playListMembers = playListMembers;
    }
}