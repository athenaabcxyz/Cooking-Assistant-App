package com.example.cookingrecipesmanager.recipetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.cookingrecipesmanager.CookingStep;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetail;
import com.example.cookingrecipesmanager.recipetracker.Adapter.StepListAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.TagListAdapter;

import java.util.ArrayList;

public class RecipeStepPreview extends AppCompatActivity {

    RecyclerView stepList;

    TextView textView;
    LinearLayoutManager linearLayoutManagerForStep;

    StepListAdapter stepListAdapter;

    RecipeDetail recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_preview);
        stepList=findViewById(R.id.stepList);
        textView=findViewById((R.id.recipeName));

        //Create data sample for testing
        recipe = new RecipeDetail();
        ArrayList<CookingStep> cookingSteps = new ArrayList<CookingStep>();
        cookingSteps.add(new CookingStep( "Prepare 4 cups of water.", "Prepare", 0));
        cookingSteps.add(new CookingStep( "Pour 4 cups of water into a pot and boil it for 2 minutes.", "Timer", 10));
        cookingSteps.add(new CookingStep( "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep( "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep( "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep( "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep( "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep( "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        recipe.CreateStepList(cookingSteps);
        recipe.recipeName="Boiled water";
        ArrayList<String> tagList = new ArrayList<String>();
        tagList.add("Boil");
        tagList.add("Water");
        recipe.CreateTagList(tagList);
        //End data sample creating


        linearLayoutManagerForStep = new LinearLayoutManager(RecipeStepPreview.this, LinearLayoutManager.VERTICAL, false);
        stepListAdapter = new StepListAdapter(RecipeStepPreview.this, recipe.cookingStepsList);
        stepList.setLayoutManager(linearLayoutManagerForStep);
        stepList.setAdapter(stepListAdapter);
        textView.setText(recipe.recipeName);


    }
}