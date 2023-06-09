package com.example.cookingrecipesmanager.details;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.Search;
import com.example.cookingrecipesmanager.Tag;

import java.io.Serializable;
import java.util.ArrayList;

public class DetailsTagAdapter extends RecyclerView.Adapter<DetailsTagAdapter.TagViewHolder> {

    protected ArrayList<Tag> tags;

    private AppCompatActivity activity;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        activity = (AppCompatActivity) recyclerView.getContext();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tags.get(position);
        holder.text.setText(tag.getName());
        holder.img.setVisibility(View.GONE);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Search.class);
                ArrayList<Tag> tags = new ArrayList<>();
                tags.add(new Tag(tag.getName(), true));
                intent.putExtra("tags", (Serializable) tags);
                activity.startActivity(intent);
            }
        });
    }

    public void setData(ArrayList<Tag> tags)
    {
        this.tags = tags;
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView text;
        public ImageView img;
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            text = itemView.findViewById(R.id.textName);
            img = itemView.findViewById(R.id.imageIcon);
        }
    }
}
