package com.yandexmusicapp.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandexmusicapp.models.Artist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CacheUtils {
    public static File cache;

    public static List<Artist> getCachedArtists() {
        //читаем файл кэша
        //try with resources не работает на старых андройдах
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(cache));
        } catch (FileNotFoundException e) {
            //если возникают ошибки, просто возвращаем пустой объект
            return new ArrayList<Artist>();
        }
        String line;
        String cached ="";
        try {
            while ((line=bufferedReader.readLine())!=null) {
                cached += line + "\n";
            }
        } catch (IOException e) {
            return new ArrayList<Artist>();
        }
        //парсим
        Gson gson = new Gson();
        List<Artist> artists = gson.fromJson(cached,new TypeToken<List<Artist>>(){}.getType());
        if (artists==null) return new ArrayList<Artist>();
        else return artists;
    }

    public static void setCachedArtists(String rawJson){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(cache);
            fileOutputStream.write(rawJson.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            //???
            e.printStackTrace();
        }
    }
}