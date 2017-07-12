package com.gloriousfury.musicplayer.model;



import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Albums implements Parcelable {

    private String data;
    private String title;
    private String album;
    private String artist;
    private int noOfSongs;
    private long albumId;
    private String albumArtString;
    private int type;

    public Albums() {


    }




    public Albums(String album, String artist, int noOfSongs, long albumId, String albumArtString) {
        this.album = album;
        this.artist = artist;
        this.noOfSongs = noOfSongs;
        this.albumId = albumId;
        this.albumArtString = albumArtString;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getnoOfSongs() {
        return noOfSongs;
    }

    public void setnoOfSongs(int noOfSongs) {
        this.noOfSongs = noOfSongs;
    }


    public long getAlbumId() {
        return albumId;
    }

    public void setDuration(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumArtUriString() {
        return albumArtString;
    }

    public void setAlbumArtUriString(String albumArtString) {
        this.albumArtString= albumArtString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
        dest.writeString(this.title);
        dest.writeString(this.album);
        dest.writeString(this.artist);
        dest.writeInt(this.noOfSongs);
        dest.writeLong(this.albumId);
        dest.writeString(this.albumArtString);
        dest.writeInt(this.type);
    }

    protected Albums(Parcel in) {
        this.data = in.readString();
        this.title = in.readString();
        this.album = in.readString();
        this.artist = in.readString();
        this.noOfSongs = in.readInt();
        this.albumId = in.readLong();
        this.albumArtString = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<Albums> CREATOR = new Creator<Albums>() {
        @Override
        public Albums createFromParcel(Parcel source) {
            return new Albums(source);
        }

        @Override
        public Albums[] newArray(int size) {
            return new Albums[size];
        }
    };
}