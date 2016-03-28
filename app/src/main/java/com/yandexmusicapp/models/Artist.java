package com.yandexmusicapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Artist implements Serializable{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("genres")
    @Expose
    private List<String> genres = new ArrayList<String>();
    @SerializedName("tracks")
    @Expose
    private Integer tracks;
    @SerializedName("albums")
    @Expose
    private Integer albums;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover")
    @Expose
    private Cover cover;

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
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The genres
     */
    public List<String> getGenres() {
        return genres;
    }

    public String getImplodedGenres(){
        if(genres.size()==1){
            return genres.get(0);
        }
        else {
            String res = "";
            for (String genre : genres
                    ) {
                res += genre + ", ";
            }
            return res;
        }
    }

    /**
     *
     * @param genres
     * The genres
     */
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /**
     *
     * @return
     * The tracks
     */
    public Integer getTracks() {
        return tracks;
    }

    /**
     *
     * @param tracks
     * The tracks
     */
    public void setTracks(Integer tracks) {
        this.tracks = tracks;
    }

    /**
     *
     * @return
     * The albums
     */
    public Integer getAlbums() {
        return albums;
    }

    /**
     *
     * @param albums
     * The albums
     */
    public void setAlbums(Integer albums) {
        this.albums = albums;
    }

    public String getRepertoire(){
        return albums+" альбомов, "+tracks+" песен";
    }
    /**
     *
     * @return
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The cover
     */
    public Cover getCover() {
        return cover;
    }

    /**
     *
     * @param cover
     * The cover
     */
    public void setCover(Cover cover) {
        this.cover = cover;
    }

}