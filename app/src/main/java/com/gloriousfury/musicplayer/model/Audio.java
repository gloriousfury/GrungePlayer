package com.gloriousfury.musicplayer.model;



import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Audio implements Parcelable {

    private String data;
    private String title;
    private String album;
    private String artist;
    private int duration;
    private long albumId;
    private String albumArtString;

    public Audio() {


    }


    public Audio(String data, String title, String artist, int duration,long albumId,String albumArtString) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.albumId = albumId;
        this.albumArtString = albumArtString;
    }

    public Audio(String data, String title, String album, String artist, int duration,long albumId, String albumArtString) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumArtUriString() {
        return albumArtString;
    }

    public void setAlbumArtUriString(String albumArtString) {
        this.albumArtString= albumArtString;
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
        dest.writeInt(this.duration);
        dest.writeLong(this.albumId);
        dest.writeString(this.albumArtString);
    }

    protected Audio(Parcel in) {
        this.data = in.readString();
        this.title = in.readString();
        this.album = in.readString();
        this.artist = in.readString();
        this.duration = in.readInt();
        this.albumId = in.readLong();
        this.albumArtString = in.readString();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel source) {
            return new Audio(source);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };
}