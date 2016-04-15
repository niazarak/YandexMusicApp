package com.yandexmusicapp;

import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    //вспомогательный класс для генерации ретрофит сервиса

    public static final String API_BASE_URL = "http://download.cdn.yandex.net";

    private static OkHttpClient.Builder client = new OkHttpClient.Builder();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        client.addInterceptor(new Interceptor() {
            // ответ с сервера используется не только для того, чтобы передать в адаптер списка
            // но и для занесения в кэш
            //
            // кроме того, метод Response.body() может возвращать либо спарсенный список,
            // либо raw string (если в YandexApi.java указать возвращаемое значение Call<ResponseBody>
            // или если использовать Response.raw().body())
            //
            // и при этом, по задумке разрабов Retrofit (Jake Wharton), использовать метод Response.body() или Response.raw().body()
            // можно только один раз (если вызвать response.body() 2 раза друг за другом, будет ошибка).
            //
            // но нужны-то оба! (стринг для кэша, а список для адаптера)
            // поэтому было два выхода - либо изначально возвращать raw string, а потом вручную парсить его,
            // либо перехватывать ответ, брать из него raw string, а после этого делать еще один запрос
            //
            // согласен, 2 не очень красивый способ, но после часов гуглежа, этот Jake Wharton сказал где-то на гитхабе, что
            // изначально возвращать стринг плохая идея, так как это противоречит задумке использования конвертеров
            // к тому же, при возвращении стринга пришлось бы писать кучу проверок на валидность
            //

            @Override
            public Response intercept(Chain chain) throws IOException {
                // поднятие обработки ошибки наверх имплементировалось само, так было задумано
                Response response = chain.proceed(chain.request());
                String rawJson = "";

                try {
                    rawJson = response.body().string();
                    FileOutputStream fileOutputStream = new FileOutputStream(MainActivity.cache);
                    fileOutputStream.write(rawJson.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    // для Тоста нужен контекст, которого нет
                    e.printStackTrace();
                }
                // делаем новый запрос
                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), rawJson)).build();
            }
        });
        Retrofit retrofit = builder.client(client.build()).build();
        return retrofit.create(serviceClass);
    }
}