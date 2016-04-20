package com.yandexmusicapp;

import java.io.File;

import static com.yandexmusicapp.utils.CacheUtils.cache;

public class Application extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        cache = new File(getCacheDir(),"cache.txt");
    }
}
