package com.example.cookingrecipesmanager.search.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.CookingNote;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagSearchAdapter extends RecyclerView.Adapter<TagSearchAdapter.TagViewHolder> {
    private Context context;
    private List<Tag> tagList;

    private ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void onItemClick(Tag tag, TagViewHolder holder);
    }
    public TagSearchAdapter( ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<Tag> list){
        this.tagList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item_search, parent, false);
        context = parent.getContext();
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        if(tag == null){
            return;
        }
        holder.name.setText(tag.getName());
        if(tag.getClicked() == true){
            holder.name.setTextColor(context.getResources().getColor(R.color.white, null));
            holder.content.getBackground().setTint(context.getResources().getColor(R.color.blue, null));
        }
        else {
            holder.name.setTextColor(context.getResources().getColor(R.color.text, null));
            holder.content.getBackground().setTint(context.getResources().getColor(R.color.white, null));
        }
//        holder.img.setImageResource(tag.getImg());
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(tag, holder);
//                if(holder.name.getTextColors().getDefaultColor() == context.getResources().getColor(R.color.text, null)){
//                    holder.name.setTextColor(context.getResources().getColor(R.color.white, null));
//                    holder.content.getBackground().setTint(context.getResources().getColor(R.color.blue, null));
//                    tagListClicked.add(tag);
//                }
//                else {
//                    holder.name.setTextColor(context.getResources().getColor(R.color.text, null));
//                    holder.content.getBackground().setTint(context.getResources().getColor(R.color.white, null));
//                    tagListClicked.remove(tag);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(tagList!= null){
            return tagList.size();
        }
        return 0;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        private ImageView img;
        public LinearLayout content;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName) ;
            img = itemView.findViewById(R.id.imageIcon);
            content = itemView.findViewById(R.id.tag_content);

        }

        public ImageView getImg() {
            return img;
        }

        public void setImg(ImageView img) {
            this.img = img;
        }
    }

}
