package com.gloriousfury.musicplayer.model;



import java.io.Serializable;

public class Albums implements Serializable {

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


}