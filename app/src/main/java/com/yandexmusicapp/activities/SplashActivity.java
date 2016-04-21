package com.yandexmusicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yandexmusicapp.R;
import com.yandexmusicapp.models.Artist;
import com.yandexmusicapp.network.ServiceGenerator;
import com.yandexmusicapp.network.YandexApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // сплэш скрин
        // говорят, что это считается антипаттерном
        // но в данном случае без него трудно было обойтись
        YandexApi client = ServiceGenerator.createService(YandexApi.class);
        Call<List<Artist>> call = client.artists();

        //обновляем кэш
        // *так как сплэш привязан к запросу на сервер, то он будет почти незаметен

        call.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),R.string.cache_failed ,Toast.LENGTH_LONG).show();
                intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
