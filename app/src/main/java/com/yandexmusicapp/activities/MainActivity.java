package com.yandexmusicapp.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yandexmusicapp.R;
import com.yandexmusicapp.adapters.ArtistAdapter;
import com.yandexmusicapp.adapters.decorations.DividerItemDecoration;
import com.yandexmusicapp.models.Artist;
import com.yandexmusicapp.network.ServiceGenerator;
import com.yandexmusicapp.network.YandexApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.yandexmusicapp.Application.POSITION;
import static com.yandexmusicapp.Application.QUERY;
import static com.yandexmusicapp.Application.SORT;
import static com.yandexmusicapp.utils.CacheUtils.getCachedArtists;
import static com.yandexmusicapp.utils.CacheUtils.setCache;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "MAIN ACTIVITY";
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    RecyclerView artistsList;
    SearchView searchView;
    MenuItem sortItem;
    ArtistAdapter aa;
    YandexApi client;
    FrameLayout emptyCache; // лейаут для пустого экрана (когда артисты не получены) *отныне он называется "сплэш"
    FrameLayout emptySearch; // лейаут для пустого экрана (когда артисты не получены) *отныне он называется "сплэш"
    Call<List<Artist>> call; //объект запроса (его лучше объявлять глобально, чтобы переиспользовать)
    int currentPosition = 0;
    String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistsList = (RecyclerView) findViewById(R.id.artistsView);
        linearLayoutManager = new LinearLayoutManager(this);
        artistsList.setLayoutManager(linearLayoutManager);
        artistsList.addItemDecoration(new DividerItemDecoration(this));
        aa = new ArtistAdapter(this);

        emptyCache = (FrameLayout) findViewById(R.id.empty_cache);
        emptySearch = (FrameLayout) findViewById(R.id.empty_search);
        client = ServiceGenerator.createService(YandexApi.class);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        initCachedArtists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                currentQuery = query;
                searchArtists();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //ужасный костыль
                //searchview не позволяет сабмитить пустую строку (нам это нужно, чтобы очистить фильтр)
                //*есть одна либа, дающая собственную имплементацию этого searchview, в которой все работает
                //но это довольно долговато и мутновато для такой фичи
                if (newText.equals("")) {
                    searchView.clearFocus();
                    currentQuery = "";
                    searchArtists();
                }
                return true;
            }
        });

        if (currentQuery!= null) {
            searchView.setIconified(false);
            searchView.clearFocus();
            searchView.setQuery(currentQuery,false);
            artistsList.getLayoutManager().scrollToPosition(currentPosition);
        }

        sortItem = menu.findItem(R.id.sort);
        if(aa.sortedAlphabetically) sortItem.setIcon(android.R.drawable.ic_menu_sort_alphabetically) ;
        else sortItem.setIcon(android.R.drawable.ic_menu_sort_by_size);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        меняем кнопку на противоположную и сортируем
        switch (item.getItemId()) {
            case R.id.sort: {
                if (aa.sortedAlphabetically)
                    sortItem.setIcon(android.R.drawable.ic_menu_sort_by_size);
                else sortItem.setIcon(android.R.drawable.ic_menu_sort_alphabetically);
                aa.sortArtists(true);
                currentPosition = 0;
                break;
            }
            case R.id.clear: {
                setCache("");
                Toast.makeText(MainActivity.this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return true;
    }

    // onRefresh для swipeRefreshLayout
    @Override
    public void onRefresh() {
        //очищаем строку поиска и убираем сплэш
        if(searchView!=null) {
            searchView.setQuery("", false);
            emptySearch.setVisibility(View.GONE);
        }
        initFreshArtists();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION,linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putBoolean(SORT,aa.sortedAlphabetically);
        outState.putString(QUERY,currentQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        aa.sortedAlphabetically = savedInstanceState.getBoolean(SORT);
        currentQuery = savedInstanceState.getString(QUERY);
        searchArtists();
        currentPosition = savedInstanceState.getInt(POSITION,0);
    }

    private void initFreshArtists(){
        // проверять соединение бесполезно
        call = client.artists();
        call.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                Toast.makeText(MainActivity.this, R.string.cache_invalidated, Toast.LENGTH_SHORT).show();
                emptyCache.setVisibility(View.GONE);
                aa.setArtists(response.body());
                artistsList.setAdapter(aa);
                searchArtists();
                artistsList.getLayoutManager().scrollToPosition(currentPosition);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
                //запрос не удался, проверяем, есть ли что-то в кэше
                initCachedArtists();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initCachedArtists(){
        emptyCache.setVisibility(View.GONE);
        aa.setArtists(getCachedArtists());
        searchArtists();
        artistsList.setAdapter(aa);
        if(aa.isAbsolutelyEmpty()) {
            //кэш пустой, показываем сплэш
            showEmptyCache();
        }
    }
    private void searchArtists(){
        aa.filterArtists(currentQuery.toLowerCase());
        artistsList.getLayoutManager().scrollToPosition(0);
        showEmptySearch();
    }

    private void showEmptySearch(){
        if(aa.isEmpty() && emptyCache.getVisibility()!=View.VISIBLE){
            emptySearch.setVisibility(View.VISIBLE);
        }
        else{
            emptySearch.setVisibility(View.GONE);
        }
    }
    private void showEmptyCache(){
        emptySearch.setVisibility(View.GONE);
        emptyCache.setVisibility(View.VISIBLE);
    }
}