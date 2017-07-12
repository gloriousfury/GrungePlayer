package com.gloriousfury.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Artist implements Parcelable {

    @SerializedName("artist_name")
    @Expose
    private String artistName;
    @SerializedName("artist_id")
    @Expose
    private long artistId;
    @SerializedName("no_of_albums")
    @Expose
    private Integer noOfAlbums;
    @SerializedName("no_of_tracks")
    @Expose
    private Integer noOfTracks;
    @SerializedName("album_art_uri")
    @Expose
    private String albumArtUri;

    public Artist() {


    }


    public Artist(String artistname, long artistId, int noOfAlbums) {

        this.artistName = artistname;
        this.artistId = artistId;
        this.noOfAlbums = noOfAlbums;


    }


    public Artist(String artistname, long artistId, int noOfTracks, int noOfAlbums,String albumArtUri) {

        this.artistName = artistname;
        this.artistId = artistId;
        this.noOfTracks = noOfTracks;
        this.noOfAlbums = noOfAlbums;
        this.albumArtUri = albumArtUri;

    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public Integer getNoOfAlbums() {
        return noOfAlbums;
    }

    public void setNoOfAlbums(Integer noOfAlbums) {
        this.noOfAlbums = noOfAlbums;
    }

    public Integer getNoOfTracks() {
        return noOfTracks;
    }

    public void setNoOfTracks(Integer noOfTracks) {
        this.noOfTracks = noOfTracks;
    }


    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri= albumArtUri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeLong(this.artistId);
        dest.writeValue(this.noOfAlbums);
        dest.writeValue(this.noOfTracks);
        dest.writeString(this.albumArtUri);
    }

    protected Artist(Parcel in) {
        this.artistName = in.readString();
        this.artistId = in.readLong();
        this.noOfAlbums = (Integer) in.readValue(Integer.class.getClassLoader());
        this.noOfTracks = (Integer) in.readValue(Integer.class.getClassLoader());
        this.albumArtUri = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
