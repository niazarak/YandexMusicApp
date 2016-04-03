package com.yandexmusicapp;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yandexmusicapp.models.Artist;

public class ArtistActivity extends AppCompatActivity {

    ImageView mainCover;
    Artist artist;
    TextView description;
    TextView title;
    TextView repertoire;
    CollapsingToolbarLayout collapsing;
    FloatingActionButton fab;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        artist = (Artist) getIntent().getExtras().getSerializable("ARTIST");

        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsing.setTitle(artist.getName());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://music.yandex.ru/artist/"+artist.getId()+"/tracks"));
                startActivity(intent);
            }
        });


        mainCover = (ImageView) findViewById(R.id.mainCover);
        mainCover.setMinimumHeight(Resources.getSystem().getDisplayMetrics().widthPixels);
        Picasso.with(this).load(artist.getCover().getBig()).fit().placeholder(R.drawable.artist).into(mainCover, new Callback() {
            @Override
            public void onSuccess() {
                Palette.from(((BitmapDrawable) mainCover.getDrawable()).getBitmap()).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
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

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.artist_layout);
        if(artist.getLink()!=null){
            Button btt = new Button(this);
            rl.addView(btt);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btt.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW,R.id.artist_description);
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
        title = (TextView) findViewById(R.id.artist_genres);
        title.setText(artist.getImplodedGenres());
        repertoire = (TextView) findViewById(R.id.artist_repertoire);
        repertoire.setText(artist.getRepertoire());
        description = (TextView) findViewById(R.id.artist_description);
        description.setText(artist.getDescription());
    }
}
