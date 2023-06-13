package com.example.cookingrecipesmanager.recipetracker.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.CookingStep;

import java.io.Console;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kotlinx.coroutines.Delay;

public class RecipeCreaterAdapter extends RecyclerView.Adapter<RecipeCreaterViewHolder> {
    public int timeCounter;

    Context mContext;
    ArrayList<CookingStep> stepList;

    public RecipeCreaterAdapter(Context mContext, ArrayList<CookingStep> data) {
        this.mContext = mContext;
        this.stepList = data;
    }

    @NonNull
    @Override
    public RecipeCreaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.step_item_delete, parent, false);
        return new RecipeCreaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCreaterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String stepNamePlaceHolder = "Step " + (position + 1) + ": \n" + stepList.get(position).stepIntruction;
        holder.stepName.setText(stepNamePlaceHolder);
        holder.time_group.setVisibility(View.GONE);
        holder.checkBox.setChecked(false);
        holder.stepName.setTextColor((ColorStateList.valueOf(0xFF3A3A3A)));
        switch (stepList.get(position).stepType) {
            case "Timer":
                holder.step.setCardBackgroundColor(ColorStateList.valueOf(0xff7709ea));
                holder.textTimer.setVisibility(View.GONE);
                holder.textTimer.setEnabled(false);

                break;
            case "Basic":
                holder.step.setCardBackgroundColor(ColorStateList.valueOf(0xff338ffd));
                holder.textTimer.setEnabled(false);
                holder.textTimer.setVisibility(View.GONE);
                break;
            case "Prepare":
                holder.step.setCardBackgroundColor(ColorStateList.valueOf(0xff09ea77));
                holder.textTimer.setEnabled(false);
                holder.textTimer.setVisibility(View.GONE);
                break;
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stepList.get(position).stepType.equals("Timer")) {
                    holder.time_group.setVisibility(View.VISIBLE);
                    holder.checkBox.setChecked(false);
                    holder.stepName.setTextColor((ColorStateList.valueOf(0xFF3A3A3A)));
                    holder.checkBox.setEnabled(false);
                    holder.checkBox.setVisibility(View.GONE);
                    holder.textTimer.setEnabled(true);
                    holder.textTimer.setVisibility(View.VISIBLE);
                    holder.textTimer.setText(String.valueOf(stepList.get(holder.getAdapterPosition()).timerBySecond));
                    final Handler handler = new Handler();
                    timeCounter = stepList.get(holder.getAdapterPosition()).timerBySecond;
                    new CountDownTimer(stepList.get(holder.getAdapterPosition()).timerBySecond * 1000L, 1000) {
                        public void onTick(long millisUntilFinished) {
                            int hours = timeCounter / 3600;
                            int minutes = (timeCounter % 3600) / 60;
                            int seconds = timeCounter % 60;

                            String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                            holder.textTimer.setText(timeString);
                            timeCounter = timeCounter - 1;

                        }

                        public void onFinish() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setTitle("Timer Step Notification");
                            builder.setMessage("Time's up! Get your dish or it will burn.");

                            // add a button
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    holder.checkBox.setEnabled(true);
                                    holder.checkBox.setVisibility(View.VISIBLE);
                                    holder.textTimer.setEnabled(false);
                                    holder.textTimer.setVisibility(View.GONE);
                                    holder.checkBox.setChecked(true);
                                    holder.stepName.setTextColor(ColorStateList.valueOf(0xff8b7e74));
                                    holder.time_group.setVisibility(View.GONE);
                                }
                            });

                            // create and show the alert dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }.start();


                }
                else {
                    if (holder.checkBox.isChecked()) {
                        holder.stepName.setTextColor(ColorStateList.valueOf(0xff8b7e74));
                    } else {
                        holder.stepName.setTextColor((ColorStateList.valueOf(0xFF3A3A3A)));
                    }
                }

            }
        });

        //delete item
        int stt = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(false);
                holder.stepName.setTextColor((ColorStateList.valueOf(0xFF3A3A3A)));
                stepList.remove(stt);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }
}


class RecipeCreaterViewHolder extends RecyclerView.ViewHolder {

    TextView stepName;
    CardView step;
    TextView textTimer;
    CheckBox checkBox;
    ImageView delete;
    CardView time_group;

    public RecipeCreaterViewHolder(@NonNull View itemView) {
        super(itemView);
        stepName = itemView.findViewById(R.id.stepName);
        step=itemView.findViewById(R.id.step);
        checkBox=itemView.findViewById((R.id.stepStatus));
        textTimer=itemView.findViewById(R.id.timer);
        delete= itemView.findViewById(R.id.step_item_delete);
        time_group=itemView.findViewById(R.id.time_group);
    }
}