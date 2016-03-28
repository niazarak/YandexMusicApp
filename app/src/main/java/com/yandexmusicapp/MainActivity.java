package com.yandexmusicapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yandexmusicapp.models.Artist;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView artistsList;
    ArtistAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistsList = (RecyclerView) findViewById(R.id.artistsView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        artistsList.setLayoutManager(llm);
        artistsList.addItemDecoration(new DividerItemDecoration(this));

        aa = new ArtistAdapter(this);

        YandexApi client = ServiceGenerator.createService(YandexApi.class);
        Call<List<Artist>> call = client.artists();
        call.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                aa.setArtists(response.body());
                artistsList.setAdapter(aa);
/*                for (Artist artist :response.body()) {
                    System.out.println(artist.getName());

                }*/
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {

            }
        });
    }
}