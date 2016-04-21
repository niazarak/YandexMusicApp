package com.yandexmusicapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yandexmusicapp.R;
import com.yandexmusicapp.models.Artist;

import static com.yandexmusicapp.Application.ARTIST;

public class ArtistActivity extends AppCompatActivity {

    ImageView mainCover;
    Artist artist;
    TextView description;
    TextView title;
    TextView repertoire;
    CollapsingToolbarLayout collapsing;
    FloatingActionButton fab;
    Toolbar toolbar;
     RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        artist = (Artist) getIntent().getExtras().getSerializable(ARTIST);

        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsing.setTitle(artist.getName());

        // ничего не смог придумать лучше, чем просто перейти на профиль артиста в яндекс музыке
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://music.yandex.ru/artist/"+artist.getId()+"/tracks"));
                startActivity(intent);
            }
        });


        mainCover = (ImageView) findViewById(R.id.mainCover);
        mainCover.setMinimumHeight(Resources.getSystem().getDisplayMetrics().widthPixels); //квадратик
        Picasso.with(this)
                .load(artist.getCover().getBig())
                .fit()
                .placeholder(R.drawable.artist)
                .into(mainCover, new Callback() {
                    @Override
                    public void onSuccess() {
                        Palette.from(((BitmapDrawable) mainCover.getDrawable()).getBitmap())
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        //palette получает картинку и отдает что-то типа "среднего цвета"
                                        // на ней, в который можно закрасить ActionBar и StatusBar, why not
                                        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                                        int primary = getResources().getColor(R.color.colorPrimary);
                                        collapsing.setContentScrimColor(palette.getMutedColor(primary));
                                        collapsing.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
                                        //updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);
                                        supportStartPostponedEnterTransition();
                                    }
                                });
                    }
                    @Override
                    public void onError() {

                    }
                });

        relativeLayout = (RelativeLayout) findViewById(R.id.artist_layout);

        if(artist.getLink()!=null){ //так как ссылки может не быть
            Button btt = new Button(this);
            relativeLayout.addView(btt);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btt.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW,R.id.description);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.setMargins(0,0,0,50);
            btt.setText("Перейти на сайт артиста");
            btt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(artist.getLink()));
                    startActivity(intent);
                }
            });
            btt.setLayoutParams(lp);
        }
        title = (TextView) relativeLayout.findViewById(R.id.genres);
        title.setText(artist.getImplodedGenres());
        repertoire = (TextView) relativeLayout.findViewById(R.id.repertoire);
        repertoire.setText(artist.getRepertoire());
        description = (TextView) relativeLayout.findViewById(R.id.description);
        description.setText(artist.getDescription());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :{
                finish();
            }
        }
        return true;
    }
}