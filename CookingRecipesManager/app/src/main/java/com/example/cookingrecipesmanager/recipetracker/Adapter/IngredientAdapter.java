package com.example.cookingrecipesmanager.recipetracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapterViewHolder> {

    Context context;

    ArrayList<String> ingredientList;

    public IngredientAdapter(Context context, ArrayList<String> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_delete, parent, false);
        return new IngredientAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapterViewHolder holder, int position) {
        String data = ingredientList.get(position);
        holder.ingredient.setText(ingredientList.get(position));
        holder.number.setText(String.valueOf(position+1));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.remove(data);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}
class IngredientAdapterViewHolder extends RecyclerView.ViewHolder{

    TextView ingredient;
    TextView number;
    ImageView delete;
    public IngredientAdapterViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredient=itemView.findViewById(R.id.ingredient_name);
        number = itemView.findViewById(R.id.ingredient_number);
        delete = itemView.findViewById(R.id.ingredient_delete);
    }
}