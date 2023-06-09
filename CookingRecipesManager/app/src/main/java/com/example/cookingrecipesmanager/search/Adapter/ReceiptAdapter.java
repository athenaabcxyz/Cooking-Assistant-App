package com.example.cookingrecipesmanager.search.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.CookingNote;
import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetailsFragment;
import com.example.cookingrecipesmanager.Search;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.TrendViewHolder> {
    private Context context;
    private List<Recipe> cookingNoteList;
    public void setData( List<Recipe> list){
        this.cookingNoteList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TrendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_item_vertical, parent, false);
//        view.findViewById(R.id.receipt_item_vertical_wrapper);
        context = parent.getContext();
        return new TrendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendViewHolder holder, int position) {

        Recipe note = cookingNoteList.get(position);
        if(note == null){
            return;
        }

        holder.title.setText(note.title);
        holder.author.setText(note.userName);
        holder.like.setText(String.valueOf(note.aggregateLikes));
        holder.time.setText(String.valueOf(note.readyInMinutes));
        Picasso.get().load(note.image).into(holder.img);

        try {
            holder.iFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.menu_popup,
                            popup.getMenu());
//                    if(note.getiFavorites()){
//                        popup.getMenu().findItem(R.id.Save).setTitle("UnSave");
//                    }
                    popup.show();
                }
            });
            holder.rootView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("RECIPE", note);
                    context.startActivity(intent);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(cookingNoteList!= null){
            return cookingNoteList.size();
        }
        return 0;
    }

    public class TrendViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private TextView author;
        private ImageView img;
        private TextView like;
        private TextView time;
        private ImageView iFavorites;
        public TrendViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            title = itemView.findViewById(R.id.textTitle) ;
            author = itemView.findViewById(R.id.textAuthor);
            img = itemView.findViewById(R.id.imageView);
            iFavorites = itemView.findViewById(R.id.imageButton);
            like = itemView.findViewById(R.id.like) ;
            time = itemView.findViewById(R.id.time);
        }
    }

    public void sortAsc(){
        Collections.sort(cookingNoteList, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe i0, Recipe i1) {
                return i0.title.compareToIgnoreCase(i1.title);
            }
        });
        notifyDataSetChanged();
    }

    public void sortDes(){
        Collections.sort(cookingNoteList, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe i0, Recipe i1) {
                return i0.title.compareToIgnoreCase(i1.title);
            }
        });
        Collections.reverse(cookingNoteList);
        notifyDataSetChanged();
    }
}
