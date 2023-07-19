package com.example.cookingrecipesmanager.details;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.R;

public class IngredientViewHolder extends RecyclerView.ViewHolder {
    public final TextView mTextView;

    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.ingredient_name);
    }

    public TextView getViewIngredientName() {
        return mTextView;
    }
}
