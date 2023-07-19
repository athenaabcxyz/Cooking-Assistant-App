package com.example.cookingrecipesmanager.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder> {
    private List<ExtendedIngredient> mData;

    public IngredientAdapter(List<ExtendedIngredient> data) {
        mData = data;
//            mData.trimToSize();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false));
    }

    protected String formatIngredient(ExtendedIngredient item) {

        return item.original;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.mTextView.setText(formatIngredient(mData.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}



