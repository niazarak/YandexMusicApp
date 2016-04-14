package com.yandexmusicapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yandexmusicapp.models.Artist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {
    //адаптер для ресайклера

    Context mainContext; // он нужен для Picasso
    ArrayList<Artist> allArtists; // так как механизм фильтрации работает по принципу удаления
    // ненужных элементов, то изначальные элементы нужно где-то хранить (чтобы было что фильтровать)
    ArrayList<Artist> filteredArtists; // нужен для вывода результата поиска (фильтрованный allArtists)
    Boolean sortedAlphabetically; //дадим пользователю всего два списка - сортирован либо по алфавиту,
    // либо по количеству песен (выводить чистый ответ с сервера,
    // в котором артисты находятся в хаотичном порядке - плохой UX)
    public ArtistAdapter(Context c){
        // инициализируем все поля
        this.sortedAlphabetically = true;
        this.allArtists = new ArrayList<>();
        this.filteredArtists = (ArrayList<Artist>) allArtists.clone();
        this.mainContext = c;
    }

    // вспомогательный сеттер для артистов
    public void setArtists(List<Artist> artists){
        this.allArtists = (ArrayList<Artist>) artists;
        this.filteredArtists = (ArrayList<Artist>) allArtists.clone(); // иначе работа с filteredArtists влияет на allArtists
        sortArtists(false); //сразу же сортируем
        notifyDataSetChanged();
    }

    public void sortArtists(boolean change){
        // этот метод используется для двух задач - 1) сортировать по нажатию кнопки (вверху которая)
        // 2) сортировать вывод любого результата
        // 1 требует изменения флага сортировки (sortedAlphabetically) и иконки на кнопке,
        // а 2 нет, поэтому вводим флаг изменения (change)

        if(change) {
            // смотрим на флаг сортировки и меняем на противоположный, сортируя при этом
            if (!sortedAlphabetically) {
                Collections.sort(this.filteredArtists, new Comparator<Artist>() {
                    @Override
                    public int compare(Artist lhs, Artist rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                sortedAlphabetically = true;
            } else {
                Collections.sort(this.filteredArtists, new Comparator<Artist>() {
                    @Override
                    public int compare(Artist lhs, Artist rhs) {
                        return rhs.getTracks().compareTo(lhs.getTracks());
                    }
                });
                sortedAlphabetically = false;
            }
        }else{
            // смотрим на флаг сортировки и сортируем по нему
            if (sortedAlphabetically) {
                Collections.sort(this.filteredArtists, new Comparator<Artist>() {
                    @Override
                    public int compare(Artist lhs, Artist rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            } else {
                Collections.sort(this.filteredArtists, new Comparator<Artist>() {
                    @Override
                    public int compare(Artist lhs, Artist rhs) {
                        return rhs.getTracks().compareTo(lhs.getTracks());
                    }
                });
            }
        }
        notifyDataSetChanged();
    }
    public void filterArtists(String query){
        // метод для поиска
        filteredArtists.clear();
        // логика довольно тривиальна - приводим запрос и имя артиста в lowercase и, если есть совпадение, выводим
        for (Artist artist: this.allArtists) {
            if(artist.getName().toLowerCase().contains(query)){
                filteredArtists.add(artist);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ArtistHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtistHolder holder, int position) {
        holder.setIsRecyclable(false);
        //находим артиста и выводим его
        final Artist artist = filteredArtists.get(position);
        holder.name.setText(artist.getName());
        holder.genres.setText(artist.getImplodedGenres());
        holder.repertoire.setText(artist.getRepertoire());

        //Picasso.with(mainContext).setIndicatorsEnabled(true);
        Picasso.with(mainContext)
                .load(artist.getCover().getSmall()) // вставляем маленькую обложку
                .placeholder(R.drawable.artist)
                .into(holder.cover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ArtistActivity.class);
                // передаем в следующий активити выбранного артиста
                intent.putExtra("ARTIST",artist);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return filteredArtists.size();
    }

    // класс для холдера
    class ArtistHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView name;
        TextView genres;
        TextView repertoire;

        public ArtistHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            name = (TextView) itemView.findViewById(R.id.name);
            genres = (TextView) itemView.findViewById(R.id.genres);
            repertoire = (TextView) itemView.findViewById(R.id.repertoire);
        }
    }
}
