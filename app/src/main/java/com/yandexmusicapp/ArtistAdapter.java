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
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    Context mainContext;
    ArrayList<Artist> artists;

    public ArtistAdapter(Context c){
        mainContext = c;
    }
    public void setArtists(List<Artist> artists){
        this.artists = (ArrayList<Artist>) artists;
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
        final Artist artist = artists.get(position);
        holder.name.setText(artist.getName());
        holder.genres.setText(artist.getImplodedGenres());
        holder.repertoire.setText(artist.getRepertoire());

        //Picasso.with(mainContext).setIndicatorsEnabled(true);
        Picasso.with(mainContext)
                .load(artist.getCover().getSmall())
                .placeholder(R.drawable.artist)
                .into(holder.cover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ArtistActivity.class);
                intent.putExtra("ARTIST",artist);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return artists.size();
    }

    class ArtistHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView name;
        TextView genres;
        TextView repertoire;

        public ArtistHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.listCover);
            name = (TextView) itemView.findViewById(R.id.listName);
            genres = (TextView) itemView.findViewById(R.id.listGenres);
            repertoire = (TextView) itemView.findViewById(R.id.listRepertoire);
        }
    }
}
