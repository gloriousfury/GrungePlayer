package com.gloriousfury.musicplayer.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Parcelable {


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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.playlistId);
        dest.writeString(this.playlistTitle);
        dest.writeInt(this.noOfSongs);
        dest.writeTypedList(this.playListMembers);
    }

    protected Playlist(Parcel in) {
        this.playlistId = in.readLong();
        this.playlistTitle = in.readString();
        this.noOfSongs = in.readInt();
        this.playListMembers = in.createTypedArrayList(Audio.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}