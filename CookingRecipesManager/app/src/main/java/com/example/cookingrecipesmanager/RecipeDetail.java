package com.example.cookingrecipesmanager;

import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;

import java.util.ArrayList;

public class RecipeDetail {


    public String recipeName;

    //List of step for cooking this recipe
    public ArrayList<CookingStep> cookingStepsList;

    //The tags of this recipe
    public ArrayList<String> tagList;

    public ArrayList<ExtendedIngredient> ingredientsList;



    public void CreateStepList(ArrayList<CookingStep> data) {
        cookingStepsList = data;
    }

    public void CreateTagList(ArrayList<String> data) {
        tagList = data;
    }

}
