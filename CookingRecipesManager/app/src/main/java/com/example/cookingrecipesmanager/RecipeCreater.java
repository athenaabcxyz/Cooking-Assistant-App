package com.example.cookingrecipesmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingrecipesmanager.CookingStep;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetail;
import com.example.cookingrecipesmanager.recipetracker.Adapter.RecipeCreaterAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.StepListAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.TagListAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class RecipeCreater extends AppCompatActivity {

    RecyclerView stepList;

    private PopupWindow popupWindow;
    TextView textView;
    EditText description;
    EditText time;
    Spinner spinner;
    LinearLayoutManager linearLayoutManagerForStep;
    Button buttonAddStep;
    Button buttonSave;
    Button buttonCancel;
    Button Save;
    Button Cancel;
    RecipeCreaterAdapter recipeCreaterAdapter;

    RecipeDetail recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        stepList = findViewById(R.id.stepList);
        textView = findViewById((R.id.recipeName));
        buttonAddStep = findViewById(R.id.addStep);
        Save =findViewById(R.id.save_recipe);
        Cancel =findViewById(R.id.cancel_recipe);

        buttonAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Create data sample for testing
        recipe = new RecipeDetail();
        ArrayList<CookingStep> cookingSteps = new ArrayList<CookingStep>();
        cookingSteps.add(new CookingStep("Prepare 4 cups of water.", "Prepare", 0));
        recipe.CreateStepList(cookingSteps);
        recipe.recipeName = "Boiled water";
        ArrayList<String> tagList = new ArrayList<String>();
        tagList.add("Boil");
        tagList.add("Water");
        recipe.CreateTagList(tagList);
        //End data sample creating

        linearLayoutManagerForStep = new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.VERTICAL, false);
        recipeCreaterAdapter = new RecipeCreaterAdapter(RecipeCreater.this, recipe.cookingStepsList);
        stepList.setLayoutManager(linearLayoutManagerForStep);
        stepList.setAdapter(recipeCreaterAdapter);
        textView.setText(recipe.recipeName);



        buttonAddStep.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addstep, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            spinner =popupView.findViewById(R.id.spinner);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            description =popupView.findViewById(R.id.editTextTextMultiLine);
            time=popupView.findViewById(R.id.editTextNumber);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeCreater.this, R.array.step_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);
            buttonSave.setOnClickListener(view1 -> {
                if(description.getText().toString().equals(""))
                {
                    Toast.makeText(RecipeCreater.this, "Please input description.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int timer = 0;
                    if(!time.getText().toString().equals(""))
                        timer=Integer.parseInt(time.getText().toString());
                    cookingSteps.add(new CookingStep(description.getText().toString(), spinner.getSelectedItem().toString(), timer));
                    recipeCreaterAdapter.notifyItemInserted(cookingSteps.size()-1);
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                }
            });
            if(!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        });



    }
}