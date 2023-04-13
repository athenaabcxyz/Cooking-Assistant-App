package com.example.cookingrecipesmanager.recipetracker.Adapter;

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

public class StepListAdapter extends RecyclerView.Adapter<StepListViewHolder> {
    public int timeCounter;

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
        String stepNamePlaceHolder = "Step " + (position + 1) + ": \n" + stepList.get(position).stepIntruction;
        holder.stepName.setText(stepNamePlaceHolder);
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
                            holder.textTimer.setText(String.valueOf(timeCounter));
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
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }
}


class StepListViewHolder extends RecyclerView.ViewHolder {

    TextView stepName;
    CardView step;

    TextView textTimer;
    CheckBox checkBox;

    public StepListViewHolder(@NonNull View itemView) {
        super(itemView);
        stepName = itemView.findViewById(R.id.stepName);
        step=itemView.findViewById(R.id.step);
        checkBox=itemView.findViewById((R.id.stepStatus));
        textTimer=itemView.findViewById(R.id.timer);
    }
}