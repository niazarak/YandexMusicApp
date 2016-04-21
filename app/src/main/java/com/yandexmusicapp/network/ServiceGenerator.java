package com.yandexmusicapp.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.yandexmusicapp.utils.CacheUtils.setCache;

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
            @Override
            public Response intercept(Chain chain) throws IOException {
                // поднятие обработки ошибки наверх имплементировалось само
                Response response = chain.proceed(chain.request());
                String rawJson = response.body().string();
                //сохраняем
                setCache(rawJson);
                // делаем новый запрос
                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), rawJson)).build();
            }
        });
        Retrofit retrofit = builder.client(client.build()).build();
        return retrofit.create(serviceClass);
    }
}