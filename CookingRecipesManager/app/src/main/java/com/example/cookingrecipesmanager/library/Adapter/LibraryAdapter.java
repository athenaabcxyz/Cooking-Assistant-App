package com.example.cookingrecipesmanager.library.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.Common.Constants;
import com.example.cookingrecipesmanager.CookingNote;
import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetailsFragment;
import com.example.cookingrecipesmanager.UserRecipe;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;




public class LibraryAdapter extends RecyclerView.Adapter<com.example.cookingrecipesmanager.library.Adapter.LibraryAdapter.LibraryViewHolder> {
    private Context context;
    private List<CookingNote> cookingNoteList;
    public void setData( List<CookingNote> list){
        this.cookingNoteList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public com.example.cookingrecipesmanager.library.Adapter.LibraryAdapter.LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_item_vertical, parent, false);
        context = parent.getContext();
        return new com.example.cookingrecipesmanager.library.Adapter.LibraryAdapter.LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.cookingrecipesmanager.library.Adapter.LibraryAdapter.LibraryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CookingNote note = cookingNoteList.get(position);
        if(note == null){
            return;
        }
        holder.title.setText(note.getTitle());
        holder.author.setText(note.getAuthor());
        holder.like.setText(String.valueOf(note.recipe.aggregateLikes)+ " like");
        holder.time.setText(String.valueOf(note.recipe.readyInMinutes)+ " min");
        Picasso.get().load(note.img).into(holder.img);
        if(note.recipe.userImage != null){
            Picasso.get().load(note.recipe.userImage).into(holder.userImage);
        }
        else {
            holder.userImage.setImageResource(R.drawable.ic_avatar_default);
        }
        try {
            holder.iFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.menu_popup,
                            popup.getMenu());
                    if(note.getiFavorites()){
                        popup.getMenu().findItem(R.id.Save).setTitle("UnSave");
                    }

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            cookingNoteList.remove(position);
                            notifyDataSetChanged();
                            return false;
                        }
                    });
                    popup.show();
                }
            });
//            holder.rootView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    ((MainActivity)context).getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note))
//                            .commitNow();
//                }
//            });

            holder.rootView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note.recipe,  Constants.LIBRARY_NAME))
                            .commitNow();
                }
            });

            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserRecipe.class);
                    intent.putExtra("userID", note.recipe.userID);
                    context.startActivity(intent);
                    ((MainActivity)context).finish();
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

    public class LibraryViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private TextView author;
        private ImageView img;
        private TextView like;
        private TextView time;
        private ImageView iFavorites;
        private ImageView userImage;
        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            title = itemView.findViewById(R.id.textTitle) ;
            author = itemView.findViewById(R.id.textAuthor);
            img = itemView.findViewById(R.id.imageView);
            iFavorites = itemView.findViewById(R.id.imageButton);
            like = itemView.findViewById(R.id.like) ;
            time = itemView.findViewById(R.id.time);
            userImage = itemView.findViewById(R.id.imageUser);
        }
    }
    public void sortAsc(){
        Collections.sort(cookingNoteList, new Comparator<CookingNote>() {
            @Override
            public int compare(CookingNote i0, CookingNote i1) {
                return i0.getTitle().compareToIgnoreCase(i1.getTitle());
            }
        });
        notifyDataSetChanged();
    }
    public void sortDes(){
        Collections.sort(cookingNoteList, new Comparator<CookingNote>() {
            @Override
            public int compare(CookingNote i0, CookingNote i1) {
                return i0.getTitle().compareToIgnoreCase(i1.getTitle());
            }
        });
        Collections.reverse(cookingNoteList);
        notifyDataSetChanged();
    }
}