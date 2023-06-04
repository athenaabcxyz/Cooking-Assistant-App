package com.example.cookingrecipesmanager.database.Model;

import java.util.ArrayList;

public class Recipe {
    public int aggregateLikes;
    public double pricePerServing;
    public ArrayList<ExtendedIngredient> extendedIngredients;
    public int id;
    public String title;
    public int readyInMinutes;
    public int servings;
    public String sourceUrl;
    public String image;
    public String imageType;
    public String summary;
    public ArrayList<String> cuisines;
    public ArrayList<String> dishTypes;
    public ArrayList<String> diets;
    public String instructions;
    public ArrayList<AnalyzedInstruction> analyzedInstructions;
}