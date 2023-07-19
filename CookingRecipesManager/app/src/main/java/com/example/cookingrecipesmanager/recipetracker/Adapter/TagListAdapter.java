package com.example.cookingrecipesmanager.recipetracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.R;

import java.util.ArrayList;

public class TagListAdapter extends RecyclerView.Adapter<TagListViewHolder> {

    Context mContext;
    ArrayList<String> tagList;

    public TagListAdapter(Context mContext, ArrayList<String> data) {
        this.mContext = mContext;
        this.tagList = data;
    }

    @NonNull
    @Override
    public TagListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, parent, false);
        return new TagListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListViewHolder holder, int position) {
        holder.tagName.setText(tagList.get(position));
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }
}


class TagListViewHolder extends RecyclerView.ViewHolder {

    TextView tagName;


    public TagListViewHolder(@NonNull View itemView) {
        super(itemView);
        tagName = itemView.findViewById(R.id.textName);

    }
}