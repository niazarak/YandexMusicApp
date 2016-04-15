package com.yandexmusicapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cover implements Serializable{
    //модель обложки артиста для Gson

    @SerializedName("small")
    @Expose
    private String small;
    @SerializedName("big")
    @Expose
    private String big;

    /**
     *
     * @return
     * The small
     */
    public String getSmall() {
        return small;
    }

    /**
     *
     * @return
     * The big
     */
    public String getBig() {
        return big;
    }

}