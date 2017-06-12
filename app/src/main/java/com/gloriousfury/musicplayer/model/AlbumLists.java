package com.gloriousfury.musicplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by OLORIAKE KEHINDE on 1/19/2017.
 */


public class AlbumLists {



    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Alphabetical_Letter")
    @Expose
    private String alphabet_letter;
    @SerializedName("Data")
    @Expose
    private ArrayList<Albums> data;



    public AlbumLists() {


    }

    public AlbumLists(String alphabet_letter) {

        this.alphabet_letter = alphabet_letter;
    }

    public AlbumLists(String alphabet_letter, ArrayList<Albums> data ) {
        this.data = data;
        this.alphabet_letter = alphabet_letter;
    }


    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The Id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImage() {
        return id;
    }

    /**
     *
     * @param id
     * The Id
     */
    public void setImage(Integer id) {
        this.id = id;
    }


    /**
     *
     * @return
     * The alphabet_letter
     */
    public String getAlphabet() {
        return alphabet_letter;
    }

    /**
     *
     * @param alphabet_letter
     * The Name
     */
    public void setAlphabet(String alphabet_letter) {
        this.alphabet_letter = alphabet_letter;
    }


    public ArrayList<Albums> getAlbums() {
        return data;
    }

    /**
     *
     * @param data
     * The Data
     */
    public void setAlbums(ArrayList<Albums> data) {
        this.data = data;
    }




}
