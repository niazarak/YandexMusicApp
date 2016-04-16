package com.yandexmusicapp.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandexmusicapp.adapters.decorations.DividerItemDecoration;
import com.yandexmusicapp.R;
import com.yandexmusicapp.adapters.ArtistAdapter;
import com.yandexmusicapp.models.Artist;
import com.yandexmusicapp.network.ServiceGenerator;
import com.yandexmusicapp.network.YandexApi;

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
    SearchView searchView;
    MenuItem sortItem;
    ArtistAdapter aa;
    YandexApi client;
    RelativeLayout splash; // лейаут для пустого экрана (когда артисты не получены) *отныне он называется "сплэш"
    Call<List<Artist>> call; //объект запроса (его лучше объявлять глобально, чтобы переиспользовать)
    public static File cache;
    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //инициализация пути кэша
        cache = new File(getCacheDir(),"cache.txt");

        artistsList = (RecyclerView) findViewById(R.id.artistsView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        artistsList.setLayoutManager(llm);
        artistsList.addItemDecoration(new DividerItemDecoration(this));
        aa = new ArtistAdapter(this);

        splash = (RelativeLayout) findViewById(R.id.splash);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                aa.filterArtists(newText.toLowerCase());
                aa.sortArtists(false);
                return true;
            }
        });
        sortItem = menu.findItem(R.id.action_sort);
        sortItem.setIcon(android.R.drawable.ic_menu_sort_alphabetically);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        меняем кнопку на противоположную и сортируем
        if(item.getItemId()==R.id.action_sort){
            if(aa.sortedAlphabetically) sortItem.setIcon(android.R.drawable.ic_menu_sort_by_size);
            else sortItem.setIcon(android.R.drawable.ic_menu_sort_alphabetically);
            aa.sortArtists(true);
        }
        return true;
    }

    // onRefresh для swipeRefreshLayout
    @Override
    public void onRefresh() {
        client = ServiceGenerator.createService(YandexApi.class);
        call = client.artists();
        if(searchView!=null) {
            searchView.setQuery("", true);
        }
        // проверять соединение бесполезно
        call.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                splash.setVisibility(View.GONE);
                aa.setArtists(response.body());
                artistsList.setAdapter(aa);
                aa.sortArtists(false);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                //запрос не удался, но показывать сплэш рано
                // - нужно проверить, есть ли что-то в кэше
                splash.setVisibility(View.GONE);
                String cached = "";
                try {
                    // создадим ридер по пути кэша (который был инициализирован в onCreate())
                    //try with resources не работает на старых андройдах (minSdk version 10)
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(MainActivity.cache));
                    String line;
                    while ((line=bufferedReader.readLine())!=null) {
                        cached += line + "\n";
                    }
                    Gson gson = new Gson();
                    List<Artist> decodedArtists = gson.fromJson(cached,new TypeToken<List<Artist>>(){}.getType());
                    aa.setArtists(decodedArtists);
                    artistsList.setAdapter(aa);
                    aa.sortArtists(false);
                    Toast.makeText(getApplicationContext(),"Нет подключения к интернету, данные загружены из кэша",Toast.LENGTH_LONG).show();
                }
                catch (FileNotFoundException e){
                    Log.d(TAG, "onFailure: NOT FOUND");
                    //кэш не найден, теперь показываем сплэш
                    splash.setVisibility(View.VISIBLE);
                }
                catch (IOException e) {
                    //тут происходит что-то несуразное и невразумительное
                    //просто попросим наконец включить интернет
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Нет подключения к интернету, проверьте соединение",Toast.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}