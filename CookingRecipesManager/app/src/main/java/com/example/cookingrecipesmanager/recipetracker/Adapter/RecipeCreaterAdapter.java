package com.example.cookingrecipesmanager.recipetracker.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.cookingrecipesmanager.CookingStep;
import com.example.cookingrecipesmanager.R;

import java.util.ArrayList;

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
        holder.step.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addstep, null);

            // Set the content view of the popup window.
            PopupWindow popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            Spinner spinner = popupView.findViewById(R.id.spinner);
            Button buttonSave = popupView.findViewById(R.id.save_edit);
            Button buttonCancel = popupView.findViewById(R.id.cancel_edit);
            EditText description = popupView.findViewById(R.id.editTextTextMultiLine);
            EditText time = popupView.findViewById(R.id.editTextNumber);
            LinearLayout group_time = popupView.findViewById(R.id.group_time);
            group_time.setVisibility(View.GONE);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.step_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);
            switch (stepList.get(position).stepType) {
                case "Timer":
                    spinner.setSelection(1);

                    break;
                case "Basic":
                    spinner.setSelection(2);
                    break;
                case "Prepare":
                    spinner.setSelection(0);
                    break;
            }
            description.setText(stepList.get(position).stepIntruction);
            String timerBySecond = "" + stepList.get(position).timerBySecond;
            time.setText(timerBySecond);
            buttonSave.setOnClickListener(view1 -> {
                if (description.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Please input description.", Toast.LENGTH_SHORT).show();
                } else {
                    int timer = 0;
                    if (!time.getText().toString().equals(""))
                        timer = Integer.parseInt(time.getText().toString());
                    stepList.get(position).stepType = spinner.getSelectedItem().toString();
                    stepList.get(position).stepIntruction = description.getText().toString();
                    int timerSave = 0;
                    if (time.getText().toString().equals(""))
                        stepList.get(position).timerBySecond = timerSave;
                    else
                        stepList.get(position).timerBySecond = Integer.parseInt(time.getText().toString());
                    notifyDataSetChanged();
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                    if (spinner.getSelectedItem().toString().equals("Timer")) {
                        group_time.setVisibility(View.VISIBLE);
                    } else {
                        group_time.setVisibility(View.GONE);
                        time.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                    if (spinner.getSelectedItem().toString().equals("Timer")) {
                        group_time.setVisibility(View.VISIBLE);
                    } else {
                        group_time.setVisibility(View.GONE);
                        time.setText("");
                    }
                }
            });
            if (!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            RelativeLayout around = popupView.findViewById(R.id.around_popup_step);
            around.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
        });
        //delete item
        int stt = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    ImageView delete;
    CardView time_group;

    public RecipeCreaterViewHolder(@NonNull View itemView) {
        super(itemView);
        stepName = itemView.findViewById(R.id.stepName);
        step = itemView.findViewById(R.id.step);
        textTimer = itemView.findViewById(R.id.timer);
        delete = itemView.findViewById(R.id.step_item_delete);
        time_group = itemView.findViewById(R.id.time_group);
    }
}