package com.example.cookingrecipesmanager.recipetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.cookingrecipesmanager.CookingStep;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetail;
import com.example.cookingrecipesmanager.recipetracker.Adapter.StepListAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.TagListAdapter;

import java.util.ArrayList;

public class RecipeStepPreview extends AppCompatActivity {

    RecyclerView stepList;
    RecyclerView tagList;

    LinearLayoutManager linearLayoutManagerForStep;
    LinearLayoutManager linearLayoutManagerForTag;
    StepListAdapter stepListAdapter;
    TagListAdapter tagListAdapter;
    RecipeDetail recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_preview);
        stepList=findViewById(R.id.stepList);
        tagList=findViewById(R.id.tagList);

        //Create data sample for testing
        recipe = new RecipeDetail();
        ArrayList<CookingStep> cookingSteps = new ArrayList<CookingStep>();
        cookingSteps.add(new CookingStep("Prepare Ingredient", "Prepare 4 cups of water.", "Prepare", 0));
        cookingSteps.add(new CookingStep("Boil water", "Pour 4 cups of water into a pot and boil it for 2 minutes.", "Timer", 120));
        cookingSteps.add(new CookingStep("Take the boiled water out", "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep("Take the boiled water out", "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep("Take the boiled water out", "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep("Take the boiled water out", "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep("Take the boiled water out", "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        cookingSteps.add(new CookingStep("Take the boiled water out", "Turn of the stove and pour the hot water into 4 cups.", "Basic", 0));
        recipe.CreateStepList(cookingSteps);
        ArrayList<String> tagList = new ArrayList<String>();
        tagList.add("Boil");
        tagList.add("Water");
        recipe.CreateTagList(tagList);
        //End data sample creating


        linearLayoutManagerForStep = new LinearLayoutManager(RecipeStepPreview.this, LinearLayoutManager.VERTICAL, false);
        stepListAdapter = new StepListAdapter(RecipeStepPreview.this, recipe.cookingStepsList);
        stepList.setLayoutManager(linearLayoutManagerForStep);
        stepList.setAdapter(stepListAdapter);

        linearLayoutManagerForTag=new LinearLayoutManager(RecipeStepPreview.this, LinearLayoutManager.HORIZONTAL, false);
        tagListAdapter=new TagListAdapter(RecipeStepPreview.this, recipe.tagList);
        this.tagList.setLayoutManager((linearLayoutManagerForTag));
        this.tagList.setAdapter(tagListAdapter);

    }
}