package com.example.cookingrecipesmanager.recipetracker.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.CookingStep;

import java.util.ArrayList;

public class StepListAdapter extends RecyclerView.Adapter<StepListViewHolder> {

    Context mContext;
    ArrayList<CookingStep> stepList;

    public StepListAdapter(Context mContext, ArrayList<CookingStep> data) {
        this.mContext = mContext;
        this.stepList = data;
    }

    @NonNull
    @Override
    public StepListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.step_item, parent, false);
        return new StepListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepListViewHolder holder, int position) {
        String stepNamePlaceHolder="Step "+position+": "+stepList.get(position).stepName;
        holder.stepName.setText(stepNamePlaceHolder);
        switch (stepList.get(position).stepType)
        {
            case "Timer":
                holder.step.setCardBackgroundColor(ColorStateList.valueOf(0xff7709ea));
                break;
            case "Basic":
                holder.step.setCardBackgroundColor(ColorStateList.valueOf(0xff338ffd));
                break;
            case "Prepare":
                holder.step.setCardBackgroundColor(ColorStateList.valueOf(0xff09ea77));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }
}


class StepListViewHolder extends RecyclerView.ViewHolder {

    TextView stepName;
    CardView step;

    public StepListViewHolder(@NonNull View itemView) {
        super(itemView);
        stepName = itemView.findViewById(R.id.stepName);
        step=itemView.findViewById(R.id.step);
    }
}