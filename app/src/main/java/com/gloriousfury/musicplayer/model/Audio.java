package com.gloriousfury.musicplayer.model;



import java.io.Serializable;

public class Audio implements Serializable {

    private String data;
    private String title;
    private String album;
    private String artist;
    private int duration;
    private long albumId;
    private String albumArtString;

    public Audio() {


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

    public void setDuration(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumArtUriString() {
        return albumArtString;
    }

    public void setAlbumArtUriString(String albumArtString) {
        this.albumArtString= albumArtString;
    }


}