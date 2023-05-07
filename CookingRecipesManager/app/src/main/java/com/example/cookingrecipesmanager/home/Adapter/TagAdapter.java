package com.example.cookingrecipesmanager.home.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.Search;
import com.example.cookingrecipesmanager.Tag;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private List<Tag> tagList;
    public void setData( List<Tag> list){
        this.tagList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        if(tag == null){
            return;
        }

        holder.name.setText(tag.getName());
        if (tag.getImg() != 0) {
            holder.img.setImageResource(tag.getImg());
        } else {
            holder.img.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Search.class);
                intent.putExtra("query", tag.getName());
                v.getContext().startActivity(intent);
            }
        });
//        holder.evaluate.setText(note.getEvaluate().toString());
    }

    @Override
    public int getItemCount() {
        if(tagList!= null){
            return tagList.size();
        }
        return 0;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private TextView name;
        private ImageView img;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            name = itemView.findViewById(R.id.textName) ;
            img = itemView.findViewById(R.id.imageIcon);

        }
    }
}
