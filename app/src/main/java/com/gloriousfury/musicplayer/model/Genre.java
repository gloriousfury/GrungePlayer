package com.gloriousfury.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Genre implements Parcelable {


    private long id;
    private String genreTitle;
    private ArrayList<Audio> audioImageList;
    private int genreType;
    private int noOfSongs;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGenreTitle() {
        return genreTitle;
    }

    public void setGenreTitle(String genreTitle) {
        this.genreTitle = genreTitle;
    }


    public ArrayList<Audio> getAudioImageList() {
        return audioImageList;
    }

    public void setAudioImageList(ArrayList<Audio> audioImageList) {
        this.audioImageList = audioImageList;
    }

    public void setGenreType(int genreType) {
        this.genreType = genreType;
    }

    public int getGenreType() {
        return this.genreType;

    }


    public void setNoOfSongs(int noOfSongs) {
        this.noOfSongs = noOfSongs;
    }

    public int getNoOfSongs() {
        return this.noOfSongs;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.genreTitle);
        dest.writeTypedList(this.audioImageList);
        dest.writeInt(this.genreType);
        dest.writeInt(this.noOfSongs);
    }

    public Genre() {
    }

    protected Genre(Parcel in) {
        this.id = in.readLong();
        this.genreTitle = in.readString();
        this.audioImageList = in.createTypedArrayList(Audio.CREATOR);
        this.genreType = in.readInt();
        this.noOfSongs = in.readInt();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };
}