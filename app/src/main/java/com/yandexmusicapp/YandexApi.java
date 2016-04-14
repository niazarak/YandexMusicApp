package com.yandexmusicapp;

import com.yandexmusicapp.models.Artist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface YandexApi {
    // описание интерфейса, использующего api
    @GET("/mobilization-2016/artists.json")
    Call<List<Artist>> artists(); // понятно, надеюсь
}
