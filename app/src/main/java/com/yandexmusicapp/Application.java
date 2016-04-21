package com.yandexmusicapp;

import java.io.File;

import static com.yandexmusicapp.utils.CacheUtils.cache;

public class Application extends android.app.Application{

    public static final String ARTIST = "ARTIST";
    public static final String POSITION = "POSITION";
    public static final String SORT = "SORT";
    public static final String QUERY= "QUERY";

    @Override
    public void onCreate() {
        super.onCreate();
        //инициализируем путь кэша
        cache = new File(getCacheDir(),"cache.txt");
    }
}
