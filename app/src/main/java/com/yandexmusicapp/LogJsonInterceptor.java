package com.yandexmusicapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LogJsonInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String rawJson = response.body().string();

        String s= "";
        try {

            Log.d(BuildConfig.APPLICATION_ID, String.valueOf(rawJson.length()));

            FileOutputStream fileOutputStream = new FileOutputStream(MainActivity.cache);
            fileOutputStream.write(rawJson.getBytes());
            fileOutputStream.close();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(MainActivity.cache));
            String line;
            while ((line=bufferedReader.readLine())!=null) {
                s += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(MainActivity.cache.getAbsolutePath());
        System.out.println(s.length());
        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), rawJson)).build();
    }
}