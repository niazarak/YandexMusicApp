package com.yandexmusicapp.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandexmusicapp.models.Artist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CacheUtils {
    public static File cache;

    public static List<Artist> getCachedArtists() {
        String cached = null;
        try {
            cached = getCache();
        } catch (IOException e) {
            return new ArrayList<Artist>();
        }
        //парсим
        Gson gson = new Gson();
        List<Artist> artists = gson.fromJson(cached,new TypeToken<List<Artist>>(){}.getType());
        if (artists==null) return new ArrayList<Artist>();
        else return artists;
    }

    public static void setCache(String rawJson) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(cache);
            fileOutputStream.write(rawJson.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            //???
            e.printStackTrace();
        }
    }

    public static String getCache() throws IOException {
        //читаем файл кэша
        //try with resources не работает на старых андройдах
        BufferedReader bufferedReader = new BufferedReader(new FileReader(cache));
        String line;
        String cache = "";
        while ((line = bufferedReader.readLine()) != null) {
            cache += line;
        }
        bufferedReader.close();
        return cache;
    }
}