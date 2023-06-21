package com.example.cookingrecipesmanager.recipetracker.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeCreater;
import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapterViewHolder> {

    Context context;

    ArrayList<ExtendedIngredient> ingredientList;

    public IngredientAdapter(Context context, ArrayList<ExtendedIngredient> ingredientList) {
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
        ExtendedIngredient data = ingredientList.get(position);
        holder.ingredient.setText(ingredientList.get(position).original);
        holder.number.setText(String.valueOf(position+1));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.remove(data);
                notifyDataSetChanged();
            }
        });
        holder.ingredientCard.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addingredients, null);

            // Set the content view of the popup window.
            PopupWindow popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);

            EditText unit = popupView.findViewById(R.id.unit);
            EditText quantity = popupView.findViewById(R.id.quantity);
            EditText ingredient = popupView.findViewById(R.id.ingredient);

            unit.setText(ingredientList.get(position).unit);
            String newString = ingredientList.get(position).amount+"";
            quantity.setText(newString);
            ingredient.setText(ingredientList.get(position).name);
            Button buttonSave = popupView.findViewById(R.id.save_edit);
            Button buttonCancel = popupView.findViewById(R.id.cancel_edit);
            ingredient.requestFocus();
            buttonSave.setOnClickListener(view1 -> {
                if (ingredient.getText().toString().equals("") || unit.getText().toString().equals("")) {
                    Toast.makeText(context, "Please input description.", Toast.LENGTH_SHORT).show();
                } else {
                    ingredientList.get(position).unit=unit.getText().toString();
                    ingredientList.get(position).amount=Double.parseDouble(quantity.getText().toString());
                    ingredientList.get(position).name=ingredient.getText().toString();
                    ingredientList.get(position).original= quantity.getText().toString()+" "+unit.getText().toString()+" of "+ingredient.getText().toString();
                    notifyDataSetChanged();
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            });
            if (!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            RelativeLayout around = popupView.findViewById(R.id.around_popup_ingredient);
            around.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}
class IngredientAdapterViewHolder extends RecyclerView.ViewHolder{

    TextView ingredient;
    CardView ingredientCard;
    TextView number;
    ImageView delete;
    public IngredientAdapterViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredientCard=itemView.findViewById(R.id.ingredientCardView);
        ingredient=itemView.findViewById(R.id.ingredient_name);
        number = itemView.findViewById(R.id.ingredient_number);
        delete = itemView.findViewById(R.id.ingredient_delete);
    }
}