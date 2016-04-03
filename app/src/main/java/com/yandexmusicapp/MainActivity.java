package com.yandexmusicapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandexmusicapp.models.Artist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "MAIN ACTIVITY";
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView artistsList;
    ArtistAdapter aa;
    YandexApi client;
    RelativeLayout splash;
    Call<List<Artist>> call;
    static File cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cache = new File(getCacheDir(),"cache.txt");

        artistsList = (RecyclerView) findViewById(R.id.artistsView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        artistsList.setLayoutManager(llm);
        artistsList.addItemDecoration(new DividerItemDecoration(this));
        aa = new ArtistAdapter(this);

        splash = (RelativeLayout) findViewById(R.id.splash);

        onRefresh();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);


    }

    @Override
    public void onRefresh() {
        client = ServiceGenerator.createService(YandexApi.class);
        call = client.artists();
        call.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                splash.setVisibility(View.GONE);
                aa.setArtists(response.body());
                artistsList.setAdapter(aa);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                splash.setVisibility(View.GONE);
                String cached = "";
                Log.d(TAG, "onFailure: FAIL");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(MainActivity.cache));
                    String line;
                    while ((line=bufferedReader.readLine())!=null) {
                        cached += line + "\n";
                    }
                    Log.d(TAG, "onFailure: "+cached.length());

                    Gson gson = new Gson();
                    List<Artist> decodedArtists = gson.fromJson(cached,new TypeToken<List<Artist>>(){}.getType());
                    Log.d(TAG, "onFailure: ARTISTS "+decodedArtists.size());
                    aa.setArtists(decodedArtists);
                    artistsList.setAdapter(aa);
                    Toast.makeText(getApplicationContext(),"Нет подключения к интернету, данные загружены из кэша",Toast.LENGTH_LONG).show();
                }
                catch (FileNotFoundException e){
                    Log.d(TAG, "onFailure: NOT FOUND");
                    splash.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}