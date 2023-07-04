package com.example.cookingrecipesmanager.home.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.Common.Constants;
import com.example.cookingrecipesmanager.CookingNote;
import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetailsFragment;
import com.example.cookingrecipesmanager.UserRecipe;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.TrendViewHolder> {
    private Context context;
    private List<Recipe> cookingNoteList;
    public void setData( List<Recipe> list){
        this.cookingNoteList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TrendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_item, parent, false);
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
        holder.like.setText(String.valueOf(note.aggregateLikes)+ " like");
        holder.time.setText(String.valueOf(note.readyInMinutes)+" min");
        Picasso.get().load(note.image).into(holder.img);
        if(note.userImage != null){
            Picasso.get().load(note.image).into(holder.userImage);
        }
        else {
            holder.userImage.setImageResource(R.drawable.ic_avatar_default);
        }
//        holder.evaluate.setText(note.getEvaluate().toString());
        try {
            holder.icon_more.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    Context wrapper = new ContextThemeWrapper(context, R.style.YOURSTYLE_PopupMenu);
                    PopupMenu popup = new PopupMenu(wrapper, v);
//                    if(note.getiFavorites()){
//                        popup.getMenu().findItem(R.id.Save).setTitle("UnSave");
//                    }

                    MenuBuilder builder = new MenuBuilder(wrapper);
                    MenuInflater inflater = new MenuInflater(wrapper);
                    inflater.inflate(R.menu.menu_popup, builder);
                    MenuPopupHelper popupHelper = new MenuPopupHelper(wrapper, builder, v);
                    popupHelper.setForceShowIcon(true);

//                    popup.inflate(R.menu.menu_popup);
//                    if(note.getiFavorites()){
//                        popup.getMenu().findItem(R.id.Save).setTitle("UnSave");
//                    }
//                    popup.show();
//                    if(note.getiFavorites()){
//                        builder.findItem(R.id.Save).setTitle("UnSave");
//                    }
                    popupHelper.show();

                    builder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
//                            if(note.getiFavorites()){
//                                Toast.makeText(wrapper, "abc", Toast.LENGTH_SHORT).show();
//                            }
                            return false;
                        }

                        @Override
                        public void onMenuModeChange(@NonNull MenuBuilder menu) {

                        }
                    });
                }
            });
            CookingNote note1 = new CookingNote(note, note.id,note.title, "Nguyen Hoang Nam", "", note.image, new Float("4.5"), true);

            holder.rootView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).getSupportFragmentManager().beginTransaction()
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

    public class TrendViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private TextView author;
        private TextView like;
        private TextView time;
        private ImageView img;
        private ImageView userImage;
        private ImageView icon_more;
        public TrendViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            title = itemView.findViewById(R.id.textTitle) ;
            author = itemView.findViewById(R.id.textAuthor);
            like = itemView.findViewById(R.id.like) ;
            time = itemView.findViewById(R.id.time);
            img = itemView.findViewById(R.id.imageView);
            icon_more = itemView.findViewById(R.id.imageButton);
            userImage = itemView.findViewById(R.id.imageUser);
        }
    }
}