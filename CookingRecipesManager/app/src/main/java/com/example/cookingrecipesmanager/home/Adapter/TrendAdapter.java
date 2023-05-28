package com.example.cookingrecipesmanager.home.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.cookingrecipesmanager.CookingNote;
import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetailsFragment;

import java.util.List;

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.TrendViewHolder> {
    private Context context;
    private List<CookingNote> cookingNoteList;
    public void setData( List<CookingNote> list){
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
        CookingNote note = cookingNoteList.get(position);
        if(note == null){
            return;
        }

        holder.title.setText(note.getTitle());
        holder.author.setText(note.getAuthor());
        holder.img.setImageResource(note.getImg());
//        holder.evaluate.setText(note.getEvaluate().toString());
        try {
            holder.iFavorites.setOnClickListener(new View.OnClickListener() {
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
                    if(note.getiFavorites()){
                        builder.findItem(R.id.Save).setTitle("UnSave");
                    }
                    popupHelper.show();

                    builder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                            if(note.getiFavorites()){
                                Toast.makeText(wrapper, "abc", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }

                        @Override
                        public void onMenuModeChange(@NonNull MenuBuilder menu) {

                        }
                    });
                }
            });
            holder.rootView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note))
                            .commitNow();
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
        private TextView evaluate;
        private ImageView iFavorites;
        public TrendViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            title = itemView.findViewById(R.id.textTitle) ;
            author = itemView.findViewById(R.id.textAuthor);
            img = itemView.findViewById(R.id.imageView);
            evaluate = itemView.findViewById(R.id.textView2);
            iFavorites = itemView.findViewById(R.id.imageButton);
        }
    }
}