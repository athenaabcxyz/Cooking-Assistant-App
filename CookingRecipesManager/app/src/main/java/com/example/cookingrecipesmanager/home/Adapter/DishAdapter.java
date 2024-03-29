package com.example.cookingrecipesmanager.home.Adapter;

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


public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private Context context;
    private List<Recipe> cookingNoteList;

    public void setData(List<Recipe> list) {
        this.cookingNoteList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dish_item, parent, false);
        context = parent.getContext();
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Recipe note = cookingNoteList.get(position);
        if (note == null) {
            return;
        }
        holder.title.setText(note.title);
        holder.author.setText(note.userName);
        holder.like.setText(String.valueOf(note.aggregateLikes) + " like");
        holder.time.setText(String.valueOf(note.readyInMinutes) + " min");
        Picasso.get().load(note.image).into(holder.img);
        if (note.userImage != null) {
            Picasso.get().load(note.image).into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.ic_avatar_default);
        }
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

            CookingNote note1 = new CookingNote(note, note.id, note.title, "Nguyen Hoang Nam", "", note.image, new Float("4.5"), true);

            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note, Constants.HOME_NAME))
                            .commitNow();
                }
            });

            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserRecipe.class);
                    intent.putExtra("userID", note.userID);
                    context.startActivity(intent);
                    ((MainActivity) context).finish();
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (cookingNoteList != null) {
            return cookingNoteList.size();
        }
        return 0;
    }

    public void sortAsc() {
        Collections.sort(cookingNoteList, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe i0, Recipe i1) {
                return i0.title.compareToIgnoreCase(i1.title);
            }
        });
        notifyDataSetChanged();
    }

    public void sortDes() {
        Collections.sort(cookingNoteList, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe i0, Recipe i1) {
                return i0.title.compareToIgnoreCase(i1.title);
            }
        });
        Collections.reverse(cookingNoteList);
        notifyDataSetChanged();
    }

    public class DishViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private TextView author;
        private TextView like;
        private TextView time;
        private ImageView img;
        private ImageView userImage;
        private ImageView iFavorites;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            title = itemView.findViewById(R.id.textTitle);
            author = itemView.findViewById(R.id.textAuthor);
            like = itemView.findViewById(R.id.like);
            time = itemView.findViewById(R.id.time);
            img = itemView.findViewById(R.id.imageView);
            iFavorites = itemView.findViewById(R.id.imageButton);
            userImage = itemView.findViewById(R.id.imageUser);
        }
    }
}